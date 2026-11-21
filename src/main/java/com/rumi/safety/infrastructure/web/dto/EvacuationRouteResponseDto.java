package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "EvacuationRoute", description = "A route residents follow to leave the building")
public record EvacuationRouteResponseDto(
        @Schema(description = "Id of the route", example = "a1b2c3d4-0002-4000-8000-000000000001")
        UUID id,
        @Schema(description = "How to follow the route", example = "Main stairs, north wing")
        String description,
        @Schema(description = "Image of the route on the floor map; null when there is none",
                example = "https://maps.example.com/rumi/routes/main-stairs.png", nullable = true)
        String mapUrl
) {
}
