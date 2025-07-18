#!/bin/bash

# Jenkins Setup Script for API Automation Pipeline
# This script sets up Jenkins with Docker Compose and configures the API automation pipeline

set -e

echo "🚀 Setting up Jenkins for API Automation Pipeline..."

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# Check prerequisites
print_header "Checking Prerequisites"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

print_status "Docker and Docker Compose are installed"

# Create necessary directories
print_header "Creating Directories"
mkdir -p logs
mkdir -p jenkins_home
mkdir -p allure-results
mkdir -p allure-reports

print_status "Directories created"

# Set permissions
print_header "Setting Permissions"
chmod 755 logs
chmod 755 jenkins_home
chmod 755 allure-results
chmod 755 allure-reports

print_status "Permissions set"

# Create environment file
print_header "Creating Environment Configuration"
cat > .env << EOF
# Jenkins Configuration
JENKINS_ADMIN_USER=admin
JENKINS_ADMIN_PASSWORD=admin123
JENKINS_AGENT_SECRET=your-secret-key-here

# Database Configuration
POSTGRES_USER=sonar
POSTGRES_PASSWORD=sonar
POSTGRES_DB=sonar

# Email Configuration
SMTP_HOST=smtp.company.com
SMTP_PORT=587
SMTP_USER=jenkins@company.com
SMTP_PASSWORD=your-smtp-password

# Notification Configuration
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
EMAIL_RECIPIENTS=team@company.com
EOF

print_status "Environment file created (.env)"

# Start Jenkins services
print_header "Starting Jenkins Services"
print_status "Starting Jenkins with Docker Compose..."

# Pull latest images
docker-compose pull

# Start services
docker-compose up -d

print_status "Jenkins services started"

# Wait for Jenkins to be ready
print_header "Waiting for Jenkins to Start"
echo "Waiting for Jenkins to be ready..."

max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -s -f http://localhost:8080/login > /dev/null 2>&1; then
        print_status "Jenkins is ready!"
        break
    else
        echo -n "."
        sleep 10
        ((attempt++))
    fi
done

if [ $attempt -eq $max_attempts ]; then
    print_error "Jenkins failed to start within 5 minutes"
    exit 1
fi

# Display access information
print_header "Access Information"
echo
echo "🎉 Jenkins Setup Complete!"
echo
echo "Access URLs:"
echo "  📊 Jenkins Dashboard: http://localhost:8080"
echo "  📈 Allure Reports: http://localhost:5050"
echo "  🔍 SonarQube: http://localhost:9000"
echo
echo "Default Credentials:"
echo "  👤 Username: admin"
echo "  🔐 Password: admin123"
echo
echo "Next Steps:"
echo "  1. Access Jenkins at http://localhost:8080"
echo "  2. Configure your Git repository credentials"
echo "  3. Update API endpoints in configuration files"
echo "  4. Run your first API automation pipeline"
echo

# Create sample job configuration
print_header "Creating Sample Job Configuration"
cat > jenkins-job-config.xml << 'EOF'
<?xml version='1.1' encoding='UTF-8'?>
<flow-definition plugin="workflow-job@2.40">
  <actions>
    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin="pipeline-model-definition@1.8.5"/>
    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin="pipeline-model-definition@1.8.5">
      <jobProperties/>
      <triggers/>
      <parameters/>
      <options/>
    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>
  </actions>
  <description>REST Assured API Automation Pipeline</description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.plugins.jira.JiraProjectProperty plugin="jira@3.7"/>
    <jenkins.model.BuildDiscarderProperty>
      <strategy class="hudson.tasks.LogRotator">
        <daysToKeep>30</daysToKeep>
        <numToKeep>30</numToKeep>
        <artifactDaysToKeep>-1</artifactDaysToKeep>
        <artifactNumToKeep>-1</artifactNumToKeep>
      </strategy>
    </jenkins.model.BuildDiscarderProperty>
    <org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
      <triggers>
        <hudson.triggers.TimerTrigger>
          <spec>0 2 * * *</spec>
        </hudson.triggers.TimerTrigger>
      </triggers>
    </org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty>
  </properties>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition" plugin="workflow-cps@2.92">
    <script>
      pipeline {
        agent any
        
        stages {
          stage('Checkout') {
            steps {
              git branch: 'main', url: 'https://github.com/your-repo/api-automation.git'
            }
          }
          
          stage('Test') {
            steps {
              sh 'mvn clean test -Denv=dev -Dgroups=smoke'
            }
          }
          
          stage('Report') {
            steps {
              allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
              publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'target/surefire-reports', reportFiles: 'index.html', reportName: 'Test Report'])
            }
          }
        }
      }
    </script>
    <sandbox>true</sandbox>
  </definition>
  <triggers/>
  <disabled>false</disabled>
