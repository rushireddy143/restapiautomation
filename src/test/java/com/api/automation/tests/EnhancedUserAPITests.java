package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.models.User;
import com.api.automation.models.UserResponse;
import com.api.automation.services.UserService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Enhanced User API tests using new framework patterns
 * Demonstrates Builder Pattern, Service Layer, and Model Classes
 */
@Epic("Enhanced User Management")
@Feature("User API with Framework Patterns")
public class EnhancedUserAPITests extends BaseTest {
    
    private UserService userService;
    
    @BeforeClass
    public void setupUserService() {
        userService = new UserService();
        logger.info("UserService initialized for enhanced API tests");
    }
    
    @Test(groups = {"smoke", "regression", "enhanced"}, priority = 1)
    @Story("Get users using service layer")
    @Description("Verify users retrieval using service layer pattern")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetUsersWithServiceLayer() {
        logTestInfo("testGetUsersWithServiceLayer", "Get users using service layer");
        
        // Using Service Layer
        Response response = userService.getAllUsers(2);
        
        // Assertions
        response.then()
                .statusCode(200)
                .body("data", not(empty()))
                .body("page", equalTo(2));
        
        // Parse response using model
        UserResponse userResponse = userService.parseUserResponse(response);
        Assert.assertNotNull(userResponse.getData(), "User data should not be null");
        Assert.assertTrue(userResponse.hasData(), "Response should contain user data");
        Assert.assertEquals(userResponse.getPage(), Integer.valueOf(2), "Page should be 2");
        
        logger.info("Successfully retrieved {} users", userResponse.getDataSize());
    }
    
    @Test(groups = {"smoke", "regression", "enhanced"}, priority = 2)
    @Story("Create user using builder pattern")
    @Description("Verify user creation using builder pattern and models")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserWithBuilderPattern() {
        logTestInfo("testCreateUserWithBuilderPattern", "Create user using builder pattern");
        
        // Create user using Builder Pattern
        User newUser = User.builder()
                .withName("John Doe")
                .withJob("Software Engineer")
                .build();
        
        // Create user using Service Layer
        Response response = userService.createUser(newUser);
        
        // Assertions
        response.then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("Software Engineer"))
                .body("id", not(emptyString()))
                .body("createdAt", not(emptyString()));
        
        logger.info("User created successfully with ID: {}", response.jsonPath().getString("id"));
    }
    
    @Test(groups = {"regression", "enhanced"}, priority = 3)
    @Story("Update user using API builder")
    @Description("Verify user update using API request builder")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserWithApiBuilder() {
        logTestInfo("testUpdateUserWithApiBuilder", "Update user using API builder");
        
        // Create update payload
        Map<String, Object> updates = new HashMap<>();
        updates.put("job", "Senior Software Engineer");
        
        // Update user using API Request Builder directly
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/users/{id}")
                .withPathParam("id", 2)
                .withBody(updates)
                .patch();
        
        // Assertions
        response.then()
                .statusCode(200)
                .body("job", equalTo("Senior Software Engineer"))
                .body("updatedAt", not(emptyString()));
        
        logger.info("User updated successfully at: {}", response.jsonPath().getString("updatedAt"));
    }
    
    @Test(groups = {"regression", "enhanced"}, priority = 4)
    @Story("Complex user operations")
    @Description("Verify complex user operations using service layer")
    @Severity(SeverityLevel.NORMAL)
    public void testComplexUserOperations() {
        logTestInfo("testComplexUserOperations", "Perform complex user operations");
        
        // 1. Create user
        User newUser = User.builder()
                .withName("Jane Smith")
                .withJob("QA Engineer")
                .build();
        
        Response createResponse = userService.createUser(newUser);
        createResponse.then().statusCode(201);
        
        // 2. Get user by ID (simulated)
        Response getUserResponse = userService.getUserById(2);
        getUserResponse.then().statusCode(200);
        
        // 3. Update user
        Map<String, Object> updates = new HashMap<>();
        updates.put("job", "Lead QA Engineer");
        
        Response updateResponse = userService.updateUserPartially(2, updates);
        updateResponse.then().statusCode(200);
        
        // 4. Search users with filters
        Map<String, Object> filters = new HashMap<>();
        filters.put("page", 1);
        
        Response searchResponse = userService.searchUsers(filters);
        searchResponse.then().statusCode(200);
        
        logger.info("Complex user operations completed successfully");
    }
    
    @Test(groups = {"regression", "enhanced", "auth"}, priority = 5)
    @Story("Authenticated user operations")
    @Description("Verify user operations with authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthenticatedUserOperations() {
        logTestInfo("testAuthenticatedUserOperations", "Test authenticated user operations");
        
        // Get users with authentication
        Response response = userService.getAllUsersWithAuth(1);
        
        // Note: reqres.in doesn't actually require auth for this endpoint
        // but this demonstrates the pattern
        response.then().statusCode(200);
        
        // Using API Builder with authentication
        Response authResponse = ApiRequestBuilder.create()
                .withEndpoint("/users")
                .withQueryParam("page", 1)
                .withAuth()
                .get();
        
        authResponse.then().statusCode(200);
        
        logger.info("Authenticated operations completed successfully");
    }
    
    @Test(groups = {"regression", "enhanced"}, priority = 6)
    @Story("User data validation")
    @Description("Verify user data validation using models")
    @Severity(SeverityLevel.NORMAL)
    public void testUserDataValidation() {
        logTestInfo("testUserDataValidation", "Validate user data using models");
        
        // Get users and validate using models
        Response response = userService.getAllUsers(1);
        UserResponse userResponse = userService.parseUserResponse(response);
        
        // Validate response structure
        Assert.assertNotNull(userResponse, "User response should not be null");
        Assert.assertTrue(userResponse.hasData(), "Response should contain data");
        Assert.assertTrue(userResponse.getDataSize() > 0, "Should have at least one user");
        
        // Validate first user
        User firstUser = userResponse.getFirstUser();
        Assert.assertNotNull(firstUser, "First user should not be null");
        Assert.assertNotNull(firstUser.getId(), "User ID should not be null");
        Assert.assertNotNull(firstUser.getEmail(), "User email should not be null");
        Assert.assertNotNull(firstUser.getFirstName(), "User first name should not be null");
        Assert.assertNotNull(firstUser.getLastName(), "User last name should not be null");
        
        logger.info("User data validation completed for user: {}", firstUser.toString());
    }
    
    @Test(groups = {"negative", "enhanced"}, priority = 7)
    @Story("Error handling")
    @Description("Verify error handling in service layer")
    @Severity(SeverityLevel.NORMAL)
    public void testErrorHandling() {
        logTestInfo("testErrorHandling", "Test error handling in service layer");
        
        // Test with invalid user ID
        Response response = userService.getUserById(999);
        response.then().statusCode(404);
        
        // Test with invalid endpoint using API builder
        Response invalidResponse = ApiRequestBuilder.create()
                .withEndpoint("/invalid-endpoint")
                .get();
        
        invalidResponse.then().statusCode(404);
        
        logger.info("Error handling tests completed successfully");
    }
    
    private void logTestInfo(String testName, String description) {
        logger.info("=== Starting Test: {} ===", testName);
        logger.info("Description: {}", description);
        Allure.step("Test: " + testName + " - " + description);
    }
}
