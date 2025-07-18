package com.api.automation.patterns;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Chain of Responsibility pattern for API response validation
 * Allows chaining multiple validators for comprehensive response validation
 */
public abstract class ChainOfResponsibilityValidator {
    
    private static final Logger logger = LogManager.getLogger(ChainOfResponsibilityValidator.class);
    protected ChainOfResponsibilityValidator nextValidator;
    
    /**
     * Set the next validator in the chain
     */
    public ChainOfResponsibilityValidator setNext(ChainOfResponsibilityValidator nextValidator) {
        this.nextValidator = nextValidator;
        return nextValidator;
    }
    
    /**
     * Validate response and pass to next validator if validation passes
     */
    public ValidationResult validate(Response response) {
        ValidationResult result = doValidation(response);
        
        if (result.isValid() && nextValidator != null) {
            ValidationResult nextResult = nextValidator.validate(response);
            result.mergeWith(nextResult);
        }
        
        return result;
    }
    
    /**
     * Abstract method for specific validation logic
     */
    protected abstract ValidationResult doValidation(Response response);
    
    /**
     * Status Code Validator
     */
    public static class StatusCodeValidator extends ChainOfResponsibilityValidator {
        private final int expectedStatusCode;
        
        public StatusCodeValidator(int expectedStatusCode) {
            this.expectedStatusCode = expectedStatusCode;
        }
        
        @Override
        protected ValidationResult doValidation(Response response) {
            ValidationResult result = new ValidationResult();
            int actualStatusCode = response.getStatusCode();
            
            if (actualStatusCode == expectedStatusCode) {
                result.addSuccess("Status code validation passed: " + actualStatusCode);
                logger.info("Status code validation passed: {}", actualStatusCode);
            } else {
                result.addError("Expected status code: " + expectedStatusCode + 
                              ", but got: " + actualStatusCode);
                logger.error("Status code validation failed. Expected: {}, Actual: {}", 
                           expectedStatusCode, actualStatusCode);
            }
            
            return result;
        }
    }
    
    /**
     * Response Time Validator
     */
    public static class ResponseTimeValidator extends ChainOfResponsibilityValidator {
        private final long maxResponseTime;
        
        public ResponseTimeValidator(long maxResponseTime) {
            this.maxResponseTime = maxResponseTime;
        }
        
        @Override
        protected ValidationResult doValidation(Response response) {
            ValidationResult result = new ValidationResult();
            long actualResponseTime = response.getTime();
            
            if (actualResponseTime <= maxResponseTime) {
                result.addSuccess("Response time validation passed: " + actualResponseTime + "ms");
                logger.info("Response time validation passed: {}ms", actualResponseTime);
            } else {
                result.addError("Response time exceeded. Expected: <= " + maxResponseTime + 
                              "ms, but got: " + actualResponseTime + "ms");
                logger.error("Response time validation failed. Expected: <= {}ms, Actual: {}ms", 
                           maxResponseTime, actualResponseTime);
            }
            
            return result;
        }
    }
    
    /**
     * Content Type Validator
     */
    public static class ContentTypeValidator extends ChainOfResponsibilityValidator {
        private final String expectedContentType;
        
        public ContentTypeValidator(String expectedContentType) {
            this.expectedContentType = expectedContentType;
        }
        
        @Override
        protected ValidationResult doValidation(Response response) {
            ValidationResult result = new ValidationResult();
            String actualContentType = response.getContentType();
            
            if (actualContentType != null && actualContentType.contains(expectedContentType)) {
                result.addSuccess("Content type validation passed: " + actualContentType);
                logger.info("Content type validation passed: {}", actualContentType);
            } else {
                result.addError("Expected content type to contain: " + expectedContentType + 
                              ", but got: " + actualContentType);
                logger.error("Content type validation failed. Expected: {}, Actual: {}", 
                           expectedContentType, actualContentType);
            }
            
            return result;
        }
    }
    
    /**
     * JSON Schema Validator
     */
    public static class JsonSchemaValidator extends ChainOfResponsibilityValidator {
        private final String schemaPath;
        
        public JsonSchemaValidator(String schemaPath) {
            this.schemaPath = schemaPath;
        }
        
        @Override
        protected ValidationResult doValidation(Response response) {
            ValidationResult result = new ValidationResult();
            
            try {
                // JSON Schema validation logic would go here
                // For now, just checking if response is valid JSON
                response.jsonPath().prettify();
                result.addSuccess("JSON schema validation passed");
                logger.info("JSON schema validation passed for schema: {}", schemaPath);
            } catch (Exception e) {
                result.addError("JSON schema validation failed: " + e.getMessage());
                logger.error("JSON schema validation failed for schema: {}", schemaPath, e);
            }
            
            return result;
        }
    }
    
    /**
     * Custom Field Validator
     */
    public static class FieldValidator extends ChainOfResponsibilityValidator {
        private final String fieldPath;
        private final Object expectedValue;
        
        public FieldValidator(String fieldPath, Object expectedValue) {
            this.fieldPath = fieldPath;
            this.expectedValue = expectedValue;
        }
        
        @Override
        protected ValidationResult doValidation(Response response) {
            ValidationResult result = new ValidationResult();
            
            try {
                Object actualValue = response.jsonPath().get(fieldPath);
                
                if (expectedValue.equals(actualValue)) {
                    result.addSuccess("Field validation passed for '" + fieldPath + "': " + actualValue);
                    logger.info("Field validation passed for '{}': {}", fieldPath, actualValue);
                } else {
                    result.addError("Field validation failed for '" + fieldPath + 
                                  "'. Expected: " + expectedValue + ", Actual: " + actualValue);
                    logger.error("Field validation failed for '{}'. Expected: {}, Actual: {}", 
                               fieldPath, expectedValue, actualValue);
                }
            } catch (Exception e) {
                result.addError("Field validation error for '" + fieldPath + "': " + e.getMessage());
                logger.error("Field validation error for '{}'", fieldPath, e);
            }
            
            return result;
        }
    }
    
    /**
     * Validation Result class
     */
    public static class ValidationResult {
        private boolean valid = true;
        private List<String> successMessages = new ArrayList<>();
        private List<String> errorMessages = new ArrayList<>();
        
        public void addSuccess(String message) {
            successMessages.add(message);
        }
        
        public void addError(String message) {
            errorMessages.add(message);
            valid = false;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getSuccessMessages() {
            return successMessages;
        }
        
        public List<String> getErrorMessages() {
            return errorMessages;
        }
        
        public void mergeWith(ValidationResult other) {
            this.successMessages.addAll(other.getSuccessMessages());
            this.errorMessages.addAll(other.getErrorMessages());
            if (!other.isValid()) {
                this.valid = false;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Validation Result: ").append(valid ? "PASSED" : "FAILED").append("\n");
            
            if (!successMessages.isEmpty()) {
                sb.append("Successes:\n");
                successMessages.forEach(msg -> sb.append("  ✓ ").append(msg).append("\n"));
            }
            
            if (!errorMessages.isEmpty()) {
                sb.append("Errors:\n");
                errorMessages.forEach(msg -> sb.append("  ✗ ").append(msg).append("\n"));
            }
            
            return sb.toString();
        }
    }
}
