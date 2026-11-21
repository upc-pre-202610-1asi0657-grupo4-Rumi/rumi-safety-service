package com.rumi.safety.infrastructure.web;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.infrastructure.web.dto.EvacuationPlanResponseDto;
import com.rumi.safety.infrastructure.web.dto.EvacuationRouteResponseDto;
import com.rumi.safety.infrastructure.web.dto.GeoPointDto;
import com.rumi.safety.infrastructure.web.dto.SafePointResponseDto;

final class EvacuationPlanDtoMapper {

    private EvacuationPlanDtoMapper() {
    }

    static EvacuationPlanResponseDto toDto(EvacuationPlan plan) {
        return new EvacuationPlanResponseDto(
                plan.id(),
                plan.buildingId(),
                plan.version(),
                plan.updatedAt(),
                plan.routes().stream()
                        .map(route -> new EvacuationRouteResponseDto(route.id(), route.description(), route.mapUrl()))
                        .toList(),
                plan.safePoints().stream()
                        .map(safePoint -> new SafePointResponseDto(
                                safePoint.id(),
                                safePoint.name(),
                                safePoint.location() == null ? null : new GeoPointDto(
                                        safePoint.location().latitude(), safePoint.location().longitude())))
                        .toList());
    }
}
