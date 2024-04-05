package com.luizalabs.simple.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppPropertiesTest {
    @Test void shouldLoadProperties() {
        AppProperties appProperties = AppProperties.getInstance();
        assertEquals("http://localhost:8081", appProperties.getProperty("app.url"));
        assertEquals("test", appProperties.getProperty("app.env"));
        assertEquals("simple", appProperties.getProperty("app.name"));
        assertEquals(System.getenv("PWD"), appProperties.getProperty("app.path"));
    }
}
