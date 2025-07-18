package com.api.automation.base;

import com.api.automation.config.ConfigManager;
import com.api.automation.utils.SpecificationBuilder;
import com.api.automation.utils.ExtentReportManager;
import com.api.automation.utils.RetryAnalyzer;
import com.api.automation.utils.TestDataManager;
import com.api.automation.utils.DatabaseManager;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import org.testng.ITestResult;
import java.lang.reflect.Method;

/**
 * Enhanced base test class providing comprehensive setup and utilities for API tests
 * Includes reporting, retry mechanism, database support, and advanced logging
 */
public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected ConfigManager config;
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    protected ExtentTest extentTest;
    protected TestDataManager testDataManager;
    protected DatabaseManager databaseManager;

    // Thread-local for parallel execution
    private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    
    @BeforeClass
    public void baseSetup() {
        logger.info("Setting up base test configuration");
        config = ConfigManager.getInstance();
        
        // Set base URI
        RestAssured.baseURI = config.getBaseUrl();
        
        // Set default specifications
        requestSpec = SpecificationBuilder.getRequestSpecification();
        responseSpec = SpecificationBuilder.getResponseSpecification();
        
        logger.info("Base test setup completed for environment: " + config.getEnvironment());
    }
    
    @BeforeMethod
    public void beforeEachTest() {
        logger.info("Starting new test execution");
        // Reset specifications for each test
        requestSpec = SpecificationBuilder.getRequestSpecification();
        responseSpec = SpecificationBuilder.getResponseSpecification();
    }
    
    /**
     * Get request specification with authentication
     */
    protected RequestSpecification getAuthenticatedRequestSpec() {
        return SpecificationBuilder.getRequestSpecificationWithAuth();
    }
    
    /**
     * Get request specification with basic authentication
     */
    protected RequestSpecification getBasicAuthRequestSpec() {
        return SpecificationBuilder.getRequestSpecificationWithBasicAuth()
                .auth().basic(config.getUsername(), config.getPassword());
    }
    
    /**
     * Log test information
     */
    protected void logTestInfo(String testName, String description) {
        logger.info("=".repeat(50));
        logger.info("Test: " + testName);
        logger.info("Description: " + description);
        logger.info("=".repeat(50));
    }
}
