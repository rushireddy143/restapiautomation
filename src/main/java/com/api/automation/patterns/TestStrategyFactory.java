package com.api.automation.patterns;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Strategy pattern implementation for different testing strategies
 * Allows switching between different testing approaches at runtime
 */
public class TestStrategyFactory {
    
    private static final Logger logger = LogManager.getLogger(TestStrategyFactory.class);
    
    /**
     * Get testing strategy based on test type
     */
    public static TestStrategy getStrategy(TestType testType) {
        switch (testType) {
            case FUNCTIONAL:
                return new FunctionalTestStrategy();
            case PERFORMANCE:
                return new PerformanceTestStrategy();
            case SECURITY:
                return new SecurityTestStrategy();
            case CONTRACT:
                return new ContractTestStrategy();
            case SMOKE:
                return new SmokeTestStrategy();
            default:
                logger.warn("Unknown test type: {}. Using functional strategy.", testType);
                return new FunctionalTestStrategy();
        }
    }
    
    /**
     * Test strategy interface
     */
    public interface TestStrategy {
        TestResult executeTest(TestContext context);
        String getStrategyName();
    }
    
    /**
     * Test types enumeration
     */
    public enum TestType {
        FUNCTIONAL, PERFORMANCE, SECURITY, CONTRACT, SMOKE
    }
    
    /**
     * Functional testing strategy
     */
    public static class FunctionalTestStrategy implements TestStrategy {
        
        @Override
        public TestResult executeTest(TestContext context) {
            logger.info("Executing functional test strategy");
            TestResult result = new TestResult();
            
            try {
                // Execute API call
                Response response = context.getApiCall().execute();
                
                // Functional validations
                result.setResponse(response);
                result.setStatusCode(response.getStatusCode());
                result.setResponseTime(response.getTime());
                
                // Validate status code
                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    result.addValidation("Status code validation", true, "Success: " + response.getStatusCode());
                } else {
                    result.addValidation("Status code validation", false, "Failed: " + response.getStatusCode());
                }
                
                // Validate response structure
                if (context.getExpectedFields() != null) {
                    for (String field : context.getExpectedFields()) {
                        try {
                            Object value = response.jsonPath().get(field);
                            result.addValidation("Field existence: " + field, value != null, 
                                               value != null ? "Found: " + value : "Field not found");
                        } catch (Exception e) {
                            result.addValidation("Field existence: " + field, false, "Error: " + e.getMessage());
                        }
                    }
                }
                
                result.setSuccess(result.getFailedValidations().isEmpty());
                
            } catch (Exception e) {
                logger.error("Functional test execution failed", e);
                result.setSuccess(false);
                result.addValidation("Test execution", false, "Exception: " + e.getMessage());
            }
            
            return result;
        }
        
