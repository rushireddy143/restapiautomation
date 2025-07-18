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
        
        // Notification settings - Outlook Configuration
        EMAIL_RECIPIENTS = 'your-email@company.com,team@company.com'
        EMAIL_FROM = 'jenkins@company.com'
        OUTLOOK_SMTP_SERVER = 'smtp.office365.com'
        OUTLOOK_SMTP_PORT = '587'

        // Additional notification settings
        SLACK_CHANNEL = '#api-automation'
        TEAMS_WEBHOOK = credentials('teams-webhook-url')
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
        string(
            name: 'NOTIFICATION_EMAILS',
            defaultValue: '',
            description: 'Additional email addresses for notifications (comma-separated)'
        )
        booleanParam(
            name: 'SEND_PRE_EXECUTION_EMAIL',
            defaultValue: true,
            description: 'Send email notification before test execution starts'
        )
        booleanParam(
            name: 'SEND_DETAILED_REPORTS',
            defaultValue: true,
            description: 'Include detailed test results in email notifications'
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
        stage('Pre-Execution Notification') {
            when {
                params.SEND_PRE_EXECUTION_EMAIL
            }
            steps {
                script {
                    echo "📧 Sending pre-execution email notification..."

                    // Combine default and additional email recipients
                    def allRecipients = EMAIL_RECIPIENTS
                    if (params.NOTIFICATION_EMAILS && params.NOTIFICATION_EMAILS.trim() != '') {
                        allRecipients = "${EMAIL_RECIPIENTS},${params.NOTIFICATION_EMAILS}"
                    }

                    // Send pre-execution notification
                    emailext (
                        to: allRecipients,
                        subject: "🚀 API Test Execution STARTED - ${TEST_ENV} - Build #${BUILD_NUMBER}",
                        body: """
                            <html>
                            <head>
                                <style>
                                    body { font-family: Arial, sans-serif; margin: 20px; }
                                    .header { background-color: #4CAF50; color: white; padding: 15px; border-radius: 5px; }
                                    .content { padding: 20px; border: 1px solid #ddd; border-radius: 5px; margin-top: 10px; }
                                    .info-table { width: 100%; border-collapse: collapse; margin: 15px 0; }
                                    .info-table th, .info-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                                    .info-table th { background-color: #f2f2f2; }
                                    .footer { margin-top: 20px; font-size: 12px; color: #666; }
                                </style>
                            </head>
                            <body>
                                <div class="header">
                                    <h2>� API Test Execution Started</h2>
                                </div>

                                <div class="content">
                                    <p>Hello Team,</p>
                                    <p>The API automation test execution has been initiated with the following configuration:</p>

                                    <table class="info-table">
                                        <tr><th>Parameter</th><th>Value</th></tr>
                                        <tr><td><strong>Environment</strong></td><td>${TEST_ENV}</td></tr>
                                        <tr><td><strong>Test Suite</strong></td><td>${TEST_SUITE}</td></tr>
                                        <tr><td><strong>Build Number</strong></td><td>${BUILD_NUMBER}</td></tr>
                                        <tr><td><strong>Parallel Threads</strong></td><td>${PARALLEL_THREADS}</td></tr>
                                        <tr><td><strong>Test Groups</strong></td><td>${params.TEST_GROUPS}</td></tr>
                                        <tr><td><strong>Started At</strong></td><td>${new Date()}</td></tr>
                                        <tr><td><strong>Triggered By</strong></td><td>${BUILD_USER ?: 'System/Schedule'}</td></tr>
                                    </table>

                                    <p><strong>Execution Steps:</strong></p>
                                    <ul>
                                        <li>✅ Environment Setup</li>
                                        <li>⏳ Dependency Check</li>
                                        <li>⏳ Code Compilation</li>
                                        <li>⏳ Smoke Tests (if enabled)</li>
                                        <li>⏳ API Test Execution</li>
                                        <li>⏳ Report Generation</li>
                                    </ul>

                                    <p><strong>Monitor Progress:</strong></p>
                                    <p>🔗 <a href="${BUILD_URL}">View Build Progress</a></p>
                                    <p>📊 <a href="${BUILD_URL}console">View Console Output</a></p>

                                    <p>You will receive another notification once the execution is completed.</p>
                                </div>

                                <div class="footer">
                                    <p>This is an automated notification from Jenkins API Automation Pipeline.</p>
                                    <p>Jenkins Server: ${JENKINS_URL}</p>
                                </div>
                            </body>
                            </html>
                        """,
                        mimeType: 'text/html',
                        attachLog: false
                    )

                    echo "✅ Pre-execution notification sent to: ${allRecipients}"
                }
            }
        }

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

                // Combine default and additional email recipients
                def allRecipients = EMAIL_RECIPIENTS
                if (params.NOTIFICATION_EMAILS && params.NOTIFICATION_EMAILS.trim() != '') {
                    allRecipients = "${EMAIL_RECIPIENTS},${params.NOTIFICATION_EMAILS}"
                }

                // Get test statistics
                def testStats = getTestStatistics()

                // Send success notification
                emailext (
                    to: allRecipients,
                    subject: "✅ API Tests PASSED - ${TEST_ENV} - Build #${BUILD_NUMBER} - All ${testStats.total} Tests Successful",
                    body: generateSuccessEmailBody(testStats),
                    mimeType: 'text/html',
                    attachLog: false,
                    attachmentsPattern: 'target/test-summary.txt'
                )

                echo "✅ Success notification sent to: ${allRecipients}"
            }
        }
        
        failure {
            script {
                echo "❌ Pipeline failed!"

                // Combine default and additional email recipients
                def allRecipients = EMAIL_RECIPIENTS
                if (params.NOTIFICATION_EMAILS && params.NOTIFICATION_EMAILS.trim() != '') {
                    allRecipients = "${EMAIL_RECIPIENTS},${params.NOTIFICATION_EMAILS}"
                }

                // Get failure details
                def failureDetails = getFailureDetails()

                // Send failure notification
                emailext (
                    to: allRecipients,
                    subject: "❌ API Tests FAILED - ${TEST_ENV} - Build #${BUILD_NUMBER} - ${failureDetails.failedCount} Tests Failed",
                    body: generateFailureEmailBody(failureDetails),
                    mimeType: 'text/html',
                    attachLog: true,
                    attachmentsPattern: 'target/surefire-reports/TEST-*.xml,target/logs/*.log'
                )

                echo "❌ Failure notification sent to: ${allRecipients}"
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

    // Helper functions for email notifications

    def getTestStatistics() {
        def stats = [:]
        try {
            // Read TestNG results
            def testResults = sh(
                script: '''
                    if [ -f target/surefire-reports/testng-results.xml ]; then
                        grep -o "total=\"[0-9]*\"" target/surefire-reports/testng-results.xml | cut -d'"' -f2
                    else
                        echo "0"
                    fi
                ''',
                returnStdout: true
            ).trim()

            def failedResults = sh(
                script: '''
                    if [ -f target/surefire-reports/testng-results.xml ]; then
                        grep -o "failed=\"[0-9]*\"" target/surefire-reports/testng-results.xml | cut -d'"' -f2
                    else
                        echo "0"
                    fi
                ''',
                returnStdout: true
            ).trim()

            def skippedResults = sh(
                script: '''
                    if [ -f target/surefire-reports/testng-results.xml ]; then
                        grep -o "skipped=\"[0-9]*\"" target/surefire-reports/testng-results.xml | cut -d'"' -f2
                    else
                        echo "0"
                    fi
                ''',
                returnStdout: true
            ).trim()

            stats.total = testResults as Integer
            stats.failed = failedResults as Integer
            stats.skipped = skippedResults as Integer
            stats.passed = stats.total - stats.failed - stats.skipped

        } catch (Exception e) {
            stats.total = 0
            stats.passed = 0
            stats.failed = 0
            stats.skipped = 0
        }

        return stats
    }

    def getFailureDetails() {
        def details = [:]
        try {
            def stats = getTestStatistics()
            details.failedCount = stats.failed
            details.totalCount = stats.total
            details.passedCount = stats.passed
            details.skippedCount = stats.skipped

            // Get failed test names
            details.failedTests = sh(
                script: '''
                    if [ -d target/surefire-reports ]; then
                        grep -l "failures=\"[1-9]" target/surefire-reports/TEST-*.xml | head -5 | xargs -I {} basename {} .xml | sed 's/TEST-//'
                    else
                        echo "No test results found"
                    fi
                ''',
                returnStdout: true
            ).trim().split('\n').findAll { it.trim() != '' }

        } catch (Exception e) {
            details.failedCount = 0
            details.totalCount = 0
            details.failedTests = []
        }

        return details
    }

    def generateSuccessEmailBody(testStats) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background-color: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #4CAF50, #45a049); color: white; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 30px; }
                    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 20px; margin: 20px 0; }
                    .stat-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #4CAF50; }
                    .stat-number { font-size: 32px; font-weight: bold; color: #4CAF50; margin-bottom: 5px; }
                    .stat-label { color: #666; font-size: 14px; }
                    .info-table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    .info-table th, .info-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    .info-table th { background-color: #f2f2f2; font-weight: bold; }
                    .links { background: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .links a { color: #4CAF50; text-decoration: none; font-weight: bold; margin-right: 20px; }
                    .links a:hover { text-decoration: underline; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                    .success-icon { font-size: 48px; margin-bottom: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="success-icon">✅</div>
                        <h1>API Tests Completed Successfully!</h1>
                        <p>All tests passed in ${TEST_ENV} environment</p>
                    </div>

                    <div class="content">
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-number">${testStats.total}</div>
                                <div class="stat-label">Total Tests</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">${testStats.passed}</div>
                                <div class="stat-label">Passed</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">${testStats.failed}</div>
                                <div class="stat-label">Failed</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">${testStats.skipped}</div>
                                <div class="stat-label">Skipped</div>
                            </div>
                        </div>

                        <table class="info-table">
                            <tr><th>Parameter</th><th>Value</th></tr>
                            <tr><td><strong>Environment</strong></td><td>${TEST_ENV}</td></tr>
                            <tr><td><strong>Test Suite</strong></td><td>${TEST_SUITE}</td></tr>
                            <tr><td><strong>Build Number</strong></td><td>${BUILD_NUMBER}</td></tr>
                            <tr><td><strong>Duration</strong></td><td>${currentBuild.durationString}</td></tr>
                            <tr><td><strong>Parallel Threads</strong></td><td>${PARALLEL_THREADS}</td></tr>
                            <tr><td><strong>Completed At</strong></td><td>${new Date()}</td></tr>
                            <tr><td><strong>Success Rate</strong></td><td>${testStats.total > 0 ? Math.round((testStats.passed * 100) / testStats.total) : 0}%</td></tr>
                        </table>

                        <div class="links">
                            <h3>📊 View Detailed Reports:</h3>
                            <a href="${BUILD_URL}allure/">🔍 Allure Report</a>
                            <a href="${BUILD_URL}testReport/">📈 TestNG Report</a>
                            <a href="${BUILD_URL}console">📋 Console Output</a>
                            <a href="${BUILD_URL}artifact/">📁 Artifacts</a>
                        </div>

                        <p><strong>🎉 Congratulations!</strong> All API tests have been executed successfully. The application is ready for the next stage.</p>

                        <p><strong>Next Steps:</strong></p>
                        <ul>
                            <li>✅ Review test reports for detailed insights</li>
                            <li>✅ Consider promoting to next environment</li>
                            <li>✅ Update stakeholders on successful validation</li>
                        </ul>
                    </div>

                    <div class="footer">
                        <p>This is an automated notification from Jenkins API Automation Pipeline</p>
                        <p>Jenkins Server: ${JENKINS_URL} | Build: ${BUILD_URL}</p>
                        <p>Generated on: ${new Date()}</p>
                    </div>
                </div>
            </body>
            </html>
        """
    }

    def generateFailureEmailBody(failureDetails) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background-color: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #f44336, #d32f2f); color: white; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 30px; }
                    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 20px; margin: 20px 0; }
                    .stat-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; }
                    .stat-card.failed { border-left: 4px solid #f44336; }
                    .stat-card.passed { border-left: 4px solid #4CAF50; }
                    .stat-card.total { border-left: 4px solid #2196F3; }
                    .stat-number { font-size: 32px; font-weight: bold; margin-bottom: 5px; }
                    .stat-number.failed { color: #f44336; }
                    .stat-number.passed { color: #4CAF50; }
                    .stat-number.total { color: #2196F3; }
                    .stat-label { color: #666; font-size: 14px; }
                    .info-table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    .info-table th, .info-table td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                    .info-table th { background-color: #f2f2f2; font-weight: bold; }
                    .failed-tests { background: #ffebee; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #f44336; }
                    .links { background: #fff3e0; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .links a { color: #ff9800; text-decoration: none; font-weight: bold; margin-right: 20px; }
                    .links a:hover { text-decoration: underline; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                    .failure-icon { font-size: 48px; margin-bottom: 10px; }
                    .action-items { background: #e3f2fd; padding: 20px; border-radius: 8px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="failure-icon">❌</div>
                        <h1>API Tests Failed</h1>
                        <p>Test execution completed with failures in ${TEST_ENV} environment</p>
                    </div>

                    <div class="content">
                        <div class="stats-grid">
                            <div class="stat-card total">
                                <div class="stat-number total">${failureDetails.totalCount}</div>
                                <div class="stat-label">Total Tests</div>
                            </div>
                            <div class="stat-card passed">
                                <div class="stat-number passed">${failureDetails.passedCount}</div>
                                <div class="stat-label">Passed</div>
                            </div>
                            <div class="stat-card failed">
                                <div class="stat-number failed">${failureDetails.failedCount}</div>
                                <div class="stat-label">Failed</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">${failureDetails.skippedCount}</div>
                                <div class="stat-label">Skipped</div>
                            </div>
                        </div>

                        <table class="info-table">
                            <tr><th>Parameter</th><th>Value</th></tr>
                            <tr><td><strong>Environment</strong></td><td>${TEST_ENV}</td></tr>
                            <tr><td><strong>Test Suite</strong></td><td>${TEST_SUITE}</td></tr>
                            <tr><td><strong>Build Number</strong></td><td>${BUILD_NUMBER}</td></tr>
                            <tr><td><strong>Duration</strong></td><td>${currentBuild.durationString}</td></tr>
                            <tr><td><strong>Failed At</strong></td><td>${new Date()}</td></tr>
                            <tr><td><strong>Success Rate</strong></td><td>${failureDetails.totalCount > 0 ? Math.round((failureDetails.passedCount * 100) / failureDetails.totalCount) : 0}%</td></tr>
                        </table>

                        ${failureDetails.failedTests.size() > 0 ? """
                        <div class="failed-tests">
                            <h3>🔍 Failed Tests (Top ${Math.min(5, failureDetails.failedTests.size())}):</h3>
                            <ul>
                                ${failureDetails.failedTests.take(5).collect { "<li><strong>${it}</strong></li>" }.join('')}
                            </ul>
                            ${failureDetails.failedTests.size() > 5 ? "<p><em>... and ${failureDetails.failedTests.size() - 5} more. Check detailed reports for complete list.</em></p>" : ""}
                        </div>
                        """ : ""}

                        <div class="links">
                            <h3>🔍 Investigation Resources:</h3>
                            <a href="${BUILD_URL}console">📋 Console Logs</a>
                            <a href="${BUILD_URL}testReport/">📊 Test Report</a>
                            <a href="${BUILD_URL}allure/">🔍 Allure Report</a>
                            <a href="${BUILD_URL}artifact/">📁 Artifacts & Logs</a>
                        </div>

                        <div class="action-items">
                            <h3>🔧 Immediate Action Items:</h3>
                            <ol>
                                <li><strong>Review Console Logs:</strong> Check the console output for detailed error messages</li>
                                <li><strong>Analyze Failed Tests:</strong> Review the test report to understand failure patterns</li>
                                <li><strong>Check Environment:</strong> Verify ${TEST_ENV} environment is accessible and configured correctly</li>
                                <li><strong>Fix Issues:</strong> Address the root cause of failures</li>
                                <li><strong>Re-run Tests:</strong> Execute the pipeline again after fixes</li>
                            </ol>
                        </div>

                        <p style="color: #f44336;"><strong>⚠️ Attention Required:</strong> The API test execution has failed. Please investigate and resolve the issues before proceeding.</p>
                    </div>

                    <div class="footer">
                        <p>This is an automated notification from Jenkins API Automation Pipeline</p>
                        <p>Jenkins Server: ${JENKINS_URL} | Build: ${BUILD_URL}</p>
                        <p>Generated on: ${new Date()}</p>
                    </div>
                </div>
            </body>
            </html>
        """
    }
}
