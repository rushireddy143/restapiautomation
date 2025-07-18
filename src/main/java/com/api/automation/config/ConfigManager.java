package com.api.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager for handling environment-specific settings
 */
public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    private String environment;

    private ConfigManager() {
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        environment = System.getProperty("env", "dev");
        
        try {
            String configFile = "src/test/resources/config/" + environment + ".properties";
            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file for environment: " + environment, e);
        }
    }

    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public String getUsername() {
        return properties.getProperty("username");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }

    public String getApiKey() {
        return properties.getProperty("api.key");
    }

    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("timeout", "30"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getEnvironment() {
        return environment;
    }
}
