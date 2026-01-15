package com.github.sudohackin.bootyaml.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PropertyMetadata model class.
 */
public class PropertyMetadataTest {

    @Test
    public void testPropertyCreation() {
        PropertyMetadata property = new PropertyMetadata();
        property.setName("server.port");
        property.setType("java.lang.Integer");
        property.setDescription("Server HTTP port");
        property.setDefaultValue(8080);

        assertEquals("server.port", property.getName());
        assertEquals("java.lang.Integer", property.getType());
        assertEquals("Server HTTP port", property.getDescription());
        assertEquals(8080, property.getDefaultValue());
    }

    @Test
    public void testGetParentName() {
        PropertyMetadata property = new PropertyMetadata();

        property.setName("server.port");
        assertEquals("server", property.getParentName());

        property.setName("spring.datasource.url");
        assertEquals("spring.datasource", property.getParentName());

        property.setName("server");
        assertNull(property.getParentName());

        property.setName(null);
        assertNull(property.getParentName());
    }

    @Test
    public void testGetSimpleName() {
        PropertyMetadata property = new PropertyMetadata();

        property.setName("server.port");
        assertEquals("port", property.getSimpleName());

        property.setName("spring.datasource.url");
        assertEquals("url", property.getSimpleName());

        property.setName("server");
        assertEquals("server", property.getSimpleName());

        property.setName(null);
        assertNull(property.getSimpleName());
    }

    @Test
    public void testDeprecation() {
        PropertyMetadata property = new PropertyMetadata();
        assertFalse(property.isDeprecated());

        DeprecationMetadata deprecation = new DeprecationMetadata();
        deprecation.setReason("Use server.port instead");
        deprecation.setReplacement("server.port");

        property.setDeprecation(deprecation);
        assertTrue(property.isDeprecated());
        assertEquals("Use server.port instead", property.getDeprecation().getReason());
        assertEquals("server.port", property.getDeprecation().getReplacement());
    }

    @Test
    public void testConstructor() {
        PropertyMetadata property = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");

        assertEquals("server.port", property.getName());
        assertEquals("java.lang.Integer", property.getType());
        assertEquals("Server port", property.getDescription());
    }

    @Test
    public void testToString() {
        PropertyMetadata property = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        String str = property.toString();

        assertTrue(str.contains("server.port"));
        assertTrue(str.contains("java.lang.Integer"));
    }
}
