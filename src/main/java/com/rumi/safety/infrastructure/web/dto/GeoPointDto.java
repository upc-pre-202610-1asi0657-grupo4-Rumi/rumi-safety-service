package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GeoPoint", description = "Geographic coordinates in decimal degrees")
public record GeoPointDto(
        @Schema(description = "Latitude, between -90 and 90", example = "-12.0464")
        double latitude,
        @Schema(description = "Longitude, between -180 and 180", example = "-77.0428")
        double longitude
) {
}
