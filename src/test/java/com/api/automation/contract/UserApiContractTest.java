package com.api.automation.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.api.automation.builders.ApiRequestBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Contract testing using Pact for User API
 * Defines consumer-driven contracts for API interactions
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserApiProvider")
public class UserApiContractTest {
    
    /**
     * Contract for getting all users
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact getAllUsersContract(PactDslWithProvider builder) {
        return builder
                .given("users exist")
                .uponReceiving("a request for all users")
                .path("/api/users")
                .method("GET")
                .query("page=1")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "page": 1,
                        "per_page": 6,
                        "total": 12,
                        "total_pages": 2,
                        "data": [
                            {
                                "id": 1,
                                "email": "george.bluth@reqres.in",
                                "first_name": "George",
                                "last_name": "Bluth",
                                "avatar": "https://reqres.in/img/faces/1-image.jpg"
                            }
                        ],
                        "support": {
                            "url": "https://reqres.in/#support-heading",
                            "text": "To keep ReqRes free, contributions towards server costs are appreciated!"
                        }
                    }
                    """)
                .toPact();
    }
    
    /**
     * Contract for getting a single user
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact getSingleUserContract(PactDslWithProvider builder) {
        return builder
                .given("user with id 2 exists")
                .uponReceiving("a request for user with id 2")
                .path("/api/users/2")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "data": {
                            "id": 2,
                            "email": "janet.weaver@reqres.in",
                            "first_name": "Janet",
                            "last_name": "Weaver",
                            "avatar": "https://reqres.in/img/faces/2-image.jpg"
                        },
                        "support": {
                            "url": "https://reqres.in/#support-heading",
                            "text": "To keep ReqRes free, contributions towards server costs are appreciated!"
                        }
                    }
                    """)
                .toPact();
    }
    
    /**
     * Contract for creating a new user
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact createUserContract(PactDslWithProvider builder) {
        return builder
                .given("user creation is allowed")
                .uponReceiving("a request to create a user")
                .path("/api/users")
                .method("POST")
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "name": "John Doe",
                        "job": "Software Engineer"
                    }
                    """)
                .willRespondWith()
                .status(201)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "name": "John Doe",
                        "job": "Software Engineer",
                        "id": "123",
                        "createdAt": "2023-01-01T00:00:00.000Z"
                    }
                    """)
                .toPact();
    }
    
    /**
     * Contract for updating a user
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact updateUserContract(PactDslWithProvider builder) {
        return builder
                .given("user with id 2 exists")
                .uponReceiving("a request to update user with id 2")
                .path("/api/users/2")
                .method("PUT")
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "name": "Jane Smith",
                        "job": "Senior Developer"
                    }
                    """)
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("""
                    {
                        "name": "Jane Smith",
                        "job": "Senior Developer",
                        "updatedAt": "2023-01-01T00:00:00.000Z"
                    }
                    """)
                .toPact();
    }
    
    /**
     * Contract for deleting a user
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact deleteUserContract(PactDslWithProvider builder) {
        return builder
                .given("user with id 2 exists")
                .uponReceiving("a request to delete user with id 2")
                .path("/api/users/2")
                .method("DELETE")
                .willRespondWith()
                .status(204)
                .toPact();
    }
    
    /**
     * Contract for user not found scenario
     */
    @Pact(consumer = "UserApiConsumer")
    public RequestResponsePact getUserNotFoundContract(PactDslWithProvider builder) {
        return builder
                .given("user with id 999 does not exist")
                .uponReceiving("a request for user with id 999")
                .path("/api/users/999")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body("{}")
                .toPact();
    }
    
    // Test methods
    
    @Test
    @PactTestFor(pactMethod = "getAllUsersContract")
    public void testGetAllUsersContract(MockServer mockServer) {
        // Arrange
        String baseUrl = mockServer.getUrl();
        
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withQueryParam("page", "1")
                .get();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("page"), equalTo(1));
        assertThat(response.jsonPath().getList("data"), hasSize(greaterThan(0)));
        assertThat(response.jsonPath().getString("data[0].email"), containsString("@reqres.in"));
    }
    
    @Test
    @PactTestFor(pactMethod = "getSingleUserContract")
    public void testGetSingleUserContract(MockServer mockServer) {
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .get();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("data.id"), equalTo(2));
        assertThat(response.jsonPath().getString("data.email"), equalTo("janet.weaver@reqres.in"));
        assertThat(response.jsonPath().getString("data.first_name"), equalTo("Janet"));
        assertThat(response.jsonPath().getString("data.last_name"), equalTo("Weaver"));
    }
    
    @Test
    @PactTestFor(pactMethod = "createUserContract")
    public void testCreateUserContract(MockServer mockServer) {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("name", "John Doe");
        userData.put("job", "Software Engineer");
        
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users")
                .withBody(userData)
                .post();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(201));
        assertThat(response.jsonPath().getString("name"), equalTo("John Doe"));
        assertThat(response.jsonPath().getString("job"), equalTo("Software Engineer"));
        assertThat(response.jsonPath().getString("id"), notNullValue());
        assertThat(response.jsonPath().getString("createdAt"), notNullValue());
    }
    
    @Test
    @PactTestFor(pactMethod = "updateUserContract")
    public void testUpdateUserContract(MockServer mockServer) {
        // Arrange
        Map<String, String> updateData = new HashMap<>();
        updateData.put("name", "Jane Smith");
        updateData.put("job", "Senior Developer");
        
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .withBody(updateData)
                .put();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("name"), equalTo("Jane Smith"));
        assertThat(response.jsonPath().getString("job"), equalTo("Senior Developer"));
        assertThat(response.jsonPath().getString("updatedAt"), notNullValue());
    }
    
    @Test
    @PactTestFor(pactMethod = "deleteUserContract")
    public void testDeleteUserContract(MockServer mockServer) {
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/2")
                .delete();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(204));
    }
    
    @Test
    @PactTestFor(pactMethod = "getUserNotFoundContract")
    public void testGetUserNotFoundContract(MockServer mockServer) {
        // Act
        Response response = ApiRequestBuilder.create()
                .withEndpoint("/api/users/999")
                .get();
        
        // Assert
        assertThat(response.getStatusCode(), equalTo(404));
    }
}
