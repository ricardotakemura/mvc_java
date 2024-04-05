package com.luizalabs.simple.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jetty.util.StringUtil;

public class AppProperties {

    public static final String ENV = System.getenv("ENV");

    private static final AppLogger LOGGER = new AppLogger(AppProperties.class);

    private static AppProperties instance;
    
    private Properties properties;

    private AppProperties() {
        init();
    }

    private void init() {
        this.properties = AppProperties.load("/application.properties");
        if (StringUtil.isNotBlank(ENV)) {
            String resourceName = "/application-" + ENV.toLowerCase() + ".properties";
            if (AppProperties.isResourceExists(resourceName)) {
                this.properties.putAll(AppProperties.load(resourceName));
            }
        }
    }

    private static void replaceEnvProperties(Properties properties) {        
        Map<String, String> envProperties = properties.entrySet()
            .stream()
            .map(AppProperties::replaceEnvProperty)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        properties.putAll(envProperties);
    }

    private static Entry<String, String> replaceEnvProperty(Entry<?, ?>  property) {
        Map<String, String> envs = System.getenv();
        String propertyValue = String.valueOf(property.getValue());
        String propertyKey = String.valueOf(property.getKey());
        Optional<String> envValue = envs.entrySet()
            .stream()
            .filter(env -> propertyValue.contains("${" + env.getKey()))
            .map(Entry::getValue)
            .findFirst();
        String value = envValue.orElse(
            propertyValue
                .replaceFirst("\\$\\{\\w+[:|}]", "")
                .replace("}", "")
        );
       return new SimpleEntry<String, String>(propertyKey, value);
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public boolean getPropertyAsBoolean(String key) {
        String value = getProperty(key);
        return StringUtil.isNotBlank(value)?
            Boolean.parseBoolean(value):
            false;
    }

    public String[] getPropertyAsArray(String key) {
        String value = getProperty(key);
        if (StringUtil.isNotBlank(value)) {
            return value.split(",");
        }
        return new String[0];
    }

    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    } 

    public static AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
        }
        return instance;
    }

    public static Properties load(String resourceName) {
        Properties localProperties = new Properties();
        try {
            localProperties.load(AppProperties.class.getResourceAsStream(resourceName));
            AppProperties.replaceEnvProperties(localProperties);
        } catch (IOException e) {
            LOGGER.error("Loading is failed: " + resourceName, e);
        }
        return localProperties;
    }

    public static boolean isResourceExists(String resourceName) {
        try {
            File file = new File(
                AppProperties.class.getResource(resourceName).toURI()
            );
            return file.exists();
        } catch (URISyntaxException e) {
            LOGGER.error("URI is invalid: " + resourceName, e);
            return false;
        }
    }
}
