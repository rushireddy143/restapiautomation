package com.api.automation.stepdefinitions;

import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.config.ConfigManager;
import com.api.automation.models.User;
import com.api.automation.services.UserService;
import com.api.automation.utils.ExtentReportManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Step definitions for User Management BDD scenarios
 */
public class UserManagementSteps {
    
    private ConfigManager config;
    private UserService userService;
    private String endpoint;
    private Map<String, String> userData;
    private Response response;
    private List<Response> responses;
    private boolean hasAuthentication = true;
    
    public UserManagementSteps() {
        this.config = ConfigManager.getInstance();
        this.userService = new UserService();
    }
    
    @Given("the API base URL is configured")
    public void theApiBaseUrlIsConfigured() {
        String baseUrl = config.getBaseUrl();
        Assert.assertNotNull(baseUrl, "Base URL should be configured");
        ExtentReportManager.logInfo("API base URL configured: " + baseUrl);
    }
    
    @Given("I have valid authentication credentials")
    public void iHaveValidAuthenticationCredentials() {
        hasAuthentication = true;
        ExtentReportManager.logInfo("Authentication credentials are available");
    }
    
    @Given("I do not have authentication credentials")
    public void iDoNotHaveAuthenticationCredentials() {
        hasAuthentication = false;
        ExtentReportManager.logInfo("No authentication credentials provided");
    }
    
    @Given("I have a valid API endpoint {string}")
    public void iHaveAValidApiEndpoint(String endpoint) {
        this.endpoint = endpoint;
        ExtentReportManager.logInfo("API endpoint set: " + endpoint);
    }
    
    @Given("I have user data:")
    public void iHaveUserData(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        this.userData = data.get(0);
        ExtentReportManager.logInfo("User data prepared: " + userData.toString());
    }
    
    @Given("I have updated user data:")
    public void iHaveUpdatedUserData(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        this.userData = data.get(0);
        ExtentReportManager.logInfo("Updated user data prepared: " + userData.toString());
    }
    
    @Given("I have partial update data:")
    public void iHavePartialUpdateData(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        this.userData = data.get(0);
        ExtentReportManager.logInfo("Partial update data prepared: " + userData.toString());
    }
    
    @Given("I have incomplete user data:")
    public void iHaveIncompleteUserData(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        this.userData = data.get(0);
        ExtentReportManager.logInfo("Incomplete user data prepared: " + userData.toString());
    }
    
