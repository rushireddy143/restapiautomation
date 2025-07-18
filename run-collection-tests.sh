#!/bin/bash

echo "========================================"
echo "   POSTMAN COLLECTION AUTOMATION SUITE"
echo "========================================"
echo

ENV=${1:-dev}
echo "Environment: $ENV"
echo

echo "========================================"
echo "   1. COLLECTION SMOKE TESTS"
echo "========================================"
echo "Running smoke tests for all collection endpoints..."
mvn clean test -Dtest=CompleteCollectionTests#testGetUsersFromCollection,testCreateUserFromCollection,testDeleteUserFromCollection -Denv=$ENV -Dgroups=smoke,collection

echo
echo "========================================"
echo "   2. COMPLETE COLLECTION REGRESSION"
echo "========================================"
echo "Running all collection endpoints with full validation..."
mvn clean test -Dtest=CompleteCollectionTests -Denv=$ENV -Dgroups=regression,collection

echo
echo "========================================"
echo "   3. BDD COLLECTION SCENARIOS"
echo "========================================"
echo "Running Cucumber BDD scenarios for collection..."
mvn test -Dtest=CollectionTestRunner -Denv=$ENV

echo
echo "========================================"
echo "   4. COLLECTION WORKFLOW TESTS"
echo "========================================"
echo "Running end-to-end workflow tests..."
mvn test -Dtest=CollectionWorkflowTestRunner -Denv=$ENV

echo
echo "========================================"
echo "   5. ENHANCED FRAMEWORK TESTS"
echo "========================================"
echo "Running enhanced framework pattern tests..."
mvn test -Dtest=EnhancedUserAPITests -Denv=$ENV -Dgroups=enhanced

echo
echo "========================================"
echo "   6. PERFORMANCE TESTS (Optional)"
echo "========================================"
read -p "Run performance tests? (y/n): " runPerf
if [[ $runPerf == "y" || $runPerf == "Y" ]]; then
    echo "Running performance tests..."
    mvn test -Dtest=PerformanceTestSuite -Denv=$ENV -Dgroups=performance
fi

echo
echo "========================================"
echo "   7. SECURITY TESTS (Optional)"
echo "========================================"
read -p "Run security tests? (y/n): " runSec
if [[ $runSec == "y" || $runSec == "Y" ]]; then
    echo "Running security tests..."
    mvn test -Dtest=SecurityTestSuite -Denv=$ENV -Dgroups=security
fi

echo
echo "========================================"
echo "   8. GENERATING REPORTS"
echo "========================================"
echo "Generating Allure reports..."
allure generate target/allure-results --clean -o target/allure-reports

echo
echo "========================================"
echo "   COLLECTION AUTOMATION COMPLETED"
echo "========================================"
echo
echo "Reports generated:"
echo "- TestNG Reports: target/surefire-reports/index.html"
echo "- Allure Reports: target/allure-reports/index.html"
echo "- Cucumber Reports: target/cucumber-reports/"
echo
echo "To view Allure reports: allure open target/allure-reports"
echo

# Make the script executable
chmod +x run-collection-tests.sh
