package com.api.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON operations and data handling
 */
public class JsonUtils {
    
    private static final Logger logger = LogManager.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Convert object to JSON string
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Failed to convert object to JSON string", e);
            throw new RuntimeException("JSON conversion failed", e);
        }
    }
    
    /**
     * Convert JSON string to object
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.error("Failed to convert JSON string to object", e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }
    
    /**
     * Read JSON from file
     */
    public static String readJsonFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            logger.error("Failed to read JSON from file: " + filePath, e);
            throw new RuntimeException("File reading failed", e);
        }
    }
    
    /**
     * Write JSON to file
     */
    public static void writeJsonToFile(Object object, String filePath) {
        try {
            objectMapper.writeValue(new File(filePath), object);
        } catch (IOException e) {
            logger.error("Failed to write JSON to file: " + filePath, e);
            throw new RuntimeException("File writing failed", e);
        }
    }
    
    /**
     * Extract value from JSON response using JsonPath
     */
    public static <T> T extractValueFromResponse(Response response, String jsonPath) {
        return response.jsonPath().get(jsonPath);
    }
    
    /**
     * Extract list of values from JSON response
     */
    public static <T> List<T> extractListFromResponse(Response response, String jsonPath) {
        return response.jsonPath().getList(jsonPath);
    }
    
    /**
     * Validate JSON structure against expected keys
     */
    public static boolean validateJsonKeys(Response response, String... expectedKeys) {
        JsonPath jsonPath = response.jsonPath();
        for (String key : expectedKeys) {
            if (jsonPath.get(key) == null) {
                logger.error("Missing expected key: " + key);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Pretty print JSON string
     */
    public static String prettyPrintJson(String jsonString) {
        try {
            Object obj = gson.fromJson(jsonString, Object.class);
            return gson.toJson(obj);
        } catch (Exception e) {
            logger.error("Failed to pretty print JSON", e);
            return jsonString;
        }
    }
    
    /**
     * Compare two JSON objects
     */
    public static boolean compareJsonObjects(String json1, String json2) {
        try {
            Map<String, Object> map1 = objectMapper.readValue(json1, Map.class);
            Map<String, Object> map2 = objectMapper.readValue(json2, Map.class);
            return map1.equals(map2);
        } catch (Exception e) {
            logger.error("Failed to compare JSON objects", e);
            return false;
        }
    }
}
