package com.github.sudohackin.bootyaml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root object representing Spring Boot configuration metadata.
 * Corresponds to the structure of spring-configuration-metadata.json
 */
public class ConfigurationMetadata {
    private List<PropertyMetadata> properties = new ArrayList<>();
    private List<GroupMetadata> groups = new ArrayList<>();
    private List<HintMetadata> hints = new ArrayList<>();

    public ConfigurationMetadata() {
    }

    public List<PropertyMetadata> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyMetadata> properties) {
        this.properties = properties;
    }

    public List<GroupMetadata> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupMetadata> groups) {
        this.groups = groups;
    }

    public List<HintMetadata> getHints() {
        return hints;
    }

    public void setHints(List<HintMetadata> hints) {
        this.hints = hints;
    }

    /**
     * Merges another metadata object into this one
     */
    public void merge(ConfigurationMetadata other) {
        if (other.properties != null) {
            this.properties.addAll(other.properties);
        }
        if (other.groups != null) {
            this.groups.addAll(other.groups);
        }
        if (other.hints != null) {
            this.hints.addAll(other.hints);
        }
    }

    /**
     * Creates an index for fast property lookup by name
     */
    public Map<String, PropertyMetadata> createPropertyIndex() {
        Map<String, PropertyMetadata> index = new HashMap<>();
        for (PropertyMetadata property : properties) {
            index.put(property.getName(), property);
        }
        return index;
    }

    /**
     * Creates an index for fast group lookup by name
     */
    public Map<String, GroupMetadata> createGroupIndex() {
        Map<String, GroupMetadata> index = new HashMap<>();
        for (GroupMetadata group : groups) {
            index.put(group.getName(), group);
        }
        return index;
    }
}