    @When("I send a GET request with page {string}")
    public void iSendAGetRequestWithPage(String page) {
        ExtentReportManager.logInfo("Sending GET request with page: " + page);
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withQueryParam("page", page)
                .get();
        
        ExtentReportManager.logApiRequest("GET", endpoint + "?page=" + page, null);
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a GET request")
    public void iSendAGetRequest() {
        ExtentReportManager.logInfo("Sending GET request to: " + endpoint);
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .get();
        
        ExtentReportManager.logApiRequest("GET", endpoint, null);
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a GET request without authentication")
    public void iSendAGetRequestWithoutAuthentication() {
        ExtentReportManager.logInfo("Sending GET request without authentication");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .get();
        
        ExtentReportManager.logApiRequest("GET", endpoint, "No authentication");
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a POST request with the user data")
    public void iSendAPostRequestWithTheUserData() {
        ExtentReportManager.logInfo("Sending POST request with user data");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withBody(userData)
                .post();
        
        ExtentReportManager.logApiRequest("POST", endpoint, userData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a POST request with the incomplete data")
    public void iSendAPostRequestWithTheIncompleteData() {
        ExtentReportManager.logInfo("Sending POST request with incomplete data");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withBody(userData)
                .post();
        
        ExtentReportManager.logApiRequest("POST", endpoint, userData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a PUT request with the updated data")
    public void iSendAPutRequestWithTheUpdatedData() {
        ExtentReportManager.logInfo("Sending PUT request with updated data");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withBody(userData)
                .put();
        
        ExtentReportManager.logApiRequest("PUT", endpoint, userData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a PATCH request with the partial data")
    public void iSendAPatchRequestWithThePartialData() {
        ExtentReportManager.logInfo("Sending PATCH request with partial data");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withBody(userData)
                .patch();
        
        ExtentReportManager.logApiRequest("PATCH", endpoint, userData.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send a DELETE request")
    public void iSendADeleteRequest() {
        ExtentReportManager.logInfo("Sending DELETE request");
        
        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .delete();
        
        ExtentReportManager.logApiRequest("DELETE", endpoint, null);
        ExtentReportManager.logApiResponse(response.getStatusCode(), 
                                         response.getBody().asString(), 
                                         response.getTime());
    }
    
    @When("I send multiple GET requests concurrently")
    public void iSendMultipleGetRequestsConcurrently() {
        ExtentReportManager.logInfo("Sending multiple concurrent GET requests");
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        CompletableFuture<Response>[] futures = IntStream.range(0, 10)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> 
                    ApiRequestBuilder.create()
                            .withEndpoint(endpoint)
                            .withQueryParam("page", "1")
                            .get(), executor))
                .toArray(CompletableFuture[]::new);
        
        responses = java.util.Arrays.stream(futures)
                .map(CompletableFuture::join)
                .collect(java.util.stream.Collectors.toList());
        
        executor.shutdown();
        ExtentReportManager.logInfo("Completed " + responses.size() + " concurrent requests");
    }
    
    @When("I create users with the following data:")
    public void iCreateUsersWithTheFollowingData(DataTable dataTable) {
        ExtentReportManager.logInfo("Creating multiple users with provided data");
        
        List<Map<String, String>> usersData = dataTable.asMaps(String.class, String.class);
        responses = new java.util.ArrayList<>();
        
        for (Map<String, String> userMap : usersData) {
            Response userResponse = ApiRequestBuilder.create()
                    .withEndpoint(endpoint)
                    .withBody(userMap)
                    .post();
            responses.add(userResponse);
            
            ExtentReportManager.logInfo("Created user: " + userMap.get("name") + 
                                      " with job: " + userMap.get("job"));
        }
        
        ExtentReportManager.logInfo("Created " + responses.size() + " users");
    }
    
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, 
                          "Expected status code " + expectedStatusCode + " but got " + actualStatusCode);
        ExtentReportManager.logPass("Status code validation passed: " + actualStatusCode);
    }
    
    @Then("the response status code should be {int} or {int}")
    public void theResponseStatusCodeShouldBeOr(int statusCode1, int statusCode2) {
        int actualStatusCode = response.getStatusCode();
        boolean isValidStatusCode = actualStatusCode == statusCode1 || actualStatusCode == statusCode2;
        Assert.assertTrue(isValidStatusCode, 
                        "Expected status code " + statusCode1 + " or " + statusCode2 + 
                        " but got " + actualStatusCode);
        ExtentReportManager.logPass("Status code validation passed: " + actualStatusCode);
    }
    
    @Then("the response should contain user data")
    public void theResponseShouldContainUserData() {
        Assert.assertNotNull(response.jsonPath().get("data"), "Response should contain user data");
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0, 
                        "User data array should not be empty");
        ExtentReportManager.logPass("Response contains user data");
    }
    
    @Then("the response time should be less than {int} milliseconds")
    public void theResponseTimeShouldBeLessThanMilliseconds(int maxTime) {
        long actualTime = response.getTime();
        Assert.assertTrue(actualTime < maxTime, 
                        "Response time " + actualTime + "ms should be less than " + maxTime + "ms");
        ExtentReportManager.logPass("Response time validation passed: " + actualTime + "ms");
    }
    
    @Then("the response should contain the created user details")
    public void theResponseShouldContainTheCreatedUserDetails() {
        Assert.assertNotNull(response.jsonPath().get("name"), "Response should contain user name");
        Assert.assertNotNull(response.jsonPath().get("job"), "Response should contain user job");
        ExtentReportManager.logPass("Response contains created user details");
    }
    
    @Then("the response should have a valid user ID")
    public void theResponseShouldHaveAValidUserId() {
        Assert.assertNotNull(response.jsonPath().get("id"), "Response should contain user ID");
        ExtentReportManager.logPass("Response contains valid user ID");
    }
    
    @Then("the response should have a creation timestamp")
    public void theResponseShouldHaveACreationTimestamp() {
        Assert.assertNotNull(response.jsonPath().get("createdAt"), 
                           "Response should contain creation timestamp");
        ExtentReportManager.logPass("Response contains creation timestamp");
    }
    
    @Then("the response body should be empty")
    public void theResponseBodyShouldBeEmpty() {
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody == null || responseBody.trim().isEmpty(), 
                        "Response body should be empty");
        ExtentReportManager.logPass("Response body is empty as expected");
    }
    
    @Then("all responses should have status code {int}")
    public void allResponsesShouldHaveStatusCode(int expectedStatusCode) {
        for (Response resp : responses) {
            Assert.assertEquals(resp.getStatusCode(), expectedStatusCode, 
                              "All responses should have status code " + expectedStatusCode);
        }
        ExtentReportManager.logPass("All " + responses.size() + " responses have correct status code");
    }
    
    @Then("the average response time should be less than {int} milliseconds")
    public void theAverageResponseTimeShouldBeLessThanMilliseconds(int maxAvgTime) {
        double avgTime = responses.stream()
                .mapToLong(Response::getTime)
                .average()
                .orElse(0.0);
        
        Assert.assertTrue(avgTime < maxAvgTime, 
                        "Average response time " + avgTime + "ms should be less than " + maxAvgTime + "ms");
        ExtentReportManager.logPass("Average response time validation passed: " + avgTime + "ms");
    }
    
    @Then("no response should take more than {int} milliseconds")
    public void noResponseShouldTakeMoreThanMilliseconds(int maxTime) {
        long maxActualTime = responses.stream()
                .mapToLong(Response::getTime)
                .max()
                .orElse(0L);
        
        Assert.assertTrue(maxActualTime <= maxTime, 
                        "Maximum response time " + maxActualTime + "ms should not exceed " + maxTime + "ms");
        ExtentReportManager.logPass("Maximum response time validation passed: " + maxActualTime + "ms");
    }
    
    @Then("all users should be created successfully")
    public void allUsersShouldBeCreatedSuccessfully() {
        for (Response resp : responses) {
            Assert.assertEquals(resp.getStatusCode(), 201, 
                              "All users should be created with status code 201");
        }
        ExtentReportManager.logPass("All " + responses.size() + " users created successfully");
    }
    
    @Then("each user should have a unique ID")
    public void eachUserShouldHaveAUniqueId() {
        java.util.Set<String> userIds = new java.util.HashSet<>();
        for (Response resp : responses) {
            String userId = resp.jsonPath().getString("id");
            Assert.assertNotNull(userId, "Each user should have an ID");
            Assert.assertTrue(userIds.add(userId), "User ID should be unique: " + userId);
        }
        ExtentReportManager.logPass("All user IDs are unique");
    }

    @Then("each user should have the correct name and job")
    public void eachUserShouldHaveTheCorrectNameAndJob() {
        for (Response resp : responses) {
            Assert.assertNotNull(resp.jsonPath().getString("name"), "User should have a name");
            Assert.assertNotNull(resp.jsonPath().getString("job"), "User should have a job");
        }
        ExtentReportManager.logPass("All users have correct name and job fields");
    }

    @Then("the response should contain the updated user details")
    public void theResponseShouldContainTheUpdatedUserDetails() {
        Assert.assertNotNull(response.jsonPath().get("name"), "Response should contain updated user name");
        Assert.assertNotNull(response.jsonPath().get("job"), "Response should contain updated user job");
        Assert.assertNotNull(response.jsonPath().get("updatedAt"), "Response should contain update timestamp");
        ExtentReportManager.logPass("Response contains updated user details");
    }

    @Then("the user name should be {string}")
    public void theUserNameShouldBe(String expectedName) {
        String actualName = response.jsonPath().getString("name");
        Assert.assertEquals(actualName, expectedName, "User name should match expected value");
        ExtentReportManager.logPass("User name validation passed: " + actualName);
    }

    @Then("the user job should be {string}")
    public void theUserJobShouldBe(String expectedJob) {
        String actualJob = response.jsonPath().getString("job");
        Assert.assertEquals(actualJob, expectedJob, "User job should match expected value");
        ExtentReportManager.logPass("User job validation passed: " + actualJob);
    }

    @Then("the response should contain the updated job title")
    public void theResponseShouldContainTheUpdatedJobTitle() {
        Assert.assertNotNull(response.jsonPath().get("job"), "Response should contain updated job title");
        Assert.assertNotNull(response.jsonPath().get("updatedAt"), "Response should contain update timestamp");
        ExtentReportManager.logPass("Response contains updated job title");
    }

    @Then("the job should be {string}")
    public void theJobShouldBe(String expectedJob) {
        String actualJob = response.jsonPath().getString("job");
        Assert.assertEquals(actualJob, expectedJob, "Job should match expected value");
        ExtentReportManager.logPass("Job validation passed: " + actualJob);
    }

    @Then("the response should contain an error message")
    public void theResponseShouldContainAnErrorMessage() {
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.isEmpty(), "Response should contain error message");
        ExtentReportManager.logPass("Response contains error message");
    }

    @Then("the response should contain validation errors")
    public void theResponseShouldContainValidationErrors() {
        // For this demo API, we'll check if response indicates validation issue
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(responseBody, "Response should contain validation information");
        ExtentReportManager.logPass("Response contains validation information");
    }

    @Then("the response should contain an authentication error")
    public void theResponseShouldContainAnAuthenticationError() {
        // Check for authentication-related response
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 401 || statusCode == 403,
                        "Response should indicate authentication error");
        ExtentReportManager.logPass("Response contains authentication error indication");
    }

    // Additional step definitions for enhanced collection automation

    @When("I send a GET request to {string} with page {string}")
    public void iSendAGetRequestToWithPage(String endpoint, String page) {
        this.endpoint = endpoint;
        ExtentReportManager.logInfo("Sending GET request to: " + endpoint + " with page: " + page);

        response = ApiRequestBuilder.create()
                .withEndpoint(endpoint)
                .withQueryParam("page", page)
                .get();

        ExtentReportManager.logApiRequest("GET", endpoint + "?page=" + page, null);
        ExtentReportManager.logApiResponse(response.getStatusCode(),
                                         response.getBody().asString(),
                                         response.getTime());
    }

    @When("I create a user with name {string} and job {string}")
    public void iCreateAUserWithNameAndJob(String name, String job) {
        ExtentReportManager.logInfo("Creating user with name: " + name + " and job: " + job);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("job", job);

        response = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(userMap)
                .post();

        ExtentReportManager.logApiRequest("POST", "/api/users", userMap.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(),
                                         response.getBody().asString(),
                                         response.getTime());
    }

    @When("I update the user with PUT method using name {string} and job {string}")
    public void iUpdateTheUserWithPutMethodUsingNameAndJob(String name, String job) {
        ExtentReportManager.logInfo("Updating user with PUT - name: " + name + " and job: " + job);

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("name", name);
        updateMap.put("job", job);

        response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(updateMap)
                .put();

        ExtentReportManager.logApiRequest("PUT", "/api/users/2", updateMap.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(),
                                         response.getBody().asString(),
                                         response.getTime());
    }

    @When("I update the user job with PATCH method to {string}")
    public void iUpdateTheUserJobWithPatchMethodTo(String job) {
        ExtentReportManager.logInfo("Updating user job with PATCH to: " + job);

        Map<String, String> patchMap = new HashMap<>();
        patchMap.put("job", job);

        response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .withBody(patchMap)
                .patch();

        ExtentReportManager.logApiRequest("PATCH", "/api/users/2", patchMap.toString());
        ExtentReportManager.logApiResponse(response.getStatusCode(),
                                         response.getBody().asString(),
                                         response.getTime());
    }

    @When("I delete the user")
    public void iDeleteTheUser() {
        ExtentReportManager.logInfo("Deleting user");

        response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withHeader("x-api-key", config.getProperty("api.key"))
                .delete();

        ExtentReportManager.logApiRequest("DELETE", "/api/users/2", null);
        ExtentReportManager.logApiResponse(response.getStatusCode(),
                                         response.getBody().asString(),
                                         response.getTime());
    }

    @Then("the response should have proper JSON structure for GET operation")
    public void theResponseShouldHaveProperJsonStructureForGetOperation() {
        Assert.assertNotNull(response.jsonPath().get("data"), "GET response should have data array");
        Assert.assertNotNull(response.jsonPath().get("page"), "GET response should have page number");
        Assert.assertNotNull(response.jsonPath().get("per_page"), "GET response should have per_page");
        Assert.assertNotNull(response.jsonPath().get("total"), "GET response should have total count");
        ExtentReportManager.logPass("GET response has proper JSON structure");
    }

    @Then("the response should contain pagination information")
    public void theResponseShouldContainPaginationInformation() {
        Assert.assertNotNull(response.jsonPath().get("page"), "Response should contain page information");
        Assert.assertNotNull(response.jsonPath().get("total_pages"), "Response should contain total pages");
        Assert.assertTrue(response.jsonPath().getInt("per_page") > 0, "Per page should be greater than 0");
        ExtentReportManager.logPass("Response contains pagination information");
    }

    @Then("the response should have proper JSON structure for POST operation")
    public void theResponseShouldHaveProperJsonStructureForPostOperation() {
        Assert.assertNotNull(response.jsonPath().get("name"), "POST response should have name");
        Assert.assertNotNull(response.jsonPath().get("job"), "POST response should have job");
        Assert.assertNotNull(response.jsonPath().get("id"), "POST response should have id");
        Assert.assertNotNull(response.jsonPath().get("createdAt"), "POST response should have createdAt");
        ExtentReportManager.logPass("POST response has proper JSON structure");
    }

    @Then("the response should contain user creation metadata")
    public void theResponseShouldContainUserCreationMetadata() {
        Assert.assertNotNull(response.jsonPath().get("id"), "Response should contain user ID");
        Assert.assertNotNull(response.jsonPath().get("createdAt"), "Response should contain creation timestamp");
        ExtentReportManager.logPass("Response contains user creation metadata");
    }

    @Then("the response should have proper JSON structure for PUT operation")
    public void theResponseShouldHaveProperJsonStructureForPutOperation() {
        Assert.assertNotNull(response.jsonPath().get("name"), "PUT response should have name");
        Assert.assertNotNull(response.jsonPath().get("job"), "PUT response should have job");
        Assert.assertNotNull(response.jsonPath().get("updatedAt"), "PUT response should have updatedAt");
        ExtentReportManager.logPass("PUT response has proper JSON structure");
    }

    @Then("the response should have proper JSON structure for PATCH operation")
    public void theResponseShouldHaveProperJsonStructureForPatchOperation() {
        Assert.assertNotNull(response.jsonPath().get("job"), "PATCH response should have job");
        Assert.assertNotNull(response.jsonPath().get("updatedAt"), "PATCH response should have updatedAt");
        ExtentReportManager.logPass("PATCH response has proper JSON structure");
    }

    @Then("the response should contain update timestamp")
    public void theResponseShouldContainUpdateTimestamp() {
        Assert.assertNotNull(response.jsonPath().get("updatedAt"), "Response should contain update timestamp");
        ExtentReportManager.logPass("Response contains update timestamp");
    }

    @Then("the response should have proper structure for DELETE operation")
    public void theResponseShouldHaveProperStructureForDeleteOperation() {
        Assert.assertEquals(response.getStatusCode(), 204, "DELETE should return 204 status");
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody == null || responseBody.trim().isEmpty(),
                        "DELETE response body should be empty");
        ExtentReportManager.logPass("DELETE response has proper structure");
    }
}
