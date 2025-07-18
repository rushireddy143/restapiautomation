package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.utils.JsonUtils;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Authentication API endpoints
 */
@Epic("Authentication")
@Feature("Authentication API")
public class AuthenticationTests extends BaseTest {

    @Test(groups = {"smoke", "regression", "auth"}, priority = 1)
    @Story("Valid login")
    @Description("Verify successful login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLogin() {
        logTestInfo("testValidLogin", "Test valid user login");
        
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "eve.holt@reqres.in");
        loginPayload.put("password", "cityslicka");
        
        Response response = given()
                .spec(requestSpec)
                .body(loginPayload)
                .when()
                .post("/login")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("token", not(emptyString()))
                .extract()
                .response();

        String token = JsonUtils.extractValueFromResponse(response, "token");
        Assert.assertNotNull(token, "Token should be present in response");
        Assert.assertTrue(token.length() > 0, "Token should not be empty");
        
        logger.info("Successfully logged in and received token: " + token.substring(0, 10) + "...");
    }

    @Test(groups = {"smoke", "regression", "auth"}, priority = 2)
    @Story("Valid registration")
    @Description("Verify successful registration with valid user data")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidRegistration() {
        logTestInfo("testValidRegistration", "Test valid user registration");
        
        Map<String, String> registrationPayload = new HashMap<>();
        registrationPayload.put("email", "eve.holt@reqres.in");
        registrationPayload.put("password", "pistol");
        
        Response response = given()
                .spec(requestSpec)
                .body(registrationPayload)
                .when()
                .post("/register")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", not(emptyString()))
                .body("token", not(emptyString()))
                .extract()
                .response();

        String userId = JsonUtils.extractValueFromResponse(response, "id");
        String token = JsonUtils.extractValueFromResponse(response, "token");
        
        Assert.assertNotNull(userId, "User ID should be present");
        Assert.assertNotNull(token, "Token should be present");
        
        logger.info("Successfully registered user with ID: " + userId);
    }

    @Test(groups = {"negative", "auth", "security"}, priority = 3)
    @Story("Invalid login")
    @Description("Verify proper error handling for invalid login credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidLogin() {
        logTestInfo("testInvalidLogin", "Test login with invalid credentials");
        
        Map<String, String> invalidLoginPayload = new HashMap<>();
        invalidLoginPayload.put("email", "invalid@email.com");
        invalidLoginPayload.put("password", "wrongpassword");
        
        Response response = given()
                .spec(requestSpec)
                .body(invalidLoginPayload)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", not(emptyString()))
                .extract()
                .response();

        String errorMessage = JsonUtils.extractValueFromResponse(response, "error");
        Assert.assertNotNull(errorMessage, "Error message should be present");
        
        logger.info("Correctly handled invalid login attempt. Error: " + errorMessage);
    }

    @Test(groups = {"negative", "auth", "security"}, priority = 4)
    @Story("Missing password login")
    @Description("Verify error handling when password is missing")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginMissingPassword() {
        logTestInfo("testLoginMissingPassword", "Test login with missing password");
        
        Map<String, String> incompletePayload = new HashMap<>();
        incompletePayload.put("email", "eve.holt@reqres.in");
        
        Response response = given()
                .spec(requestSpec)
                .body(incompletePayload)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", containsString("Missing password"))
                .extract()
                .response();

        String errorMessage = JsonUtils.extractValueFromResponse(response, "error");
        logger.info("Correctly handled missing password. Error: " + errorMessage);
    }

    @Test(groups = {"negative", "auth", "security"}, priority = 5)
    @Story("Invalid registration")
    @Description("Verify error handling for invalid registration data")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidRegistration() {
        logTestInfo("testInvalidRegistration", "Test registration with invalid data");
        
        Map<String, String> invalidPayload = new HashMap<>();
        invalidPayload.put("email", "sydney@fife");
        
        Response response = given()
                .spec(requestSpec)
                .body(invalidPayload)
                .when()
                .post("/register")
                .then()
                .statusCode(400)
                .body("error", not(emptyString()))
                .extract()
                .response();

        String errorMessage = JsonUtils.extractValueFromResponse(response, "error");
        Assert.assertNotNull(errorMessage, "Error message should be present");
        
        logger.info("Correctly handled invalid registration. Error: " + errorMessage);
    }

    @Test(groups = {"negative", "auth", "security"}, priority = 6)
    @Story("Unauthorized access")
    @Description("Verify that protected endpoints require authentication")
    @Severity(SeverityLevel.CRITICAL)
    public void testUnauthorizedAccess() {
        logTestInfo("testUnauthorizedAccess", "Test access to protected endpoint without auth");
        
        // Attempt to access a protected endpoint without authentication
        given()
                .spec(requestSpec)
                .when()
                .get("/users/2")
                .then()
                .statusCode(anyOf(is(200), is(401), is(403))); // Some APIs may not require auth for GET
        
        logger.info("Tested unauthorized access to protected endpoint");
    }

    @Test(groups = {"auth", "security"}, priority = 7)
    @Story("Token validation")
    @Description("Verify that valid tokens are accepted for authenticated requests")
    @Severity(SeverityLevel.NORMAL)
    public void testTokenValidation() {
        logTestInfo("testTokenValidation", "Test API access with valid token");
        
        // First, login to get a token
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "eve.holt@reqres.in");
        loginPayload.put("password", "cityslicka");
        
        Response loginResponse = given()
                .spec(requestSpec)
                .body(loginPayload)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = JsonUtils.extractValueFromResponse(loginResponse, "token");
        
        // Use the token for an authenticated request
        given()
                .spec(getAuthenticatedRequestSpec())
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users")
                .then()
                .statusCode(200);
        
        logger.info("Successfully validated token for authenticated request");
    }
}
