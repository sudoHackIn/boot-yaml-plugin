package com.github.sudohackin.bootyaml.parser;

import com.github.sudohackin.bootyaml.model.ConfigurationMetadata;
import com.github.sudohackin.bootyaml.model.PropertyMetadata;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Tests for MetadataParser.
 */
public class MetadataParserTest {

    private MetadataParser parser;

    @Before
    public void setUp() {
        parser = new MetadataParser();
    }

    @Test
    public void testParseSimpleJson() {
        String json = "{\n" +
                "  \"properties\": [\n" +
                "    {\n" +
                "      \"name\": \"server.port\",\n" +
                "      \"type\": \"java.lang.Integer\",\n" +
                "      \"description\": \"Server HTTP port\",\n" +
                "      \"defaultValue\": 8080\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        ConfigurationMetadata metadata = parser.parse(json);

        assertNotNull(metadata);
        assertEquals(1, metadata.getProperties().size());

        PropertyMetadata property = metadata.getProperties().get(0);
        assertEquals("server.port", property.getName());
        assertEquals("java.lang.Integer", property.getType());
        assertEquals("Server HTTP port", property.getDescription());
        assertEquals(8080.0, property.getDefaultValue());
    }

    @Test
    public void testParseFromInputStream() throws IOException {
        InputStream is = getClass().getResourceAsStream("/test-metadata/spring-configuration-metadata.json");
        assertNotNull("Test metadata file not found", is);

        ConfigurationMetadata metadata = parser.parse(is);

        assertNotNull(metadata);
        assertFalse(metadata.getProperties().isEmpty());
        assertFalse(metadata.getGroups().isEmpty());

        // Verify specific properties
        PropertyMetadata serverPort = metadata.getProperties().stream()
                .filter(p -> "server.port".equals(p.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull("server.port property not found", serverPort);
        assertEquals("java.lang.Integer", serverPort.getType());
        assertEquals("Server HTTP port", serverPort.getDescription());
    }

    @Test
    public void testParseWithGroups() {
        String json = "{\n" +
                "  \"groups\": [\n" +
                "    {\n" +
                "      \"name\": \"server\",\n" +
                "      \"type\": \"org.springframework.boot.ServerProperties\",\n" +
                "      \"description\": \"Server configuration\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"properties\": []\n" +
                "}";

        ConfigurationMetadata metadata = parser.parse(json);

        assertEquals(1, metadata.getGroups().size());
        assertEquals("server", metadata.getGroups().get(0).getName());
    }

    @Test
    public void testParseWithHints() throws IOException {
        InputStream is = getClass().getResourceAsStream("/test-metadata/spring-configuration-metadata.json");
        ConfigurationMetadata metadata = parser.parse(is);

        assertFalse(metadata.getHints().isEmpty());
    }

    @Test
    public void testToJson() {
        ConfigurationMetadata metadata = new ConfigurationMetadata();
        PropertyMetadata property = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        metadata.getProperties().add(property);

        String json = parser.toJson(metadata);

        assertNotNull(json);
        assertTrue(json.contains("server.port"));
        assertTrue(json.contains("java.lang.Integer"));
    }

    @Test
    public void testRoundTrip() {
        ConfigurationMetadata original = new ConfigurationMetadata();
        PropertyMetadata property = new PropertyMetadata("server.port", "java.lang.Integer", "Server port");
        property.setDefaultValue(8080);
        original.getProperties().add(property);

        String json = parser.toJson(original);
        ConfigurationMetadata parsed = parser.parse(json);

        assertEquals(1, parsed.getProperties().size());
        assertEquals("server.port", parsed.getProperties().get(0).getName());
        assertEquals("java.lang.Integer", parsed.getProperties().get(0).getType());
    }
}