</flow-definition>
EOF

print_status "Sample job configuration created (jenkins-job-config.xml)"

# Create Jenkins CLI helper script
print_header "Creating Jenkins CLI Helper"
cat > jenkins-cli.sh << 'EOF'
#!/bin/bash

# Jenkins CLI Helper Script
JENKINS_URL="http://localhost:8080"
JENKINS_USER="admin"
JENKINS_PASSWORD="admin123"

# Function to create a job
create_job() {
    local job_name=$1
    local config_file=$2
    
    curl -X POST "${JENKINS_URL}/createItem?name=${job_name}" \
         --user "${JENKINS_USER}:${JENKINS_PASSWORD}" \
         --data-binary @"${config_file}" \
         -H "Content-Type: application/xml"
}

# Function to trigger a build
trigger_build() {
    local job_name=$1
    
    curl -X POST "${JENKINS_URL}/job/${job_name}/build" \
         --user "${JENKINS_USER}:${JENKINS_PASSWORD}"
}

# Function to get build status
get_build_status() {
    local job_name=$1
    local build_number=$2
    
    curl -s "${JENKINS_URL}/job/${job_name}/${build_number}/api/json" \
         --user "${JENKINS_USER}:${JENKINS_PASSWORD}" | \
         jq -r '.result'
}

# Main menu
case "$1" in
    "create")
        create_job "$2" "$3"
        ;;
    "build")
        trigger_build "$2"
        ;;
    "status")
        get_build_status "$2" "$3"
        ;;
    *)
        echo "Usage: $0 {create|build|status}"
        echo "  create <job_name> <config_file>"
        echo "  build <job_name>"
        echo "  status <job_name> <build_number>"
        ;;
esac
EOF

chmod +x jenkins-cli.sh

print_status "Jenkins CLI helper created (jenkins-cli.sh)"

# Create management scripts
print_header "Creating Management Scripts"

# Stop script
cat > stop-jenkins.sh << 'EOF'
#!/bin/bash
echo "Stopping Jenkins services..."
docker-compose down
echo "Jenkins services stopped"
EOF

chmod +x stop-jenkins.sh

# Restart script
cat > restart-jenkins.sh << 'EOF'
#!/bin/bash
echo "Restarting Jenkins services..."
docker-compose down
docker-compose up -d
echo "Jenkins services restarted"
EOF

chmod +x restart-jenkins.sh

# Logs script
cat > view-logs.sh << 'EOF'
#!/bin/bash
echo "Viewing Jenkins logs..."
docker-compose logs -f jenkins
EOF

chmod +x view-logs.sh

print_status "Management scripts created"

# Final message
print_header "Setup Complete"
echo
echo "📝 Files created:"
echo "  - Jenkinsfile (Pipeline definition)"
echo "  - jenkins.yaml (Configuration as Code)"
echo "  - docker-compose.yml (Docker services)"
echo "  - jenkins-plugins.txt (Required plugins)"
echo "  - .env (Environment configuration)"
echo "  - jenkins-job-config.xml (Sample job)"
echo "  - jenkins-cli.sh (CLI helper)"
echo "  - Management scripts (stop, restart, view logs)"
echo
echo "🎯 To get started:"
echo "  1. Update .env file with your configuration"
echo "  2. Update jenkins.yaml with your settings"
echo "  3. Access Jenkins at http://localhost:8080"
echo "  4. Create your first API automation job"
echo
echo "📚 Documentation:"
echo "  - Check the README.md for detailed instructions"
echo "  - Review the Jenkinsfile for pipeline configuration"
echo "  - Customize jenkins.yaml for your needs"
echo
print_status "Jenkins setup completed successfully! 🚀"
