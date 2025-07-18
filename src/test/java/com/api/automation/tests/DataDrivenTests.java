package com.api.automation.tests;

import com.api.automation.base.BaseTest;
import com.api.automation.utils.AssertionUtils;
import com.api.automation.utils.JsonUtils;
import com.api.automation.utils.TestDataFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Data-driven test class demonstrating various testing approaches
 */
@Epic("Data-Driven Testing")
@Feature("API Data Testing")
public class DataDrivenTests extends BaseTest {

    private static final String TEST_DATA_FILE = "src/test/resources/testdata/test-data.json";

    @DataProvider(name = "userCreationData")
    public Object[][] getUserCreationData() throws IOException {
        String testDataJson = JsonUtils.readJsonFromFile(TEST_DATA_FILE);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> testData = mapper.readValue(testDataJson, new TypeReference<Map<String, Object>>(){});
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> scenarios = (List<Map<String, Object>>) 
            ((Map<String, Object>) testData.get("testScenarios")).get("userCreation");
        
        Object[][] data = new Object[scenarios.size()][3];
        for (int i = 0; i < scenarios.size(); i++) {
            Map<String, Object> scenario = scenarios.get(i);
            data[i][0] = scenario.get("name");
            data[i][1] = scenario.get("job");
            data[i][2] = scenario.get("expectedStatus");
        }
        
        return data;
    }

    @DataProvider(name = "randomUserData")
    public Object[][] getRandomUserData() {
        Object[][] data = new Object[3][1];
        for (int i = 0; i < 3; i++) {
            data[i][0] = TestDataFactory.generateRandomUser();
        }
        return data;
    }

    @Test(dataProvider = "userCreationData", groups = {"regression", "data-driven"})
    @Story("Data-driven user creation")
    @Description("Test user creation with various data scenarios from external file")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithDataProvider(String name, String job, int expectedStatus) {
        logTestInfo("testCreateUserWithDataProvider", 
            "Testing user creation with name: " + name + ", job: " + job);
        
        Map<String, Object> userPayload = TestDataFactory.generateUserWithAttributes(name, job, null);
        
        Response response = given()
                .spec(requestSpec)
                .body(userPayload)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();

        AssertionUtils.validateStatusCode(response, expectedStatus);
        
        if (expectedStatus == 201) {
            AssertionUtils.validateRequiredFields(response, "name", "job", "id", "createdAt");
            AssertionUtils.validateFieldValue(response, "name", name);
            AssertionUtils.validateFieldValue(response, "job", job);
        } else if (expectedStatus == 400) {
            AssertionUtils.validateErrorResponse(response, 400);
        }
        
        logger.info("Data-driven test completed for user: " + name);
    }

    @Test(dataProvider = "randomUserData", groups = {"regression", "data-driven"})
    @Story("Random user creation")
    @Description("Test user creation with randomly generated data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithRandomData(Map<String, Object> userData) {
        logTestInfo("testCreateUserWithRandomData", 
            "Testing user creation with random data: " + userData.get("name"));
        
        Response response = given()
                .spec(requestSpec)
                .body(userData)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();

        AssertionUtils.validateStatusCode(response, 201);
        AssertionUtils.validateRequiredFields(response, "name", "job", "id", "createdAt");
        AssertionUtils.validateFieldValue(response, "name", userData.get("name"));
        AssertionUtils.validateFieldValue(response, "job", userData.get("job"));
        AssertionUtils.validateResponseTime(response, 5000);
        
        logger.info("Random data test completed for user: " + userData.get("name"));
    }

    @Test(groups = {"regression", "data-driven", "negative"})
    @Story("Bulk user creation validation")
    @Description("Test creating multiple users and validate consistency")
    @Severity(SeverityLevel.NORMAL)
    public void testBulkUserCreation() {
        logTestInfo("testBulkUserCreation", "Testing bulk user creation");
        
        int numberOfUsers = 5;
        String[] createdUserIds = new String[numberOfUsers];
        
        for (int i = 0; i < numberOfUsers; i++) {
            Map<String, Object> userData = TestDataFactory.generateRandomUser();
            
            Response response = given()
                    .spec(requestSpec)
                    .body(userData)
                    .when()
                    .post("/users")
                    .then()
                    .extract()
                    .response();

            AssertionUtils.validateStatusCode(response, 201);
            AssertionUtils.validateRequiredFields(response, "name", "job", "id", "createdAt");
            
            String userId = JsonUtils.extractValueFromResponse(response, "id");
            createdUserIds[i] = userId;
            
            logger.info("Created user " + (i + 1) + " with ID: " + userId);
        }
        
        // Validate all users were created with unique IDs
        for (int i = 0; i < numberOfUsers; i++) {
            for (int j = i + 1; j < numberOfUsers; j++) {
                assert !createdUserIds[i].equals(createdUserIds[j]) : 
                    "User IDs should be unique, but found duplicate: " + createdUserIds[i];
            }
        }
        
        logger.info("Bulk user creation test completed successfully");
    }

    @Test(groups = {"regression", "data-driven"})
    @Story("Invalid data scenarios")
    @Description("Test various invalid data scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidDataScenarios() {
        logTestInfo("testInvalidDataScenarios", "Testing various invalid data scenarios");
        
        // Test with different types of invalid data
        for (int i = 0; i < 3; i++) {
            Map<String, Object> invalidData = TestDataFactory.generateInvalidUserData();
            
            Response response = given()
                    .spec(requestSpec)
                    .body(invalidData)
                    .when()
                    .post("/users")
                    .then()
                    .extract()
                    .response();

            // API might return 201 even for some "invalid" data, or 400 for truly invalid data
            int statusCode = response.getStatusCode();
            assert statusCode == 201 || statusCode == 400 || statusCode == 422 : 
                "Status code should be 201, 400, or 422, but was: " + statusCode;
            
            logger.info("Invalid data scenario " + (i + 1) + " tested with status: " + statusCode);
        }
    }

    @Test(groups = {"performance", "data-driven"})
    @Story("Data volume performance")
    @Description("Test API performance with varying data sizes")
    @Severity(SeverityLevel.MINOR)
    public void testDataVolumePerformance() {
        logTestInfo("testDataVolumePerformance", "Testing performance with different data volumes");
        
        // Test with different sizes of data
        int[] dataSizes = {10, 100, 1000}; // characters in name field
        
        for (int size : dataSizes) {
            String largeName = TestDataFactory.generateRandomString(size);
            Map<String, Object> userData = TestDataFactory.generateUserWithAttributes(
                largeName, "Test Job", null);
            
            long startTime = System.currentTimeMillis();
            
            Response response = given()
                    .spec(requestSpec)
                    .body(userData)
                    .when()
                    .post("/users")
                    .then()
                    .extract()
                    .response();
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Most APIs should handle reasonable data sizes
            AssertionUtils.validateStatusCode(response, 201);
            AssertionUtils.validateResponseTime(response, 10000); // 10 seconds max
            
            logger.info("Data size " + size + " processed in " + responseTime + "ms");
        }
    }
}
