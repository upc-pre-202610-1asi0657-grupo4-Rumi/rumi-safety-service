package com.rumi.safety.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root: the evacuation plan of one building, with its routes and safe points.
 * The building belongs to the Building Management context and is referenced by id only.
 */
public record EvacuationPlan(
        UUID id,
        UUID buildingId,
        int version,
        Instant updatedAt,
        List<EvacuationRoute> routes,
        List<SafePoint> safePoints
) {

    public EvacuationPlan {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(buildingId, "buildingId is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
        if (version < 1) {
            throw new IllegalArgumentException("version must be at least 1");
        }
        routes = routes == null ? List.of() : List.copyOf(routes);
        safePoints = safePoints == null ? List.of() : List.copyOf(safePoints);
    }
}
