package com.api.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/**
 * Utility class for API response assertions and validations
 */
public class AssertionUtils {
    
    private static final Logger logger = LogManager.getLogger(AssertionUtils.class);
    
    /**
     * Validate response status code
     */
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
            "Expected status code: " + expectedStatusCode + ", but got: " + actualStatusCode);
        logger.info("Status code validation passed: " + actualStatusCode);
    }
    
    /**
     * Validate response contains specific field
     */
    public static void validateFieldExists(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(fieldValue, "Field '" + fieldPath + "' should exist in response");
        logger.info("Field existence validation passed: " + fieldPath);
    }
    
    /**
     * Validate response field has expected value
     */
    public static void validateFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        Assert.assertEquals(actualValue, expectedValue, 
            "Field '" + fieldPath + "' should have value: " + expectedValue + ", but got: " + actualValue);
        logger.info("Field value validation passed: " + fieldPath + " = " + actualValue);
    }
    
    /**
     * Validate response field is not null or empty
     */
    public static void validateFieldNotEmpty(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        Assert.assertNotNull(fieldValue, "Field '" + fieldPath + "' should not be null");
        
        if (fieldValue instanceof String) {
            Assert.assertFalse(((String) fieldValue).isEmpty(), 
                "Field '" + fieldPath + "' should not be empty");
        } else if (fieldValue instanceof List) {
            Assert.assertFalse(((List<?>) fieldValue).isEmpty(), 
                "Field '" + fieldPath + "' should not be empty list");
        }
        
        logger.info("Field not empty validation passed: " + fieldPath);
    }
    
    /**
     * Validate response time is within expected limit
     */
    public static void validateResponseTime(Response response, long maxTimeInMillis) {
        long actualTime = response.getTime();
        Assert.assertTrue(actualTime <= maxTimeInMillis, 
            "Response time should be <= " + maxTimeInMillis + "ms, but was: " + actualTime + "ms");
        logger.info("Response time validation passed: " + actualTime + "ms");
    }
    
    /**
     * Validate response contains all required fields
     */
    public static void validateRequiredFields(Response response, String... requiredFields) {
        for (String field : requiredFields) {
            validateFieldExists(response, field);
        }
        logger.info("All required fields validation passed: " + String.join(", ", requiredFields));
    }
    
    /**
     * Validate response content type
     */
    public static void validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        Assert.assertTrue(actualContentType.contains(expectedContentType), 
            "Expected content type to contain: " + expectedContentType + ", but got: " + actualContentType);
        logger.info("Content type validation passed: " + actualContentType);
    }
    
    /**
     * Validate response body is not empty
     */
    public static void validateResponseBodyNotEmpty(Response response) {
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body should not be null");
        Assert.assertFalse(responseBody.trim().isEmpty(), "Response body should not be empty");
        logger.info("Response body not empty validation passed");
    }
    
    /**
     * Validate array/list size in response
     */
    public static void validateArraySize(Response response, String arrayPath, int expectedSize) {
        List<Object> array = response.jsonPath().getList(arrayPath);
        Assert.assertNotNull(array, "Array at path '" + arrayPath + "' should exist");
        Assert.assertEquals(array.size(), expectedSize, 
            "Array size should be: " + expectedSize + ", but was: " + array.size());
        logger.info("Array size validation passed: " + arrayPath + " size = " + array.size());
    }
    
    /**
     * Validate array/list minimum size in response
     */
    public static void validateArrayMinSize(Response response, String arrayPath, int minSize) {
        List<Object> array = response.jsonPath().getList(arrayPath);
        Assert.assertNotNull(array, "Array at path '" + arrayPath + "' should exist");
        Assert.assertTrue(array.size() >= minSize, 
            "Array size should be >= " + minSize + ", but was: " + array.size());
        logger.info("Array minimum size validation passed: " + arrayPath + " size = " + array.size());
    }
    
    /**
     * Validate response headers
     */
    public static void validateHeader(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        Assert.assertNotNull(actualValue, "Header '" + headerName + "' should exist");
        Assert.assertEquals(actualValue, expectedValue, 
            "Header '" + headerName + "' should have value: " + expectedValue + ", but got: " + actualValue);
        logger.info("Header validation passed: " + headerName + " = " + actualValue);
    }
    
    /**
     * Validate error response structure
     */
    public static void validateErrorResponse(Response response, int expectedStatusCode) {
        validateStatusCode(response, expectedStatusCode);
        validateFieldExists(response, "error");
        validateFieldNotEmpty(response, "error");
        logger.info("Error response validation completed");
    }
    
    /**
     * Custom validation with lambda expression
     */
    public static void validateCustomCondition(Response response, String fieldPath, 
                                             java.util.function.Predicate<Object> condition, 
                                             String errorMessage) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        Assert.assertTrue(condition.test(fieldValue), errorMessage);
        logger.info("Custom validation passed for field: " + fieldPath);
    }
}
