package com.api.automation.utils;

import com.api.automation.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Factory class for generating test data
 */
public class TestDataFactory {
    
    private static final Logger logger = LogManager.getLogger(TestDataFactory.class);
    private static final Random random = new Random();
    private static ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Generate random user data for testing
     */
    public static Map<String, Object> generateRandomUser() {
        Map<String, Object> user = new HashMap<>();
        
        String[] firstNames = {"John", "Jane", "Bob", "Alice", "Charlie", "Diana", "Eve", "Frank"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"};
        String[] jobs = {"Software Engineer", "Product Manager", "Designer", "QA Engineer", "DevOps Engineer", "Data Scientist"};
        
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String job = jobs[random.nextInt(jobs.length)];
        
        user.put("name", firstName + " " + lastName);
        user.put("job", job);
        user.put("email", firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        user.put("age", 25 + random.nextInt(40)); // Age between 25-65
        
        logger.info("Generated random user: " + user.get("name"));
        return user;
    }
    
    /**
     * Generate login credentials for testing
     */
    public static Map<String, String> getValidLoginCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "eve.holt@reqres.in");
        credentials.put("password", "cityslicka");
        return credentials;
    }
    
    /**
     * Generate invalid login credentials for negative testing
     */
    public static Map<String, String> getInvalidLoginCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "invalid" + random.nextInt(1000) + "@example.com");
        credentials.put("password", "wrongpassword" + random.nextInt(1000));
        return credentials;
    }
    
    /**
     * Generate registration data
     */
    public static Map<String, String> generateRegistrationData() {
        Map<String, String> registration = new HashMap<>();
        registration.put("email", "user" + random.nextInt(10000) + "@example.com");
        registration.put("password", "password" + random.nextInt(1000));
        return registration;
    }
    
    /**
     * Generate invalid data for negative testing
     */
    public static Map<String, Object> generateInvalidUserData() {
        Map<String, Object> invalidUser = new HashMap<>();
        
        // Various invalid scenarios
        int scenario = random.nextInt(4);
        switch (scenario) {
            case 0: // Empty name
                invalidUser.put("name", "");
                invalidUser.put("job", "Test Job");
                break;
            case 1: // Null values
                invalidUser.put("name", null);
                invalidUser.put("job", null);
                break;
            case 2: // Special characters
                invalidUser.put("name", "!@#$%^&*()");
                invalidUser.put("job", "<script>alert('test')</script>");
                break;
            case 3: // Very long strings
                invalidUser.put("name", "A".repeat(1000));
                invalidUser.put("job", "B".repeat(1000));
                break;
        }
        
        logger.info("Generated invalid user data for scenario: " + scenario);
        return invalidUser;
    }
    
    /**
     * Generate test user data with specific attributes
     */
    public static Map<String, Object> generateUserWithAttributes(String name, String job, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("job", job);
        if (email != null) {
            user.put("email", email);
        }
        return user;
    }
    
    /**
     * Generate random string of specified length
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generate random number within range
     */
    public static int generateRandomNumber(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
