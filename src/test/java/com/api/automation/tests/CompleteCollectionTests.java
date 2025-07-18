package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.models.User;
import com.api.automation.services.UserService;
import com.api.automation.utils.ExtentReportManager;
import com.api.automation.utils.TestDataManager;
import com.api.automation.patterns.ChainOfResponsibilityValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Complete automation for all endpoints in the Postman collection
 * Covers all CRUD operations with comprehensive validations
 */
@Epic("Complete Collection Automation")
@Feature("All API Endpoints")
public class CompleteCollectionTests extends BaseTest {
    
    private UserService userService;
    private TestDataManager testDataManager;
    private String createdUserId;
    
    @BeforeClass
    public void setupCollectionTests() {
        userService = new UserService();
        testDataManager = TestDataManager.getInstance();
        logger.info("Complete collection tests initialized");
        ExtentReportManager.logInfo("Starting complete collection automation tests");
    }
    
    @Test(groups = {"smoke", "regression", "collection"}, priority = 1)
    @Story("Get Users - Collection Endpoint 1")
    @Description("Automate 'Get Users' endpoint from Postman collection")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetUsersFromCollection() {
        ExtentReportManager.logInfo("Testing Get Users endpoint from collection");
        
        // Execute the exact request from collection
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withQueryParam("page", "2")
                .get();
        
        // Log API details
        ExtentReportManager.logApiRequest("GET", "/api/users?page=2", null);
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
        
        // Validate using chain of responsibility pattern
        ChainOfResponsibilityValidator validator = new ChainOfResponsibilityValidator.StatusCodeValidator(200)
                .setNext(new ChainOfResponsibilityValidator.ContentTypeValidator("application/json"))
                .setNext(new ChainOfResponsibilityValidator.ResponseTimeValidator(5000));
        
        ChainOfResponsibilityValidator.ValidationResult validationResult = validator.validate(response);
        Assert.assertTrue(validationResult.isValid(), "Validation chain should pass");
        
        // Collection-specific validations
        response.then()
                .statusCode(200)
                .body("data", not(empty()))
                .body("data.size()", greaterThan(0))
                .body("data[0].id", notNullValue())
                .body("page", equalTo(2));
        
        ExtentReportManager.logPass("Get Users endpoint validation completed successfully");
        logger.info("Get Users test completed - Status: {}, Response time: {}ms", 
                   response.getStatusCode(), response.getTime());
    }
    
