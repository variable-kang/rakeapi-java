package com.rake.rakeapi.util;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class RakePropertiesTest {
    @Test(expected = IllegalArgumentException.class)
    public void devDefaultProperties() {
        RakeProperties rakeProperties = new RakeProperties(new Properties());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidProperty() {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        RakeProperties rakeProperties = new RakeProperties(properties);
        rakeProperties.get("abc");
    }

    @Test
    public void validProperty() {
        Properties properties = new Properties();
        properties.setProperty("api.type", "dev");
        properties.setProperty("service.id", "rakeapi-tester");
        RakeProperties rakeProperties = new RakeProperties(properties);
        rakeProperties.get("api.type");
    }
}
