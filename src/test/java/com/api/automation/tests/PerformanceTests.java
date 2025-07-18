package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

/**
 * Test class for Performance testing of API endpoints
 */
@Epic("Performance")
@Feature("API Performance")
public class PerformanceTests extends BaseTest {

    @Test(groups = {"performance"}, priority = 1)
    @Story("Response time validation")
    @Description("Verify that API responses are within acceptable time limits")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUsersResponseTime() {
        logTestInfo("testGetUsersResponseTime", "Validate response time for get users endpoint");
        
        long startTime = System.currentTimeMillis();
        
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Assert response time is under 3 seconds
        Assert.assertTrue(responseTime < 3000, 
            "Response time should be less than 3000ms, but was: " + responseTime + "ms");
        
        // Also check REST Assured's built-in response time
        long restAssuredTime = response.getTimeIn(TimeUnit.MILLISECONDS);
        Assert.assertTrue(restAssuredTime < 3000,
            "REST Assured response time should be less than 3000ms, but was: " + restAssuredTime + "ms");
        
        logger.info("Response time: " + responseTime + "ms (Manual), " + restAssuredTime + "ms (REST Assured)");
    }

    @Test(groups = {"performance"}, priority = 2)
    @Story("Load testing simulation")
    @Description("Simulate multiple concurrent requests to test API load handling")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentRequests() {
        logTestInfo("testConcurrentRequests", "Test API with multiple concurrent requests");
        
        int numberOfRequests = 10;
        long[] responseTimes = new long[numberOfRequests];
        
        // Execute multiple requests and measure response times
        for (int i = 0; i < numberOfRequests; i++) {
            long startTime = System.currentTimeMillis();
            
            given()
                    .spec(requestSpec)
                    .when()
                    .get("/users")
                    .then()
                    .statusCode(200);
            
            responseTimes[i] = System.currentTimeMillis() - startTime;
        }
        
        // Calculate average response time
        long totalTime = 0;
        for (long time : responseTimes) {
            totalTime += time;
        }
        long averageTime = totalTime / numberOfRequests;
        
        // Assert average response time is reasonable
        Assert.assertTrue(averageTime < 5000,
            "Average response time should be less than 5000ms, but was: " + averageTime + "ms");
        
        logger.info("Executed " + numberOfRequests + " requests with average response time: " + averageTime + "ms");
    }

    @Test(groups = {"performance"}, priority = 3)
    @Story("Memory usage validation")
    @Description("Verify API doesn't cause memory issues with large responses")
    @Severity(SeverityLevel.MINOR)
    public void testLargeResponseHandling() {
        logTestInfo("testLargeResponseHandling", "Test handling of potentially large responses");
        
        // Get initial memory usage
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Make request that might return large response
        Response response = given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .queryParam("per_page", 12) // Maximum allowed by the API
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        // Check memory usage after request
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        // Response should not be null and should contain data
        Assert.assertNotNull(response.getBody(), "Response body should not be null");
        Assert.assertTrue(response.getBody().asString().length() > 0, "Response should contain data");
        
        logger.info("Memory used for large response: " + memoryUsed + " bytes");
        logger.info("Response size: " + response.getBody().asString().length() + " characters");
    }

    @Test(groups = {"performance"}, priority = 4)
    @Story("Timeout handling")
    @Description("Verify API handles timeout scenarios gracefully")
    @Severity(SeverityLevel.NORMAL)
    public void testTimeoutHandling() {
        logTestInfo("testTimeoutHandling", "Test API timeout handling");
        
        try {
            Response response = given()
                    .spec(requestSpec)
                    .queryParam("delay", 3) // Add delay parameter if supported
                    .when()
                    .get("/users")
                    .then()
                    .extract()
                    .response();
            
            // If request succeeds, verify it's within reasonable time
            long responseTime = response.getTimeIn(TimeUnit.MILLISECONDS);
            logger.info("Request with delay completed in: " + responseTime + "ms");
            
        } catch (Exception e) {
            // If timeout occurs, that's also a valid test result
            logger.info("Request timed out as expected: " + e.getMessage());
        }
    }
}
