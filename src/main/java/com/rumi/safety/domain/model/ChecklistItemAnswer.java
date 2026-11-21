package com.rumi.safety.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * The answer of a resident to one item of the template: whether the hazard was observed.
 */
public record ChecklistItemAnswer(UUID id, UUID itemId, boolean checked) {

    public ChecklistItemAnswer {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(itemId, "itemId is required");
    }
}
