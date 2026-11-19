package com.rumi.safety.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * One question of a checklist template. Items are shown in ascending position.
 */
public record ChecklistItem(UUID id, int position, String label) {

    public ChecklistItem {
        Objects.requireNonNull(id, "id is required");
        if (position < 1) {
            throw new IllegalArgumentException("position must be at least 1");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label is required");
        }
    }
}
