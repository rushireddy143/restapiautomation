package com.api.automation.utils;

import com.api.automation.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * Database manager for API test data validation and setup
 * Supports MySQL, PostgreSQL, and other JDBC-compatible databases
 */
public class DatabaseManager {
    
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private Connection connection;
    private final ConfigManager config;
    
    public DatabaseManager() {
        this.config = ConfigManager.getInstance();
    }
    
    /**
     * Establish database connection
     */
    public void connect() {
        try {
            String dbUrl = config.getProperty("db.url");
            String dbUsername = config.getProperty("db.username");
            String dbPassword = config.getProperty("db.password");
            
            if (dbUrl == null || dbUrl.isEmpty()) {
                logger.warn("Database URL not configured. Skipping database connection.");
                return;
            }
            
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            logger.info("Database connection established successfully");
            
        } catch (SQLException e) {
            logger.error("Failed to establish database connection", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    /**
     * Execute SELECT query and return results
     */
    public List<Map<String, Object>> executeQuery(String query, Object... parameters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
            
            logger.info("Query executed successfully. Returned {} rows", results.size());
            
        } catch (SQLException e) {
            logger.error("Failed to execute query: {}", query, e);
            throw new RuntimeException("Query execution failed", e);
        }
        
        return results;
    }
    
    /**
     * Execute UPDATE, INSERT, DELETE queries
     */
    public int executeUpdate(String query, Object... parameters) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Update query executed successfully. {} rows affected", rowsAffected);
            return rowsAffected;
            
        } catch (SQLException e) {
            logger.error("Failed to execute update query: {}", query, e);
            throw new RuntimeException("Update query execution failed", e);
        }
    }
    
    /**
     * Get single value from database
     */
    public Object getSingleValue(String query, Object... parameters) {
        List<Map<String, Object>> results = executeQuery(query, parameters);
        if (results.isEmpty()) {
            return null;
        }
        
        Map<String, Object> firstRow = results.get(0);
        return firstRow.values().iterator().next();
    }
    
    /**
     * Check if record exists
     */
    public boolean recordExists(String tableName, String whereClause, Object... parameters) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + whereClause;
        Object count = getSingleValue(query, parameters);
        return count != null && ((Number) count).intValue() > 0;
    }
    
    /**
     * Insert test data
     */
    public void insertTestData(String tableName, Map<String, Object> data) {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(entry.getKey());
            placeholders.append("?");
            values.add(entry.getValue());
        }
        
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", 
            tableName, columns.toString(), placeholders.toString());
        
        executeUpdate(query, values.toArray());
    }
    
    /**
     * Update test data
     */
    public void updateTestData(String tableName, Map<String, Object> data, String whereClause, Object... whereParams) {
        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            values.add(entry.getValue());
        }
        
        // Add where parameters
        values.addAll(Arrays.asList(whereParams));
        
        String query = String.format("UPDATE %s SET %s WHERE %s", 
            tableName, setClause.toString(), whereClause);
        
        executeUpdate(query, values.toArray());
    }
    
    /**
     * Delete test data
     */
    public void deleteTestData(String tableName, String whereClause, Object... parameters) {
        String query = "DELETE FROM " + tableName + " WHERE " + whereClause;
        executeUpdate(query, parameters);
    }
    
    /**
     * Clean up test data
     */
    public void cleanupTestData(String tableName) {
        String query = "DELETE FROM " + tableName + " WHERE created_by = 'automation_test'";
        executeUpdate(query);
        logger.info("Test data cleaned up from table: {}", tableName);
    }
    
    /**
     * Begin transaction
     */
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
            logger.info("Transaction started");
        } catch (SQLException e) {
            logger.error("Failed to start transaction", e);
            throw new RuntimeException("Transaction start failed", e);
        }
    }
    
    /**
     * Commit transaction
     */
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
            logger.info("Transaction committed");
        } catch (SQLException e) {
            logger.error("Failed to commit transaction", e);
            throw new RuntimeException("Transaction commit failed", e);
        }
    }
    
    /**
     * Rollback transaction
     */
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
            logger.info("Transaction rolled back");
        } catch (SQLException e) {
            logger.error("Failed to rollback transaction", e);
        }
    }
    
    /**
     * Close database connection
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }
        }
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
