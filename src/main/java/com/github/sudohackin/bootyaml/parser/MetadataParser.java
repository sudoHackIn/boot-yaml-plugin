package com.github.sudohackin.bootyaml.parser;

import com.github.sudohackin.bootyaml.model.ConfigurationMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Parser for Spring Boot configuration metadata JSON files.
 * Parses spring-configuration-metadata.json files from JAR dependencies.
 */
public class MetadataParser {
    private final Gson gson;

    public MetadataParser() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Parses configuration metadata from an input stream.
     *
     * @param inputStream the input stream containing JSON metadata
     * @return parsed ConfigurationMetadata object
     * @throws IOException if reading fails
     */
    public ConfigurationMetadata parse(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return parse(reader);
        }
    }

    /**
     * Parses configuration metadata from a reader.
     *
     * @param reader the reader containing JSON metadata
     * @return parsed ConfigurationMetadata object
     */
    public ConfigurationMetadata parse(Reader reader) {
        return gson.fromJson(reader, ConfigurationMetadata.class);
    }

    /**
     * Parses configuration metadata from a JSON string.
     *
     * @param json the JSON string
     * @return parsed ConfigurationMetadata object
     */
    public ConfigurationMetadata parse(String json) {
        return gson.fromJson(json, ConfigurationMetadata.class);
    }

    /**
     * Serializes configuration metadata to JSON string.
     *
     * @param metadata the metadata to serialize
     * @return JSON string representation
     */
    public String toJson(ConfigurationMetadata metadata) {
        return gson.toJson(metadata);
    }
}
