package com.github.sudohackin.bootyaml.completion;

import com.github.sudohackin.bootyaml.model.PropertyMetadata;
import com.github.sudohackin.bootyaml.service.ConfigurationMetadataService;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.List;

/**
 * Provides autocomplete suggestions for Spring Boot YAML configuration files.
 */
public class SpringBootYamlCompletionContributor extends CompletionContributor {

    public SpringBootYamlCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(YAMLLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                   @NotNull ProcessingContext context,
                                                   @NotNull CompletionResultSet result) {
                        if (!isApplicationYaml(parameters)) {
                            return;
                        }

                        ConfigurationMetadataService service = parameters.getOriginalFile()
                                .getProject()
                                .getService(ConfigurationMetadataService.class);

                        String prefix = getCurrentPrefix(parameters);
                        List<PropertyMetadata> properties = service.findPropertiesWithPrefix(prefix);

                        for (PropertyMetadata property : properties) {
                            String propertyName = property.getName();

                            // Extract the part after the prefix
                            String suggestion = getSuggestionFromProperty(propertyName, prefix);

                            if (suggestion != null && !suggestion.isEmpty()) {
                                LookupElementBuilder element = LookupElementBuilder.create(suggestion)
                                        .withTypeText(property.getType())
                                        .withTailText(getDefaultValueText(property), true)
                                        .withIcon(com.intellij.icons.AllIcons.Nodes.Property);

                                if (property.getDescription() != null) {
                                    element = element.withPresentableText(suggestion + " - " + property.getDescription());
                                }

                                if (property.isDeprecated()) {
                                    element = element.withStrikeoutness(true);
                                }

                                result.addElement(element);
                            }
                        }
                    }
                });
    }

    /**
     * Checks if the current file is application.yaml or application.yml
     */
    private boolean isApplicationYaml(CompletionParameters parameters) {
        String fileName = parameters.getOriginalFile().getName();
        return fileName.equals("application.yaml") ||
               fileName.equals("application.yml") ||
               fileName.startsWith("application-");
    }

    /**
     * Gets the current property path prefix based on cursor position
     */
    private String getCurrentPrefix(CompletionParameters parameters) {
        StringBuilder prefix = new StringBuilder();

        // Walk up the YAML tree to build the full property path
        YAMLKeyValue current = findParentKeyValue(parameters.getPosition());
        while (current != null) {
            if (prefix.length() > 0) {
                prefix.insert(0, ".");
            }
            String key = current.getKeyText();
            prefix.insert(0, key);
            current = findParentKeyValue(current);
        }

        return prefix.toString();
    }

    /**
     * Finds the parent YAMLKeyValue for a given element
     */
    private YAMLKeyValue findParentKeyValue(com.intellij.psi.PsiElement element) {
        while (element != null) {
            if (element instanceof YAMLKeyValue) {
                return (YAMLKeyValue) element;
            }
            element = element.getParent();
        }
        return null;
    }

    /**
     * Extracts the suggestion text from a property name given a prefix
     */
    private String getSuggestionFromProperty(String propertyName, String prefix) {
        if (prefix.isEmpty()) {
            // For top-level, return the first segment
            int dotIndex = propertyName.indexOf('.');
            return dotIndex > 0 ? propertyName.substring(0, dotIndex) : propertyName;
        }

        if (propertyName.startsWith(prefix + ".")) {
            String remaining = propertyName.substring(prefix.length() + 1);
            int dotIndex = remaining.indexOf('.');
            return dotIndex > 0 ? remaining.substring(0, dotIndex) : remaining;
        }

        return null;
    }

    /**
     * Formats the default value for display in the lookup element
     */
    private String getDefaultValueText(PropertyMetadata property) {
        if (property.getDefaultValue() != null) {
            return " (default: " + property.getDefaultValue() + ")";
        }
        return "";
    }
}
