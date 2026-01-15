package com.github.sudohackin.bootyaml.model;

/**
 * Represents a group of configuration properties.
 * Groups are typically top-level property prefixes like "server", "spring.datasource", etc.
 */
public class GroupMetadata {
    private String name;
    private String type;
    private String description;
    private String sourceType;

    public GroupMetadata() {
    }

    public GroupMetadata(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public String toString() {
        return "GroupMetadata{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
