package com.github.sudohackin.bootyaml.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for ConfigurationMetadata model class.
 */
public class ConfigurationMetadataTest {

    private ConfigurationMetadata metadata;

    @Before
    public void setUp() {
        metadata = new ConfigurationMetadata();
    }

    @Test
    public void testEmptyMetadata() {
        assertNotNull(metadata.getProperties());
        assertNotNull(metadata.getGroups());
        assertNotNull(metadata.getHints());
        assertTrue(metadata.getProperties().isEmpty());
        assertTrue(metadata.getGroups().isEmpty());
        assertTrue(metadata.getHints().isEmpty());
    }

    @Test
    public void testAddProperties() {
        PropertyMetadata prop1 = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        PropertyMetadata prop2 = new PropertyMetadata("server.address", "java.lang.String", "Server address");

        metadata.setProperties(Arrays.asList(prop1, prop2));

        assertEquals(2, metadata.getProperties().size());
        assertEquals("server.port", metadata.getProperties().get(0).getName());
        assertEquals("server.address", metadata.getProperties().get(1).getName());
    }

    @Test
    public void testMergeMetadata() {
        PropertyMetadata prop1 = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        metadata.getProperties().add(prop1);

        ConfigurationMetadata other = new ConfigurationMetadata();
        PropertyMetadata prop2 = new PropertyMetadata("server.address", "java.lang.String", "Server address");
        other.getProperties().add(prop2);

        metadata.merge(other);

        assertEquals(2, metadata.getProperties().size());
    }

    @Test
    public void testCreatePropertyIndex() {
        PropertyMetadata prop1 = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        PropertyMetadata prop2 = new PropertyMetadata("server.address", "java.lang.String", "Server address");

        metadata.setProperties(Arrays.asList(prop1, prop2));

        Map<String, PropertyMetadata> index = metadata.createPropertyIndex();

        assertEquals(2, index.size());
        assertNotNull(index.get("server.port"));
        assertNotNull(index.get("server.address"));
        assertEquals("java.lang.Integer", index.get("server.port").getType());
        assertEquals("java.lang.String", index.get("server.address").getType());
    }

    @Test
    public void testCreateGroupIndex() {
        GroupMetadata group1 = new GroupMetadata("server", "org.springframework.ServerProperties", "Server config");
        GroupMetadata group2 = new GroupMetadata("spring.datasource", "org.springframework.DataSourceProperties", "DB config");

        metadata.setGroups(Arrays.asList(group1, group2));

        Map<String, GroupMetadata> index = metadata.createGroupIndex();

        assertEquals(2, index.size());
        assertNotNull(index.get("server"));
        assertNotNull(index.get("spring.datasource"));
    }
}
