package com.api.automation.services;

import com.api.automation.builders.ApiRequestBuilder;
import com.api.automation.models.User;
import com.api.automation.models.UserResponse;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for User API operations
 * Encapsulates all user-related API calls
 */
public class UserService {
    
    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_BY_ID_ENDPOINT = "/users/{id}";
    private final ObjectMapper objectMapper;
    
    public UserService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get all users with pagination
     */
    public Response getAllUsers(int page) {
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withQueryParam("page", page)
                .get();
    }
    
    /**
     * Get user by ID
     */
    public Response getUserById(int userId) {
        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .get();
    }
    
    /**
     * Create a new user
     */
    public Response createUser(User user) {
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withBody(user)
                .post();
    }
    
    /**
     * Create user with map payload
     */
    public Response createUser(String name, String job) {
        Map<String, String> userPayload = new HashMap<>();
        userPayload.put("name", name);
        userPayload.put("job", job);
        
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withBody(userPayload)
                .post();
    }
    
    /**
     * Update user (PUT - full update)
     */
    public Response updateUser(int userId, User user) {
        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .withBody(user)
                .put();
    }
    
    /**
     * Update user partially (PATCH)
     */
    public Response updateUserPartially(int userId, Map<String, Object> updates) {
        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .withBody(updates)
                .patch();
    }
    
    /**
     * Delete user
     */
    public Response deleteUser(int userId) {
        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .delete();
    }
    
    /**
     * Search users with filters
     */
    public Response searchUsers(Map<String, Object> filters) {
        ApiRequestBuilder builder = ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT);
        
        if (filters != null && !filters.isEmpty()) {
            builder.withQueryParams(filters);
        }
        
        return builder.get();
    }
    
    /**
     * Get users with authentication
     */
    public Response getAllUsersWithAuth(int page) {
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withQueryParam("page", page)
                .withAuth()
                .get();
    }
    
    /**
     * Bulk create users
     */
    public Response createMultipleUsers(User[] users) {
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT + "/bulk")
                .withBody(users)
                .post();
    }

    /**
     * Collection-specific methods for Postman collection automation
     */

    /**
     * Create user with collection-specific headers and data
     */
    public Response createUserFromCollection(String name, String job) {
        Map<String, String> userPayload = new HashMap<>();
        userPayload.put("name", name);
        userPayload.put("job", job);

        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withHeader("x-api-key", "reqres-free-v1")
                .withBody(userPayload)
                .post();
    }

    /**
     * Update user with PUT method (collection-specific)
     */
    public Response updateUserWithPutFromCollection(int userId, String name, String job) {
        Map<String, String> userPayload = new HashMap<>();
        userPayload.put("name", name);
        userPayload.put("job", job);

        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .withHeader("x-api-key", "reqres-free-v1")
                .withBody(userPayload)
                .put();
    }

    /**
     * Update user with PATCH method (collection-specific)
     */
    public Response updateUserWithPatchFromCollection(int userId, String job) {
        Map<String, String> userPayload = new HashMap<>();
        userPayload.put("job", job);

        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .withHeader("x-api-key", "reqres-free-v1")
                .withBody(userPayload)
                .patch();
    }

    /**
     * Delete user (collection-specific)
     */
    public Response deleteUserFromCollection(int userId) {
        return ApiRequestBuilder.create()
                .withEndpoint(USER_BY_ID_ENDPOINT)
                .withPathParam("id", userId)
                .withHeader("x-api-key", "reqres-free-v1")
                .delete();
    }

    /**
     * Get users with specific page (collection-specific)
     */
    public Response getUsersFromCollection(int page) {
        return ApiRequestBuilder.create()
                .withEndpoint(USERS_ENDPOINT)
                .withQueryParam("page", page)
                .get();
    }

    /**
     * Execute complete CRUD workflow
     */
    public Map<String, Response> executeCompleteWorkflow(String name, String job) {
        Map<String, Response> workflowResults = new HashMap<>();

        // Step 1: Get users
        workflowResults.put("getUsers", getUsersFromCollection(1));

        // Step 2: Create user
        Response createResponse = createUserFromCollection(name, job);
        workflowResults.put("createUser", createResponse);

        // Extract user ID for subsequent operations (simulate with fixed ID for demo API)
        int userId = 2; // Using fixed ID as demo API doesn't persist data

        // Step 3: Update user (PUT)
        workflowResults.put("updateUserPut",
            updateUserWithPutFromCollection(userId, name + " Updated", "Senior " + job));

        // Step 4: Update user (PATCH)
        workflowResults.put("updateUserPatch",
            updateUserWithPatchFromCollection(userId, "Lead " + job));

        // Step 5: Delete user
        workflowResults.put("deleteUser", deleteUserFromCollection(userId));

        return workflowResults;
    }

    /**
     * Validate collection response structure
     */
    public boolean validateCollectionResponse(Response response, String operation) {
        try {
            switch (operation.toLowerCase()) {
                case "get":
                    return response.getStatusCode() == 200 &&
                           response.jsonPath().get("data") != null;
                case "post":
                    return response.getStatusCode() == 201 &&
                           response.jsonPath().get("name") != null &&
                           response.jsonPath().get("job") != null &&
                           response.jsonPath().get("id") != null;
                case "put":
                case "patch":
                    return response.getStatusCode() == 200 &&
                           response.jsonPath().get("updatedAt") != null;
                case "delete":
                    return response.getStatusCode() == 204;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Helper methods for response parsing
    public UserResponse parseUserResponse(Response response) {
        try {
            return objectMapper.readValue(response.asString(), UserResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user response", e);
        }
    }

    public User parseUser(Response response) {
        try {
            return objectMapper.readValue(response.asString(), User.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user", e);
        }
    }
}
