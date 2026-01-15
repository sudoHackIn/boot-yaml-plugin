package com.github.sudohackin.bootyaml.model;

/**
 * Information about deprecated configuration properties.
 */
public class DeprecationMetadata {
    private String reason;
    private String replacement;
    private String level;

    public DeprecationMetadata() {
    }

    public DeprecationMetadata(String reason, String replacement) {
        this.reason = reason;
        this.replacement = replacement;
        this.level = "warning";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isError() {
        return "error".equalsIgnoreCase(level);
    }
}