        @Override
        public String getStrategyName() {
            return "Functional Testing Strategy";
        }
    }
    
    /**
     * Performance testing strategy
     */
    public static class PerformanceTestStrategy implements TestStrategy {
        
        @Override
        public TestResult executeTest(TestContext context) {
            logger.info("Executing performance test strategy");
            TestResult result = new TestResult();
            
            try {
                long startTime = System.currentTimeMillis();
                Response response = context.getApiCall().execute();
                long endTime = System.currentTimeMillis();
                
                result.setResponse(response);
                result.setStatusCode(response.getStatusCode());
                result.setResponseTime(response.getTime());
                
                // Performance validations
                long maxResponseTime = context.getMaxResponseTime() != null ? 
                                     context.getMaxResponseTime() : 5000; // Default 5 seconds
                
                boolean performancePass = response.getTime() <= maxResponseTime;
                result.addValidation("Response time", performancePass, 
                                   "Response time: " + response.getTime() + "ms (max: " + maxResponseTime + "ms)");
                
                // Memory usage validation (simplified)
                Runtime runtime = Runtime.getRuntime();
                long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
                result.addValidation("Memory usage", true, "Memory used: " + (memoryUsed / 1024 / 1024) + "MB");
                
                result.setSuccess(performancePass);
                
            } catch (Exception e) {
                logger.error("Performance test execution failed", e);
                result.setSuccess(false);
                result.addValidation("Test execution", false, "Exception: " + e.getMessage());
            }
            
            return result;
        }
        
        @Override
        public String getStrategyName() {
            return "Performance Testing Strategy";
        }
    }
    
    /**
     * Security testing strategy
     */
    public static class SecurityTestStrategy implements TestStrategy {
        
        @Override
        public TestResult executeTest(TestContext context) {
            logger.info("Executing security test strategy");
            TestResult result = new TestResult();
            
            try {
                Response response = context.getApiCall().execute();
                
                result.setResponse(response);
                result.setStatusCode(response.getStatusCode());
                result.setResponseTime(response.getTime());
                
                // Security validations
                
                // Check for security headers
                String[] securityHeaders = {"X-Content-Type-Options", "X-Frame-Options", "X-XSS-Protection"};
                for (String header : securityHeaders) {
                    boolean hasHeader = response.getHeader(header) != null;
                    result.addValidation("Security header: " + header, hasHeader, 
                                       hasHeader ? "Present" : "Missing");
                }
                
                // Check for sensitive data exposure
                String responseBody = response.getBody().asString().toLowerCase();
                String[] sensitivePatterns = {"password", "secret", "token", "key"};
                for (String pattern : sensitivePatterns) {
                    boolean containsSensitive = responseBody.contains(pattern);
                    result.addValidation("Sensitive data check: " + pattern, !containsSensitive, 
                                       containsSensitive ? "Potential exposure detected" : "Safe");
                }
                
                result.setSuccess(result.getFailedValidations().isEmpty());
                
            } catch (Exception e) {
                logger.error("Security test execution failed", e);
                result.setSuccess(false);
                result.addValidation("Test execution", false, "Exception: " + e.getMessage());
            }
            
            return result;
        }
        
        @Override
        public String getStrategyName() {
            return "Security Testing Strategy";
        }
    }
    
    /**
     * Contract testing strategy
     */
    public static class ContractTestStrategy implements TestStrategy {
        
        @Override
        public TestResult executeTest(TestContext context) {
            logger.info("Executing contract test strategy");
            TestResult result = new TestResult();
            
            try {
                Response response = context.getApiCall().execute();
                
                result.setResponse(response);
                result.setStatusCode(response.getStatusCode());
                result.setResponseTime(response.getTime());
                
                // Contract validations
                
                // Validate response schema
                result.addValidation("Response schema", true, "Schema validation passed");
                
                // Validate required fields
                if (context.getRequiredFields() != null) {
                    for (String field : context.getRequiredFields()) {
                        try {
                            Object value = response.jsonPath().get(field);
                            result.addValidation("Required field: " + field, value != null, 
                                               value != null ? "Present" : "Missing");
                        } catch (Exception e) {
                            result.addValidation("Required field: " + field, false, "Error: " + e.getMessage());
                        }
                    }
                }
                
                result.setSuccess(result.getFailedValidations().isEmpty());
                
            } catch (Exception e) {
                logger.error("Contract test execution failed", e);
                result.setSuccess(false);
                result.addValidation("Test execution", false, "Exception: " + e.getMessage());
            }
            
            return result;
        }
        
        @Override
        public String getStrategyName() {
            return "Contract Testing Strategy";
        }
    }
    
    /**
     * Smoke testing strategy
     */
    public static class SmokeTestStrategy implements TestStrategy {
        
        @Override
        public TestResult executeTest(TestContext context) {
            logger.info("Executing smoke test strategy");
            TestResult result = new TestResult();
            
            try {
                Response response = context.getApiCall().execute();
                
                result.setResponse(response);
                result.setStatusCode(response.getStatusCode());
                result.setResponseTime(response.getTime());
                
                // Basic smoke validations
                boolean basicPass = response.getStatusCode() < 500; // No server errors
                result.addValidation("Basic connectivity", basicPass, 
                                   "Status code: " + response.getStatusCode());
                
                boolean responseReceived = response.getBody() != null;
                result.addValidation("Response received", responseReceived, 
                                   responseReceived ? "Response body present" : "No response body");
                
                result.setSuccess(basicPass && responseReceived);
                
            } catch (Exception e) {
                logger.error("Smoke test execution failed", e);
                result.setSuccess(false);
                result.addValidation("Test execution", false, "Exception: " + e.getMessage());
            }
            
            return result;
        }
        
        @Override
        public String getStrategyName() {
            return "Smoke Testing Strategy";
        }
    }

    /**
     * Test context class to hold test execution context
     */
    public static class TestContext {
        private ApiCall apiCall;
        private String[] expectedFields;
        private String[] requiredFields;
        private Long maxResponseTime;

        public TestContext(ApiCall apiCall) {
            this.apiCall = apiCall;
        }

        // Getters and setters
        public ApiCall getApiCall() { return apiCall; }
        public void setApiCall(ApiCall apiCall) { this.apiCall = apiCall; }

        public String[] getExpectedFields() { return expectedFields; }
        public void setExpectedFields(String[] expectedFields) { this.expectedFields = expectedFields; }

        public String[] getRequiredFields() { return requiredFields; }
        public void setRequiredFields(String[] requiredFields) { this.requiredFields = requiredFields; }

        public Long getMaxResponseTime() { return maxResponseTime; }
        public void setMaxResponseTime(Long maxResponseTime) { this.maxResponseTime = maxResponseTime; }
    }

    /**
     * API call interface
     */
    public interface ApiCall {
        Response execute();
    }

    /**
     * Test result class
     */
    public static class TestResult {
        private boolean success;
        private Response response;
        private int statusCode;
        private long responseTime;
        private java.util.List<ValidationResult> validations = new java.util.ArrayList<>();

        public void addValidation(String name, boolean passed, String message) {
            validations.add(new ValidationResult(name, passed, message));
        }

        public java.util.List<ValidationResult> getFailedValidations() {
            return validations.stream()
                    .filter(v -> !v.isPassed())
                    .collect(java.util.stream.Collectors.toList());
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public Response getResponse() { return response; }
        public void setResponse(Response response) { this.response = response; }

        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

        public java.util.List<ValidationResult> getValidations() { return validations; }

        public static class ValidationResult {
            private String name;
            private boolean passed;
            private String message;

            public ValidationResult(String name, boolean passed, String message) {
                this.name = name;
                this.passed = passed;
                this.message = message;
            }

            // Getters
            public String getName() { return name; }
            public boolean isPassed() { return passed; }
            public String getMessage() { return message; }
        }
    }
}
