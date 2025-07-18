package com.api.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Extent Report Manager for comprehensive test reporting
 */
public class ExtentReportManager {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;
    
    /**
     * Initialize Extent Reports
     */
    public static void initializeReport() {
        if (extentReports == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = "target/extent-reports/API-Test-Report_" + timestamp + ".html";
            
            // Create directory if it doesn't exist
            File reportDir = new File("target/extent-reports");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInfo();
            
            logger.info("Extent Report initialized at: {}", reportPath);
        }
    }
    
    /**
     * Configure Spark Reporter
     */
    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("API Automation Test Report");
        sparkReporter.config().setReportName("REST API Test Execution Report");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setEncoding("utf-8");
    }
    
    /**
     * Set system information
     */
    private static void setSystemInfo() {
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name", System.getProperty("user.name"));
        extentReports.setSystemInfo("Environment", System.getProperty("env", "dev"));
        extentReports.setSystemInfo("Framework", "REST Assured + TestNG");
    }
    
    /**
     * Create test entry
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
        return test;
    }
    
    /**
     * Get current test
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Log info message
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
        logger.info(message);
    }
    
    /**
     * Log pass message
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
        logger.info("PASS: {}", message);
    }
    
    /**
     * Log fail message
     */
    public static void logFail(String message) {
        if (getTest() != null) {
            getTest().log(Status.FAIL, message);
        }
        logger.error("FAIL: {}", message);
    }
    
    /**
     * Log warning message
     */
    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, message);
        }
        logger.warn("WARNING: {}", message);
    }
    
    /**
     * Log skip message
     */
    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().log(Status.SKIP, message);
        }
        logger.warn("SKIP: {}", message);
    }
    
    /**
     * Add API request details
     */
    public static void logApiRequest(String method, String endpoint, String requestBody) {
        if (getTest() != null) {
            String requestDetails = String.format(
                "<b>API Request:</b><br>" +
                "Method: %s<br>" +
                "Endpoint: %s<br>" +
                "Request Body: <pre>%s</pre>",
                method, endpoint, requestBody != null ? requestBody : "No body"
            );
            getTest().log(Status.INFO, requestDetails);
        }
    }
    
    /**
     * Add API response details
     */
    public static void logApiResponse(int statusCode, String responseBody, long responseTime) {
        if (getTest() != null) {
            String responseDetails = String.format(
                "<b>API Response:</b><br>" +
                "Status Code: %d<br>" +
                "Response Time: %d ms<br>" +
                "Response Body: <pre>%s</pre>",
                statusCode, responseTime, responseBody != null ? responseBody : "No response body"
            );
            getTest().log(Status.INFO, responseDetails);
        }
    }
    
    /**
     * Flush reports
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("Extent Report flushed successfully");
        }
    }
    
    /**
     * Get report path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Clean up thread local
     */
    public static void removeTest() {
        extentTest.remove();
    }
}
