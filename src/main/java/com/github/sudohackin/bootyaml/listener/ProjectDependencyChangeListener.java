package com.github.sudohackin.bootyaml.listener;

import com.github.sudohackin.bootyaml.service.ConfigurationMetadataService;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationEvent;
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for project dependency changes and triggers metadata reload.
 * This ensures the autocomplete suggestions stay up-to-date when dependencies change.
 */
public class ProjectDependencyChangeListener implements ExternalSystemTaskNotificationListener {

    @Override
    public void onSuccess(@NotNull ExternalSystemTaskId id) {
        // Reload metadata when dependencies are successfully updated
        Project project = id.findProject();
        if (project != null && !project.isDisposed()) {
            ConfigurationMetadataService service = project.getService(ConfigurationMetadataService.class);
            if (service != null) {
                service.reloadMetadata();
            }
        }
    }

    @Override
    public void onStatusChange(@NotNull ExternalSystemTaskNotificationEvent event) {
        // Not used
    }

    @Override
    public void onTaskOutput(@NotNull ExternalSystemTaskId id, @NotNull String text, boolean stdOut) {
        // Not used
    }

    @Override
    public void onEnd(@NotNull ExternalSystemTaskId id) {
        // Not used
    }

    @Override
    public void onStart(@NotNull ExternalSystemTaskId id) {
        // Not used
    }

    @Override
    public void onFailure(@NotNull ExternalSystemTaskId id, @NotNull Exception e) {
        // Not used
    }

    @Override
    public void onCancel(@NotNull ExternalSystemTaskId id) {
        // Not used
    }
}
