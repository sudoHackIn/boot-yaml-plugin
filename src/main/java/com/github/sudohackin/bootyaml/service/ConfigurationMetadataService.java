package com.github.sudohackin.bootyaml.service;

import com.github.sudohackin.bootyaml.model.ConfigurationMetadata;
import com.github.sudohackin.bootyaml.model.PropertyMetadata;
import com.github.sudohackin.bootyaml.parser.MetadataParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Service for managing Spring Boot configuration metadata.
 * Scans project dependencies for configuration metadata files and provides access to them.
 */
@Service(Service.Level.PROJECT)
public final class ConfigurationMetadataService {
    private static final Logger LOG = Logger.getInstance(ConfigurationMetadataService.class);
    private static final String METADATA_PATH = "META-INF/spring-configuration-metadata.json";

    private final Project project;
    private final MetadataParser parser;
    private ConfigurationMetadata cachedMetadata;
    private Map<String, PropertyMetadata> propertyIndex;
    private long lastUpdateTime = 0;

    public ConfigurationMetadataService(Project project) {
        this.project = project;
        this.parser = new MetadataParser();
    }

    /**
     * Gets all configuration metadata from project dependencies.
     * Results are cached and reused until dependencies change.
     *
     * @return merged configuration metadata from all sources
     */
    public synchronized ConfigurationMetadata getMetadata() {
        if (cachedMetadata == null) {
            reloadMetadata();
        }
        return cachedMetadata;
    }

    /**
     * Gets a specific property by its full name.
     *
     * @param propertyName the property name (e.g., "server.port")
     * @return the property metadata, or null if not found
     */
    public PropertyMetadata getProperty(String propertyName) {
        if (propertyIndex == null) {
            synchronized (this) {
                if (propertyIndex == null) {
                    propertyIndex = getMetadata().createPropertyIndex();
                }
            }
        }
        return propertyIndex.get(propertyName);
    }

    /**
     * Finds all properties that start with the given prefix.
     *
     * @param prefix the property name prefix
     * @return list of matching properties
     */
    public List<PropertyMetadata> findPropertiesWithPrefix(String prefix) {
        List<PropertyMetadata> result = new ArrayList<>();
        for (PropertyMetadata property : getMetadata().getProperties()) {
            if (property.getName().startsWith(prefix)) {
                result.add(property);
            }
        }
        return result;
    }

    /**
     * Reloads all metadata from project dependencies.
     * This is typically called when dependencies change.
     */
    public synchronized void reloadMetadata() {
        LOG.info("Reloading Spring Boot configuration metadata");
        cachedMetadata = new ConfigurationMetadata();
        propertyIndex = null;
        lastUpdateTime = System.currentTimeMillis();

        scanDependencies();
    }

    /**
     * Scans all project dependencies for configuration metadata files.
     */
    private void scanDependencies() {
        OrderEnumerator.orderEntries(project)
                .withoutSdk()
                .forEachLibrary(library -> {
                    if (library != null) {
                        for (VirtualFile file : library.getFiles(OrderRootType.CLASSES)) {
                            if (file.getName().endsWith(".jar")) {
                                scanJarFile(file);
                            }
                        }
                    }
                    return true;
                });

        LOG.info("Loaded " + cachedMetadata.getProperties().size() + " properties from dependencies");
    }

    /**
     * Scans a single JAR file for configuration metadata.
     */
    private void scanJarFile(VirtualFile jarFile) {
        try {
            String path = jarFile.getPath();
            // Remove jar:// prefix and !/
            path = path.replace("jar://", "").replace("!/", "");

            try (JarFile jar = new JarFile(path)) {
                JarEntry entry = jar.getJarEntry(METADATA_PATH);
                if (entry != null) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        ConfigurationMetadata metadata = parser.parse(is);
                        cachedMetadata.merge(metadata);
                        LOG.debug("Loaded metadata from: " + jarFile.getName());
                    }
                }
            }
        } catch (IOException e) {
            LOG.warn("Failed to read metadata from: " + jarFile.getName(), e);
        }
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
