package com.rumi.safety.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * A route residents follow to leave the building. The map is optional.
 */
public record EvacuationRoute(UUID id, String description, String mapUrl) {

    public EvacuationRoute {
        Objects.requireNonNull(id, "id is required");
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
    }
}
