package com.rumi.safety.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * A place where residents gather after leaving the building. The location is optional.
 */
public record SafePoint(UUID id, String name, GeoPoint location) {

    public SafePoint {
        Objects.requireNonNull(id, "id is required");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}
