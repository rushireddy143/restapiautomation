package com.api.automation.builders;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.api.automation.config.ConfigManager;
import com.api.automation.utils.SpecificationBuilder;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Builder pattern for API requests - Fluent interface for API calls
 */
public class ApiRequestBuilder {
    
    private RequestSpecification requestSpec;
    private String endpoint;
    private Object requestBody;
    private Map<String, String> headers;
    private Map<String, Object> queryParams;
    private Map<String, Object> pathParams;
    
    public ApiRequestBuilder() {
        this.requestSpec = SpecificationBuilder.getRequestSpecification();
    }
    
    public static ApiRequestBuilder create() {
        return new ApiRequestBuilder();
    }
    
    public ApiRequestBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
    
    public ApiRequestBuilder withBody(Object body) {
        this.requestBody = body;
        return this;
    }
    
    public ApiRequestBuilder withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
    
    public ApiRequestBuilder withHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new java.util.HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }
    
    public ApiRequestBuilder withQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
        return this;
    }
    
    public ApiRequestBuilder withQueryParam(String key, Object value) {
        if (this.queryParams == null) {
            this.queryParams = new java.util.HashMap<>();
        }
        this.queryParams.put(key, value);
        return this;
    }
    
    public ApiRequestBuilder withPathParams(Map<String, Object> pathParams) {
        this.pathParams = pathParams;
        return this;
    }
    
    public ApiRequestBuilder withPathParam(String key, Object value) {
        if (this.pathParams == null) {
            this.pathParams = new java.util.HashMap<>();
        }
        this.pathParams.put(key, value);
        return this;
    }
    
    public ApiRequestBuilder withAuth() {
        this.requestSpec = SpecificationBuilder.getRequestSpecificationWithAuth();
        return this;
    }
    
    public ApiRequestBuilder withBasicAuth(String username, String password) {
        this.requestSpec = SpecificationBuilder.getRequestSpecificationWithBasicAuth()
                .auth().basic(username, password);
        return this;
    }
    
    public ApiRequestBuilder withContentType(ContentType contentType) {
        this.requestSpec = this.requestSpec.contentType(contentType);
        return this;
    }
    
    private RequestSpecification buildRequest() {
        RequestSpecification request = given().spec(requestSpec);
        
        if (requestBody != null) {
            request = request.body(requestBody);
        }
        
        if (headers != null) {
            request = request.headers(headers);
        }
        
        if (queryParams != null) {
            request = request.queryParams(queryParams);
        }
        
        if (pathParams != null) {
            request = request.pathParams(pathParams);
        }
        
        return request;
    }
    
    // HTTP Methods
    public Response get() {
        return buildRequest().when().get(endpoint);
    }
    
    public Response post() {
        return buildRequest().when().post(endpoint);
    }
    
    public Response put() {
        return buildRequest().when().put(endpoint);
    }
    
    public Response patch() {
        return buildRequest().when().patch(endpoint);
    }
    
    public Response delete() {
        return buildRequest().when().delete(endpoint);
    }
    
    // Fluent execution methods
    public Response execute(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return get();
            case "POST":
                return post();
            case "PUT":
                return put();
            case "PATCH":
                return patch();
            case "DELETE":
                return delete();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
