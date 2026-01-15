package com.github.sudohackin.bootyaml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides additional hints for property values, such as allowed values or value providers.
 */
public class HintMetadata {
    private String name;
    private List<ValueHint> values = new ArrayList<>();
    private List<ValueProvider> providers = new ArrayList<>();

    public HintMetadata() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValueHint> getValues() {
        return values;
    }

    public void setValues(List<ValueHint> values) {
        this.values = values;
    }

    public List<ValueProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<ValueProvider> providers) {
        this.providers = providers;
    }

    public static class ValueHint {
        private Object value;
        private String description;

        public ValueHint() {
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class ValueProvider {
        private String name;
        private Object parameters;

        public ValueProvider() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getParameters() {
            return parameters;
        }

        public void setParameters(Object parameters) {
            this.parameters = parameters;
        }
    }
}