    @Test(groups = {"smoke", "regression", "collection"}, priority = 2)
    @Story("Create User - Collection Endpoint 2")
    @Description("Automate 'Create User' endpoint from Postman collection")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserFromCollection() {
        ExtentReportManager.logInfo("Testing Create User endpoint from collection");
        
        // Use exact data from collection
        Map<String, String> userData = new HashMap<>();
        userData.put("name", "Rushi");
        userData.put("job", "QA Engineer");
        
        // Execute request with API key header as in collection
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(userData)
                .post();
        
        // Log API details
        ExtentReportManager.logApiRequest("POST", "/api/users", userData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
        
        // Collection-specific validations
        response.then()
                .statusCode(201)
                .body("name", equalTo("Rushi"))
                .body("job", equalTo("QA Engineer"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue());
        
        // Store created user ID for subsequent tests
        createdUserId = response.jsonPath().getString("id");
        
        ExtentReportManager.logPass("Create User endpoint validation completed successfully");
        ExtentReportManager.logInfo("Created user with ID: " + createdUserId);
        logger.info("Create User test completed - User ID: {}", createdUserId);
    }
    
    @Test(groups = {"regression", "collection"}, priority = 3, dependsOnMethods = "testCreateUserFromCollection")
    @Story("Update User (PUT) - Collection Endpoint 3")
    @Description("Automate 'Update User (PUT)' endpoint from Postman collection")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserPutFromCollection() {
        ExtentReportManager.logInfo("Testing Update User (PUT) endpoint from collection");
        
        // Use exact data from collection
        Map<String, String> updateData = new HashMap<>();
        updateData.put("name", "Rushi");
        updateData.put("job", "Senior QA Engineer");
        
        // Execute PUT request as in collection
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(updateData)
                .put();
        
        // Log API details
        ExtentReportManager.logApiRequest("PUT", "/api/users/2", updateData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
        
        // Collection-specific validations
        response.then()
                .statusCode(200)
                .body("name", equalTo("Rushi"))
                .body("job", equalTo("Senior QA Engineer"))
                .body("updatedAt", notNullValue());
        
        ExtentReportManager.logPass("Update User (PUT) endpoint validation completed successfully");
        logger.info("Update User (PUT) test completed - Status: {}", response.getStatusCode());
    }
    
    @Test(groups = {"regression", "collection"}, priority = 4)
    @Story("Update User (PATCH) - Collection Endpoint 4")
    @Description("Automate 'Update User (PATCH)' endpoint from Postman collection")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserPatchFromCollection() {
        ExtentReportManager.logInfo("Testing Update User (PATCH) endpoint from collection");
        
        // Use exact data from collection
        Map<String, String> patchData = new HashMap<>();
        patchData.put("job", "Lead QA Engineer");
        
        // Execute PATCH request as in collection
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(patchData)
                .patch();
        
        // Log API details
        ExtentReportManager.logApiRequest("PATCH", "/api/users/2", patchData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
        
        // Collection-specific validations
        response.then()
                .statusCode(200)
                .body("job", equalTo("Lead QA Engineer"))
                .body("updatedAt", notNullValue());
        
        ExtentReportManager.logPass("Update User (PATCH) endpoint validation completed successfully");
        logger.info("Update User (PATCH) test completed - Status: {}", response.getStatusCode());
    }
    
    @Test(groups = {"regression", "collection"}, priority = 5)
    @Story("Delete User - Collection Endpoint 5")
    @Description("Automate 'Delete User' endpoint from Postman collection")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUserFromCollection() {
        ExtentReportManager.logInfo("Testing Delete User endpoint from collection");
        
        // Execute DELETE request as in collection
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .delete();
        
        // Log API details
        ExtentReportManager.logApiRequest("DELETE", "/api/users/2", null);
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
        
        // Collection-specific validations
        response.then()
                .statusCode(204);
        
        // Verify empty response body
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody == null || responseBody.trim().isEmpty(), 
                        "Response body should be empty for DELETE operation");
        
        ExtentReportManager.logPass("Delete User endpoint validation completed successfully");
        logger.info("Delete User test completed - Status: {}", response.getStatusCode());
    }
    
    @Test(groups = {"regression", "collection", "comprehensive"}, priority = 6)
    @Story("Complete Collection Workflow")
    @Description("Execute complete workflow covering all collection endpoints")
    @Severity(SeverityLevel.CRITICAL)
    public void testCompleteCollectionWorkflow() {
        ExtentReportManager.logInfo("Testing complete collection workflow");
        
        // Step 1: Get Users (verify system state)
        Response getUsersResponse = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withQueryParam("page", "1")
                .get();
        
        Assert.assertEquals(getUsersResponse.getStatusCode(), 200, "Get Users should succeed");
        ExtentReportManager.logPass("Step 1: Get Users - Completed");
        
        // Step 2: Create User
        Map<String, String> newUser = testDataManager.generateFakeUserData()
                .entrySet().stream()
                .filter(entry -> entry.getKey().equals("firstName") || entry.getKey().equals("job"))
                .collect(HashMap::new, (m, e) -> m.put(
                    e.getKey().equals("firstName") ? "name" : e.getKey(), 
                    e.getValue().toString()), HashMap::putAll);
        
        Response createResponse = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(newUser)
                .post();
        
        Assert.assertEquals(createResponse.getStatusCode(), 201, "Create User should succeed");
        String workflowUserId = createResponse.jsonPath().getString("id");
        ExtentReportManager.logPass("Step 2: Create User - Completed with ID: " + workflowUserId);
        
        // Step 3: Update User (PUT)
        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", newUser.get("name") + " Updated");
        updateUser.put("job", "Senior " + newUser.get("job"));
        
        Response updateResponse = ApiRequestBuilder.create()
                .withEndpoint("/api/users/" + workflowUserId)
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(updateUser)
                .put();
        
        Assert.assertEquals(updateResponse.getStatusCode(), 200, "Update User should succeed");
        ExtentReportManager.logPass("Step 3: Update User (PUT) - Completed");
        
        // Step 4: Partial Update (PATCH)
        Map<String, String> patchUser = new HashMap<>();
        patchUser.put("job", "Lead " + newUser.get("job"));
        
        Response patchResponse = ApiRequestBuilder.create()
                .withEndpoint("/api/users/" + workflowUserId)
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(patchUser)
                .patch();
        
        Assert.assertEquals(patchResponse.getStatusCode(), 200, "Patch User should succeed");
        ExtentReportManager.logPass("Step 4: Update User (PATCH) - Completed");
        
        // Step 5: Delete User
        Response deleteResponse = ApiRequestBuilder.create()
                .withEndpoint("/api/users/" + workflowUserId)
                .withHeader("x-api-key", config.getProperty("api.key"))
                .delete();
        
        Assert.assertEquals(deleteResponse.getStatusCode(), 204, "Delete User should succeed");
        ExtentReportManager.logPass("Step 5: Delete User - Completed");
        
        ExtentReportManager.logPass("Complete collection workflow executed successfully");
        logger.info("Complete collection workflow test completed successfully");
    }
    
    @Test(groups = {"regression", "collection", "datadriven"}, priority = 7)
    @Story("Data-Driven Collection Tests")
    @Description("Execute collection endpoints with multiple data sets")
    @Severity(SeverityLevel.NORMAL)
    public void testCollectionWithMultipleDataSets() {
        ExtentReportManager.logInfo("Testing collection endpoints with multiple data sets");
        
        // Test data sets
        String[][] testData = {
            {"Alice Johnson", "Frontend Developer"},
            {"Bob Smith", "Backend Developer"},
            {"Carol Brown", "DevOps Engineer"},
            {"David Wilson", "Data Scientist"}
        };
        
        for (String[] userData : testData) {
            String name = userData[0];
            String job = userData[1];
            
            ExtentReportManager.logInfo("Testing with data: " + name + " - " + job);
            
            // Create user
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("job", job);
            
            Response createResponse = userService.createUser(userMap.get("name"), userMap.get("job"));
            
            Assert.assertEquals(createResponse.getStatusCode(), 201, 
                              "User creation should succeed for: " + name);
            
            // Verify created user data
            Assert.assertEquals(createResponse.jsonPath().getString("name"), name, 
                              "Created user name should match");
            Assert.assertEquals(createResponse.jsonPath().getString("job"), job, 
                              "Created user job should match");
            
            ExtentReportManager.logPass("Data-driven test passed for: " + name);
        }
        
        ExtentReportManager.logPass("Data-driven collection tests completed successfully");
        logger.info("Data-driven collection tests completed");
    }
}
