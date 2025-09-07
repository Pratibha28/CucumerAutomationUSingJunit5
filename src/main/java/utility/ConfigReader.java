package utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    /**
     * Loads environment-specific properties file. Looks for config-<env>.properties
     */
    public static Properties loadProperties(String env) {
        if (prop == null) {
            prop = new Properties();
        }

        String fileName = "config-" + env + ".properties";

        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("❌ Properties file not found: " + fileName);
            }
            prop.load(input);
            System.out.println("✅ Loaded config file: " + fileName);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to load properties file: " + fileName, e);
        }

        return prop;
    }

    public static String getProperty(String key) {
        if (prop == null) {
            throw new IllegalStateException("❌ Properties not loaded. Call loadProperties(env) first.");
        }
        String value = prop.getProperty(key);
        if (value == null) {
            System.err.println("⚠️ Key not found in properties: " + key);
        }
        return value;
    }
}
