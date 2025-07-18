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
}
