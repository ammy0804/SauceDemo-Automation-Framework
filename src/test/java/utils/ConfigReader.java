package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        // OPTIMIZATION 1: Try-With-Resources automatically closes the file stream
        // preventing memory leaks without needing a manual input.close()
        String path = "src/test/resources/config.properties";
        try (FileInputStream input = new FileInputStream(path)) {
            
            properties = new Properties();
            properties.load(input);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file at path: " + path);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        
        // OPTIMIZATION 2: Null Safety check
        // Instantly tells you EXACTLY which key is missing from your properties file!
        if (value == null) {
            throw new RuntimeException("Config.properties error: The key '" + key + "' was not found!");
        }
        
        return value;
    }
}