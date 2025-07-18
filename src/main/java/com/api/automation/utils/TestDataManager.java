package com.api.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Enhanced test data manager supporting multiple data sources
 * JSON, Excel, Database, and Faker-generated data
 */
public class TestDataManager {
    
    private static final Logger logger = LogManager.getLogger(TestDataManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Faker faker = new Faker();
    private static TestDataManager instance;
    
    private TestDataManager() {}
    
    public static TestDataManager getInstance() {
        if (instance == null) {
            synchronized (TestDataManager.class) {
                if (instance == null) {
                    instance = new TestDataManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load test data from JSON file
     */
    public Map<String, Object> loadJsonTestData(String filePath) {
        try {
            JsonNode jsonNode = objectMapper.readTree(new FileInputStream(filePath));
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (IOException e) {
            logger.error("Failed to load JSON test data from: {}", filePath, e);
            throw new RuntimeException("Failed to load JSON test data", e);
        }
    }
    
    /**
     * Load test data from Excel file
     */
    public List<Map<String, Object>> loadExcelTestData(String filePath, String sheetName) {
        List<Map<String, Object>> testData = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file");
            }
            
            // Get header row
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }
            
            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, Object> rowData = new HashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        Object cellValue = getCellValue(cell);
                        rowData.put(headers.get(j), cellValue);
                    }
                    testData.add(rowData);
                }
            }
            
            logger.info("Loaded {} rows of test data from Excel file: {}", testData.size(), filePath);
            
        } catch (IOException e) {
            logger.error("Failed to load Excel test data from: {}", filePath, e);
            throw new RuntimeException("Failed to load Excel test data", e);
        }
        
        return testData;
    }
    
    /**
     * Get cell value based on cell type
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    /**
     * Generate fake user data
     */
    public Map<String, Object> generateFakeUserData() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", faker.name().firstName());
        userData.put("lastName", faker.name().lastName());
        userData.put("email", faker.internet().emailAddress());
        userData.put("phone", faker.phoneNumber().phoneNumber());
        userData.put("address", faker.address().fullAddress());
        userData.put("company", faker.company().name());
        userData.put("job", faker.job().title());
        userData.put("username", faker.name().username());
        userData.put("password", faker.internet().password(8, 16));
        return userData;
    }
    
    /**
     * Generate fake product data
     */
    public Map<String, Object> generateFakeProductData() {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", faker.commerce().productName());
        productData.put("description", faker.lorem().sentence());
        productData.put("price", faker.number().randomDouble(2, 10, 1000));
        productData.put("category", faker.commerce().department());
        productData.put("brand", faker.company().name());
        productData.put("sku", faker.code().ean13());
        productData.put("inStock", faker.bool().bool());
        return productData;
    }
    
    /**
     * Generate random string
     */
    public String generateRandomString(int length) {
        return faker.lorem().characters(length);
    }
    
    /**
     * Generate random number
     */
    public int generateRandomNumber(int min, int max) {
        return faker.number().numberBetween(min, max);
    }
    
    /**
     * Generate random email
     */
    public String generateRandomEmail() {
        return faker.internet().emailAddress();
    }
    
    /**
     * Generate random phone number
     */
    public String generateRandomPhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }
    
    /**
     * Generate test data with specific pattern
     */
    public Map<String, Object> generateTestDataWithPattern(String pattern) {
        Map<String, Object> data = new HashMap<>();
        
        switch (pattern.toLowerCase()) {
            case "user":
                return generateFakeUserData();
            case "product":
                return generateFakeProductData();
            case "minimal":
                data.put("id", faker.number().randomNumber());
                data.put("name", faker.name().name());
                return data;
            default:
                logger.warn("Unknown pattern: {}. Generating minimal data.", pattern);
                return generateTestDataWithPattern("minimal");
        }
    }
    
    /**
     * Get test data by key from properties
     */
    public String getTestDataFromProperties(String key) {
        return System.getProperty(key, "");
    }
    
    /**
     * Merge multiple data sources
     */
    public Map<String, Object> mergeTestData(Map<String, Object>... dataSources) {
        Map<String, Object> mergedData = new HashMap<>();
        for (Map<String, Object> dataSource : dataSources) {
            if (dataSource != null) {
                mergedData.putAll(dataSource);
            }
        }
        return mergedData;
    }
}
