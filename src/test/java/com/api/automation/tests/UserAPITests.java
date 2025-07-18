package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.utils.JsonUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test class for User API endpoints
 */
@Epic("User Management")
@Feature("User API")
public class UserAPITests extends BaseTest {

    @Test(groups = {"smoke", "regression", "user"}, priority = 1)
    @Story("Get all users")
    @Description("Verify that all users can be retrieved successfully")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetAllUsers() {
        logTestInfo("testGetAllUsers", "Retrieve all users from the API");
        
        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/users")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("data", not(empty()))
                .extract()
                .response();

        // Additional validations
        List<Object> users = JsonUtils.extractListFromResponse(response, "data");
        Assert.assertTrue(users.size() > 0, "Users list should not be empty");
        
        // Validate JSON structure
        Assert.assertTrue(JsonUtils.validateJsonKeys(response, "data", "page", "per_page", "total"),
                "Response should contain required keys");
        
        logger.info("Successfully retrieved " + users.size() + " users");
    }

    @Test(groups = {"smoke", "regression", "user"}, priority = 2)
    @Story("Get user by ID")
    @Description("Verify that a specific user can be retrieved by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserById() {
        logTestInfo("testGetUserById", "Retrieve a specific user by ID");
        
        int userId = 2;
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", userId)
                .when()
                .get("/users/{id}")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("data.id", equalTo(userId))
                .body("data.email", not(emptyString()))
                .body("data.first_name", not(emptyString()))
                .body("data.last_name", not(emptyString()))
                .extract()
                .response();

        // Extract and validate user data
        String email = JsonUtils.extractValueFromResponse(response, "data.email");
        String firstName = JsonUtils.extractValueFromResponse(response, "data.first_name");
        
        Assert.assertNotNull(email, "User email should not be null");
        Assert.assertNotNull(firstName, "User first name should not be null");
        
        logger.info("Successfully retrieved user: " + firstName + " (" + email + ")");
    }

    @Test(groups = {"smoke", "regression", "user"}, priority = 3)
    @Story("Create new user")
    @Description("Verify that a new user can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUser() {
        logTestInfo("testCreateUser", "Create a new user");
        
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", "John Doe");
        userPayload.put("job", "Software Engineer");
        
        Response response = given()
                .spec(requestSpec)
                .body(userPayload)
                .when()
                .post("/users")
                .then()
                .spec(responseSpec)
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("Software Engineer"))
                .body("id", not(emptyString()))
                .body("createdAt", not(emptyString()))
                .extract()
                .response();

        // Extract created user details
        String userId = JsonUtils.extractValueFromResponse(response, "id");
        String createdAt = JsonUtils.extractValueFromResponse(response, "createdAt");
        
        Assert.assertNotNull(userId, "User ID should be generated");
        Assert.assertNotNull(createdAt, "Creation timestamp should be present");
        
        logger.info("Successfully created user with ID: " + userId);
    }

    @Test(groups = {"regression", "user"}, priority = 4)
    @Story("Update user")
    @Description("Verify that an existing user can be updated")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUser() {
        logTestInfo("testUpdateUser", "Update an existing user");
        
        int userId = 2;
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("name", "Jane Smith");
        updatePayload.put("job", "Senior Developer");
        
        Response response = given()
                .spec(requestSpec)
                .pathParam("id", userId)
                .body(updatePayload)
                .when()
                .put("/users/{id}")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo("Jane Smith"))
                .body("job", equalTo("Senior Developer"))
                .body("updatedAt", not(emptyString()))
                .extract()
                .response();

        String updatedAt = JsonUtils.extractValueFromResponse(response, "updatedAt");
        Assert.assertNotNull(updatedAt, "Update timestamp should be present");
        
        logger.info("Successfully updated user ID: " + userId);
    }

    @Test(groups = {"regression", "user"}, priority = 5)
    @Story("Delete user")
    @Description("Verify that a user can be deleted successfully")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUser() {
        logTestInfo("testDeleteUser", "Delete an existing user");
        
        int userId = 2;
        
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(204);
        
        logger.info("Successfully deleted user ID: " + userId);
    }

    @Test(groups = {"negative", "user"}, priority = 6)
    @Story("Invalid user creation")
    @Description("Verify proper error handling for invalid user data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithInvalidData() {
        logTestInfo("testCreateUserWithInvalidData", "Attempt to create user with invalid data");
        
        Map<String, Object> invalidPayload = new HashMap<>();
        invalidPayload.put("name", "");
        invalidPayload.put("job", "");
        
        given()
                .spec(requestSpec)
                .body(invalidPayload)
                .when()
                .post("/users")
                .then()
                .statusCode(anyOf(is(400), is(422)));
        
        logger.info("Correctly handled invalid user creation attempt");
    }

    @Test(groups = {"negative", "user"}, priority = 7)
    @Story("Non-existent user")
    @Description("Verify proper error handling for non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentUser() {
        logTestInfo("testGetNonExistentUser", "Attempt to get non-existent user");
        
        int nonExistentUserId = 999999;
        
        given()
                .spec(requestSpec)
                .pathParam("id", nonExistentUserId)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(404);
        
        logger.info("Correctly handled request for non-existent user");
    }
}
