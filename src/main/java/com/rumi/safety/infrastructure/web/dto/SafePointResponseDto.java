package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "SafePoint", description = "A place where residents gather after leaving the building")
public record SafePointResponseDto(
        @Schema(description = "Id of the safe point", example = "a1b2c3d4-0003-4000-8000-000000000001")
        UUID id,
        @Schema(description = "Name of the safe point", example = "Meeting point: park in front of the building")
        String name,
        @Schema(description = "Location of the safe point; null when it is not known", nullable = true)
        GeoPointDto location
) {
}
