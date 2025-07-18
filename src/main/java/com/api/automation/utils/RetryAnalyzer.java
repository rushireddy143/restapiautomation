package com.api.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for failed test cases
 * Implements intelligent retry mechanism with configurable retry count
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = Integer.parseInt(
        System.getProperty("retry.count", "2")
    );
    
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("Retrying test '{}' - Attempt {} of {}", 
                result.getMethod().getMethodName(), retryCount, MAX_RETRY_COUNT);
            
            // Log the failure reason
            if (result.getThrowable() != null) {
                logger.warn("Retry reason: {}", result.getThrowable().getMessage());
            }
            
            return true;
        }
        
        logger.error("Test '{}' failed after {} attempts", 
            result.getMethod().getMethodName(), MAX_RETRY_COUNT);
        return false;
    }
    
    /**
     * Reset retry count for new test
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
    
    /**
     * Get current retry count
     */
    public int getCurrentRetryCount() {
        return retryCount;
    }
    
    /**
     * Get max retry count
     */
    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}
