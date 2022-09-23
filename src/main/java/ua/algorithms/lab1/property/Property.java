package ua.algorithms.lab1.property;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Property {
    private static Property property;
    private final Properties properties;
    private final static String DEFAULT_CONFIGURATION_FILE_PATH;

    static {
        DEFAULT_CONFIGURATION_FILE_PATH =  "src/main/resources/configuration.properties";
    }

    private Property() {
        properties = new Properties();

        try {
            properties.load(new FileReader(DEFAULT_CONFIGURATION_FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Property getInstance() {
        if (!Objects.nonNull(property)) {
            property = new Property();
        }

        return property;
    }

    public Properties getProperties() {
        return properties;
    }

}