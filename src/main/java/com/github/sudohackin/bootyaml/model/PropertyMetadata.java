package com.github.sudohackin.bootyaml.model;

/**
 * Represents a single configuration property metadata entry.
 * Example: server.port, spring.datasource.url, etc.
 */
public class PropertyMetadata {
    private String name;
    private String type;
    private String description;
    private Object defaultValue;
    private DeprecationMetadata deprecation;
    private String sourceType;

    public PropertyMetadata() {
    }

    public PropertyMetadata(String name, String type, String description) {
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

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public DeprecationMetadata getDeprecation() {
        return deprecation;
    }

    public void setDeprecation(DeprecationMetadata deprecation) {
        this.deprecation = deprecation;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isDeprecated() {
        return deprecation != null;
    }

    /**
     * Gets the parent property name (e.g., "server" for "server.port")
     */
    public String getParentName() {
        if (name == null) {
            return null;
        }
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(0, lastDot) : null;
    }

    /**
     * Gets the simple name without parent (e.g., "port" for "server.port")
     */
    public String getSimpleName() {
        if (name == null) {
            return null;
        }
        int lastDot = name.lastIndexOf('.');
        return lastDot >= 0 ? name.substring(lastDot + 1) : name;
    }

    @Override
    public String toString() {
        return "PropertyMetadata{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
