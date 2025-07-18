pipeline {
    agent any
    
    // Environment variables
    environment {
        MAVEN_HOME = tool 'Maven-3.8.1'
        JAVA_HOME = tool 'JDK-11'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
        
        // Test environment configuration
        TEST_ENV = "${params.ENVIRONMENT ?: 'dev'}"
        TEST_SUITE = "${params.TEST_SUITE ?: 'testng.xml'}"
        PARALLEL_THREADS = "${params.PARALLEL_THREADS ?: '3'}"
        
        // Notification settings
        SLACK_CHANNEL = '#api-automation'
        EMAIL_RECIPIENTS = 'team@company.com'
    }
    
    // Build parameters
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environment to run tests against'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['testng.xml', 'smoke-tests.xml', 'regression-tests.xml'],
            description: 'Test suite to execute'
        )
        choice(
            name: 'PARALLEL_THREADS',
            choices: ['1', '3', '5', '10'],
            description: 'Number of parallel threads for test execution'
        )
        booleanParam(
            name: 'SKIP_SMOKE_TESTS',
            defaultValue: false,
            description: 'Skip smoke tests and run full suite'
        )
        booleanParam(
            name: 'GENERATE_ALLURE_REPORT',
            defaultValue: true,
            description: 'Generate Allure test reports'
        )
        string(
            name: 'TEST_GROUPS',
            defaultValue: 'smoke,regression',
            description: 'TestNG groups to execute (comma-separated)'
        )
    }
    
    // Trigger configuration
    triggers {
        // Run daily at 2 AM
        cron('0 2 * * *')
        
        // Trigger on SCM changes (if using polling)
        pollSCM('H/15 * * * *')
    }
    
    // Build options
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timeout(time: 2, unit: 'HOURS')
        retry(1)
        skipStagesAfterUnstable()
        ansiColor('xterm')
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "🔄 Checking out source code..."
                    checkout scm
                    
                    // Set build display name
                    currentBuild.displayName = "#${BUILD_NUMBER} - ${TEST_ENV} - ${TEST_SUITE}"
                    currentBuild.description = "Environment: ${TEST_ENV}, Suite: ${TEST_SUITE}"
                }
            }
        }
        
        stage('Environment Setup') {
            steps {
                script {
                    echo "🔧 Setting up environment..."
                    
                    // Create necessary directories
                    sh '''
                        mkdir -p logs
                        mkdir -p target/allure-results
                        mkdir -p target/test-reports
                    '''
                    
                    // Display environment information
                    sh '''
                        echo "Java Version:"
                        java -version
                        echo "Maven Version:"
                        mvn -version
                        echo "Current Environment: ${TEST_ENV}"
                        echo "Test Suite: ${TEST_SUITE}"
                    '''
                }
            }
        }
        
        stage('Dependency Check') {
            steps {
                script {
                    echo "📦 Checking and installing dependencies..."
                    sh '''
                        mvn clean dependency:resolve -B
                        mvn dependency:tree > target/dependency-tree.txt
                    '''
                }
            }
        }
        
        stage('Compile & Validate') {
            steps {
                script {
                    echo "🔨 Compiling source code..."
                    sh 'mvn clean compile test-compile -B'
                }
            }
            post {
                failure {
                    echo "❌ Compilation failed!"
                    script {
                        currentBuild.result = 'FAILURE'
                    }
                }
            }
        }
        
        stage('Smoke Tests') {
            when {
                not { params.SKIP_SMOKE_TESTS }
            }
            steps {
                script {
                    echo "🚀 Running smoke tests..."
                    sh '''
                        mvn clean test \\
                            -Denv=${TEST_ENV} \\
                            -Dsuite=smoke-tests.xml \\
                            -Dgroups=smoke \\
                            -Dmaven.test.failure.ignore=true \\
                            -B
                    '''
                }
            }
            post {
                always {
                    // Archive smoke test results
                    archiveArtifacts artifacts: 'target/surefire-reports/*smoke*', allowEmptyArchive: true
                }
                failure {
                    echo "❌ Smoke tests failed! Stopping pipeline..."
                    script {
                        currentBuild.result = 'FAILURE'
                        error("Smoke tests failed - pipeline terminated")
                    }
                }
            }
        }
        
        stage('API Tests Execution') {
            parallel {
                stage('Functional Tests') {
                    steps {
                        script {
                            echo "🧪 Running functional API tests..."
                            sh '''
                                mvn clean test \\
                                    -Denv=${TEST_ENV} \\
                                    -Dsuite=${TEST_SUITE} \\
                                    -Dgroups=${TEST_GROUPS} \\
                                    -DthreadCount=${PARALLEL_THREADS} \\
                                    -Dmaven.test.failure.ignore=true \\
                                    -B
                            '''
                        }
                    }
                }
                
                stage('Performance Tests') {
                    when {
                        anyOf {
                            environment name: 'TEST_ENV', value: 'staging'
                            environment name: 'TEST_ENV', value: 'dev'
                        }
                    }
                    steps {
                        script {
                            echo "⚡ Running performance tests..."
                            sh '''
                                mvn clean test \\
                                    -Denv=${TEST_ENV} \\
                                    -Dgroups=performance \\
                                    -Dmaven.test.failure.ignore=true \\
                                    -B
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Security Tests') {
            when {
                anyOf {
                    environment name: 'TEST_ENV', value: 'staging'
                    environment name: 'TEST_ENV', value: 'prod'
                }
            }
            steps {
                script {
                    echo "🔒 Running security tests..."
                    sh '''
                        mvn clean test \\
                            -Denv=${TEST_ENV} \\
                            -Dgroups=security,negative \\
                            -Dmaven.test.failure.ignore=true \\
                            -B
                    '''
                }
            }
        }
        
        stage('Generate Reports') {
            parallel {
                stage('Allure Report') {
                    when {
                        params.GENERATE_ALLURE_REPORT
                    }
                    steps {
                        script {
                            echo "📊 Generating Allure reports..."
                            sh '''
                                mvn allure:report
                                mvn allure:aggregate
                            '''
                        }
                    }
                }
                
                stage('Test Results Analysis') {
                    steps {
                        script {
                            echo "📈 Analyzing test results..."
                            
                            // Parse test results
                            sh '''
                                # Count test results
                                find target/surefire-reports -name "*.xml" -exec grep -l "testcase" {} \\; | wc -l > target/total-tests.txt
                                find target/surefire-reports -name "*.xml" -exec grep -l "failure\\|error" {} \\; | wc -l > target/failed-tests.txt
                                
                                # Generate summary
                                echo "Test Execution Summary:" > target/test-summary.txt
                                echo "Environment: ${TEST_ENV}" >> target/test-summary.txt
                                echo "Suite: ${TEST_SUITE}" >> target/test-summary.txt
                                echo "Build: ${BUILD_NUMBER}" >> target/test-summary.txt
                                echo "Date: $(date)" >> target/test-summary.txt
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Quality Gates') {
            steps {
                script {
                    echo "🎯 Checking quality gates..."
                    
                    // Check test results and set build status
                    def testResults = sh(
                        script: 'find target/surefire-reports -name "TEST-*.xml" -exec grep -l "failures=\\"[1-9]" {} \\; | wc -l',
                        returnStdout: true
                    ).trim()
                    
                    if (testResults.toInteger() > 0) {
                        echo "⚠️ Some tests failed, marking build as unstable"
                        currentBuild.result = 'UNSTABLE'
                    }
                    
                    // Performance threshold check
                    sh '''
                        # Check for performance test failures
                        if grep -q "Response time.*exceeded" target/surefire-reports/*.txt 2>/dev/null; then
                            echo "⚠️ Performance thresholds exceeded"
                            exit 0
                        fi
                    '''
                }
            }
        }
        
        stage('Publish Results') {
            steps {
                script {
                    echo "📤 Publishing test results..."
                }
            }
            post {
                always {
                    // Publish TestNG results
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'TestNG Report',
                        reportTitles: 'API Test Results'
                    ])
                    
                    // Publish Allure results
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                    
                    // Archive artifacts
                    archiveArtifacts artifacts: '''
                        target/surefire-reports/**/*,
                        target/allure-results/**/*,
                        target/*.txt,
                        logs/*.log
                    ''', allowEmptyArchive: true
                    
                    // Publish JUnit test results
                    junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "🏁 Pipeline execution completed"
                
                // Clean workspace
                cleanWs()
            }
        }
        
        success {
            script {
                echo "✅ All tests passed successfully!"
                
                // Send success notification
                emailext (
                    to: "${EMAIL_RECIPIENTS}",
                    subject: "✅ API Tests PASSED - ${TEST_ENV} - Build #${BUILD_NUMBER}",
                    body: """
                        <h2>API Test Execution Successful</h2>
                        <p><strong>Environment:</strong> ${TEST_ENV}</p>
                        <p><strong>Test Suite:</strong> ${TEST_SUITE}</p>
                        <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>View Reports:</strong> <a href="${BUILD_URL}allure/">Allure Report</a></p>
                        
                        <p>All API tests executed successfully!</p>
                    """,
                    mimeType: 'text/html'
                )
            }
        }
        
        failure {
            script {
                echo "❌ Pipeline failed!"
                
                // Send failure notification
                emailext (
                    to: "${EMAIL_RECIPIENTS}",
                    subject: "❌ API Tests FAILED - ${TEST_ENV} - Build #${BUILD_NUMBER}",
                    body: """
                        <h2>API Test Execution Failed</h2>
                        <p><strong>Environment:</strong> ${TEST_ENV}</p>
                        <p><strong>Test Suite:</strong> ${TEST_SUITE}</p>
                        <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>View Logs:</strong> <a href="${BUILD_URL}console">Console Output</a></p>
                        <p><strong>View Reports:</strong> <a href="${BUILD_URL}allure/">Allure Report</a></p>
                        
                        <p style="color: red;">Please check the logs and fix the failing tests.</p>
                    """,
                    mimeType: 'text/html'
                )
            }
        }
        
        unstable {
            script {
                echo "⚠️ Some tests failed, build marked as unstable"
                
                // Send unstable notification
                emailext (
                    to: "${EMAIL_RECIPIENTS}",
                    subject: "⚠️ API Tests UNSTABLE - ${TEST_ENV} - Build #${BUILD_NUMBER}",
                    body: """
                        <h2>API Test Execution Unstable</h2>
                        <p><strong>Environment:</strong> ${TEST_ENV}</p>
                        <p><strong>Test Suite:</strong> ${TEST_SUITE}</p>
                        <p><strong>Build Number:</strong> ${BUILD_NUMBER}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <p><strong>View Reports:</strong> <a href="${BUILD_URL}allure/">Allure Report</a></p>
                        
                        <p style="color: orange;">Some tests failed. Please review the test results.</p>
                    """,
                    mimeType: 'text/html'
                )
            }
        }
        
        aborted {
            script {
                echo "🛑 Pipeline was aborted"
                
                emailext (
                    to: "${EMAIL_RECIPIENTS}",
                    subject: "🛑 API Tests ABORTED - ${TEST_ENV} - Build #${BUILD_NUMBER}",
                    body: "The API test pipeline was aborted during execution.",
                    mimeType: 'text/plain'
                )
            }
        }
    }
}
