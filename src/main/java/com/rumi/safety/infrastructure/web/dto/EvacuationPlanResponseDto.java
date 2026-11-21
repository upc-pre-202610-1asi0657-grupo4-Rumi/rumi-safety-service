package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(name = "EvacuationPlan", description = "Evacuation plan of a building with its routes and safe points")
public record EvacuationPlanResponseDto(
        @Schema(description = "Id of the plan", example = "a1b2c3d4-0001-4000-8000-000000000001")
        UUID id,
        @Schema(description = "Id of the building (Building Management context)",
                example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d")
        UUID buildingId,
        @Schema(description = "Version of the plan; the latest version is returned", example = "1")
        int version,
        @Schema(description = "Last update of the plan, in UTC", example = "2026-11-20T15:00:00Z")
        Instant updatedAt,
        @Schema(description = "Evacuation routes, ordered by description")
        List<EvacuationRouteResponseDto> routes,
        @Schema(description = "Safe points, ordered by name")
        List<SafePointResponseDto> safePoints
) {
}
