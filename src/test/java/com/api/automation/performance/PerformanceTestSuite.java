package com.api.automation.performance;

import com.api.automation.base.BaseTest;
import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.utils.ExtentReportManager;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * Performance testing suite for API endpoints
 * Tests response times, throughput, and concurrent user scenarios
 */
public class PerformanceTestSuite extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(PerformanceTestSuite.class);
    
    @Test(groups = {"performance"}, priority = 1)
    public void testSingleUserResponseTime() {
        logger.info("Testing single user response time");
        ExtentReportManager.logInfo("Starting single user response time test");
        
        long startTime = System.currentTimeMillis();
        
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .get();
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertTrue(responseTime < 3000, 
                        "Response time should be less than 3 seconds. Actual: " + responseTime + "ms");
        
        ExtentReportManager.logPass("Single user response time: " + responseTime + "ms");
        logger.info("Single user response time test completed: {}ms", responseTime);
    }
    
    @Test(groups = {"performance"}, priority = 2)
    public void testConcurrentUsers() {
        logger.info("Testing concurrent users performance");
        ExtentReportManager.logInfo("Starting concurrent users performance test");
        
        int numberOfUsers = 10;
        int numberOfRequestsPerUser = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        
        List<CompletableFuture<PerformanceResult>> futures = new ArrayList<>();
        
        // Create concurrent user scenarios
        for (int user = 0; user < numberOfUsers; user++) {
            final int userId = user + 1;
            CompletableFuture<PerformanceResult> future = CompletableFuture.supplyAsync(() -> {
                return executeUserScenario(userId, numberOfRequestsPerUser);
            }, executor);
            futures.add(future);
        }
        
        // Collect results
        List<PerformanceResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        
        executor.shutdown();
        
        // Analyze results
        analyzePerformanceResults(results, numberOfUsers, numberOfRequestsPerUser);
        
        ExtentReportManager.logPass("Concurrent users test completed successfully");
        logger.info("Concurrent users performance test completed");
    }
    
    @Test(groups = {"performance"}, priority = 3)
    public void testLoadTesting() {
        logger.info("Starting load testing");
        ExtentReportManager.logInfo("Starting load testing with high request volume");
        
        int totalRequests = 100;
        int concurrentThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
        
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<Response>> futures = IntStream.range(0, totalRequests)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    return ApiRequestBuilder.create()
                            .withEndpoint("/api/users")
                            .withQueryParam("page", String.valueOf((i % 2) + 1))
                            .get();
                }, executor))
                .toList();
        
        List<Response> responses = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        executor.shutdown();
        
        // Analyze load test results
        analyzeLoadTestResults(responses, totalTime, totalRequests);
        
        ExtentReportManager.logPass("Load testing completed successfully");
        logger.info("Load testing completed");
    }
    
    @Test(groups = {"performance"}, priority = 4)
    public void testStressTesting() {
        logger.info("Starting stress testing");
        ExtentReportManager.logInfo("Starting stress testing to find breaking point");
        
        int maxUsers = 50;
        int requestsPerUser = 3;
        List<PerformanceMetrics> stressResults = new ArrayList<>();
        
        // Gradually increase load
        for (int users = 10; users <= maxUsers; users += 10) {
            logger.info("Testing with {} concurrent users", users);
            
            ExecutorService executor = Executors.newFixedThreadPool(users);
            long startTime = System.currentTimeMillis();
            
            List<CompletableFuture<Long>> futures = IntStream.range(0, users)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        long userStartTime = System.currentTimeMillis();
                        for (int req = 0; req < requestsPerUser; req++) {
                            ApiRequestBuilder.create()
                                    .withEndpoint("/api/users")
                                    .withQueryParam("page", "1")
                                    .get();
                        }
                        return System.currentTimeMillis() - userStartTime;
                    }, executor))
                    .toList();
            
            List<Long> userTimes = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
            
            long totalTime = System.currentTimeMillis() - startTime;
            executor.shutdown();
            
            // Calculate metrics
            double avgResponseTime = userTimes.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            
            double throughput = (users * requestsPerUser * 1000.0) / totalTime;
            
            PerformanceMetrics metrics = new PerformanceMetrics(users, avgResponseTime, throughput, totalTime);
            stressResults.add(metrics);
            
            ExtentReportManager.logInfo(String.format(
                    "Users: %d, Avg Response Time: %.2fms, Throughput: %.2f req/sec",
                    users, avgResponseTime, throughput));
            
            // Check if performance is degrading significantly
            if (avgResponseTime > 10000) { // 10 seconds threshold
                logger.warn("Performance degradation detected at {} users", users);
                break;
            }
        }
        
        // Report stress test results
        reportStressTestResults(stressResults);
        
        ExtentReportManager.logPass("Stress testing completed");
        logger.info("Stress testing completed");
    }
    
    @Test(groups = {"performance"}, priority = 5)
    public void testEnduranceTesting() {
        logger.info("Starting endurance testing");
        ExtentReportManager.logInfo("Starting endurance testing for sustained load");
        
        int concurrentUsers = 5;
        int durationMinutes = 2; // Reduced for demo purposes
        int requestInterval = 5000; // 5 seconds between requests
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        long testDuration = durationMinutes * 60 * 1000; // Convert to milliseconds
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<EnduranceResult>> futures = new ArrayList<>();
        
        for (int user = 0; user < concurrentUsers; user++) {
            final int userId = user;
            CompletableFuture<EnduranceResult> future = CompletableFuture.supplyAsync(() -> {
                return runEnduranceUser(userId, testDuration, requestInterval);
            }, executor);
            futures.add(future);
        }
        
        List<EnduranceResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        
        executor.shutdown();
        
        // Analyze endurance results
        analyzeEnduranceResults(results, durationMinutes);
        
        ExtentReportManager.logPass("Endurance testing completed successfully");
        logger.info("Endurance testing completed");
    }
    
    // Helper methods
    
    private PerformanceResult executeUserScenario(int userId, int numberOfRequests) {
        List<Long> responseTimes = new ArrayList<>();
        List<Integer> statusCodes = new ArrayList<>();
        
        for (int i = 0; i < numberOfRequests; i++) {
            long startTime = System.currentTimeMillis();
            
            Response response = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withQueryParam("page", String.valueOf((i % 2) + 1))
                    .get();
            
            long responseTime = System.currentTimeMillis() - startTime;
            responseTimes.add(responseTime);
            statusCodes.add(response.getStatusCode());
        }
        
        return new PerformanceResult(userId, responseTimes, statusCodes);
    }
    
    private void analyzePerformanceResults(List<PerformanceResult> results, int numberOfUsers, int requestsPerUser) {
        double totalAvgResponseTime = results.stream()
                .mapToDouble(PerformanceResult::getAverageResponseTime)
                .average()
                .orElse(0.0);
        
        long totalRequests = numberOfUsers * requestsPerUser;
        long successfulRequests = results.stream()
                .mapToLong(PerformanceResult::getSuccessfulRequests)
                .sum();
        
        double successRate = (successfulRequests * 100.0) / totalRequests;
        
        ExtentReportManager.logInfo(String.format(
                "Concurrent Users Performance Results:\n" +
                "Users: %d\n" +
                "Total Requests: %d\n" +
                "Successful Requests: %d\n" +
                "Success Rate: %.2f%%\n" +
                "Average Response Time: %.2fms",
                numberOfUsers, totalRequests, successfulRequests, successRate, totalAvgResponseTime));
        
        Assert.assertTrue(successRate >= 95.0, "Success rate should be at least 95%");
        Assert.assertTrue(totalAvgResponseTime < 5000, "Average response time should be less than 5 seconds");
    }
    
    private void analyzeLoadTestResults(List<Response> responses, long totalTime, int totalRequests) {
        long successfulRequests = responses.stream()
                .mapToInt(Response::getStatusCode)
                .filter(code -> code >= 200 && code < 300)
                .count();
        
        double successRate = (successfulRequests * 100.0) / totalRequests;
        double throughput = (totalRequests * 1000.0) / totalTime;
        
        double avgResponseTime = responses.stream()
                .mapToLong(Response::getTime)
                .average()
                .orElse(0.0);
        
        ExtentReportManager.logInfo(String.format(
                "Load Test Results:\n" +
                "Total Requests: %d\n" +
                "Successful Requests: %d\n" +
                "Success Rate: %.2f%%\n" +
                "Throughput: %.2f req/sec\n" +
                "Average Response Time: %.2fms\n" +
                "Total Test Time: %dms",
                totalRequests, successfulRequests, successRate, throughput, avgResponseTime, totalTime));
        
        Assert.assertTrue(successRate >= 90.0, "Success rate should be at least 90% under load");
        Assert.assertTrue(throughput >= 10.0, "Throughput should be at least 10 requests per second");
    }
    
    private void reportStressTestResults(List<PerformanceMetrics> results) {
        StringBuilder report = new StringBuilder("Stress Test Results:\n");
        
        for (PerformanceMetrics metrics : results) {
            report.append(String.format(
                    "Users: %d, Avg Response Time: %.2fms, Throughput: %.2f req/sec\n",
                    metrics.users, metrics.avgResponseTime, metrics.throughput));
        }
        
        ExtentReportManager.logInfo(report.toString());
        
        // Find optimal load point (before significant performance degradation)
        PerformanceMetrics optimalLoad = results.stream()
                .filter(m -> m.avgResponseTime < 3000) // Less than 3 seconds
                .max((m1, m2) -> Integer.compare(m1.users, m2.users))
                .orElse(results.get(0));
        
        ExtentReportManager.logInfo("Optimal load point: " + optimalLoad.users + " concurrent users");
    }
    
    private EnduranceResult runEnduranceUser(int userId, long testDuration, int requestInterval) {
        List<Long> responseTimes = new ArrayList<>();
        List<Integer> statusCodes = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < testDuration) {
            long requestStart = System.currentTimeMillis();
            
            Response response = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withQueryParam("page", "1")
                    .get();
            
            long responseTime = System.currentTimeMillis() - requestStart;
            responseTimes.add(responseTime);
            statusCodes.add(response.getStatusCode());
            
            try {
                Thread.sleep(requestInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return new EnduranceResult(userId, responseTimes, statusCodes);
    }
    
    private void analyzeEnduranceResults(List<EnduranceResult> results, int durationMinutes) {
        int totalRequests = results.stream()
                .mapToInt(r -> r.responseTimes.size())
                .sum();
        
        long successfulRequests = results.stream()
                .flatMap(r -> r.statusCodes.stream())
                .filter(code -> code >= 200 && code < 300)
                .count();
        
        double successRate = (successfulRequests * 100.0) / totalRequests;
        
        double avgResponseTime = results.stream()
                .flatMap(r -> r.responseTimes.stream())
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        ExtentReportManager.logInfo(String.format(
                "Endurance Test Results (%d minutes):\n" +
                "Total Requests: %d\n" +
                "Successful Requests: %d\n" +
                "Success Rate: %.2f%%\n" +
                "Average Response Time: %.2fms",
                durationMinutes, totalRequests, successfulRequests, successRate, avgResponseTime));
        
        Assert.assertTrue(successRate >= 95.0, "Success rate should remain high during endurance test");
        Assert.assertTrue(avgResponseTime < 5000, "Response time should remain stable during endurance test");
    }
    
    // Data classes
    
    private static class PerformanceResult {
        private final int userId;
        private final List<Long> responseTimes;
        private final List<Integer> statusCodes;
        
        public PerformanceResult(int userId, List<Long> responseTimes, List<Integer> statusCodes) {
            this.userId = userId;
            this.responseTimes = responseTimes;
            this.statusCodes = statusCodes;
        }
        
        public double getAverageResponseTime() {
            return responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        }
        
        public long getSuccessfulRequests() {
            return statusCodes.stream().filter(code -> code >= 200 && code < 300).count();
        }
    }
    
    private static class PerformanceMetrics {
        public final int users;
        public final double avgResponseTime;
        public final double throughput;
        public final long totalTime;
        
        public PerformanceMetrics(int users, double avgResponseTime, double throughput, long totalTime) {
            this.users = users;
            this.avgResponseTime = avgResponseTime;
            this.throughput = throughput;
            this.totalTime = totalTime;
        }
    }
    
    private static class EnduranceResult {
        public final int userId;
        public final List<Long> responseTimes;
        public final List<Integer> statusCodes;
        
        public EnduranceResult(int userId, List<Long> responseTimes, List<Integer> statusCodes) {
            this.userId = userId;
            this.responseTimes = responseTimes;
            this.statusCodes = statusCodes;
        }
    }
}
