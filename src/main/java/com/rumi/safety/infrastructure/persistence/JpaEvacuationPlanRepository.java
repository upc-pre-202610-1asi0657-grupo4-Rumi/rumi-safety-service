package com.rumi.safety.infrastructure.persistence;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.model.EvacuationRoute;
import com.rumi.safety.domain.model.GeoPoint;
import com.rumi.safety.domain.model.SafePoint;
import com.rumi.safety.domain.repository.EvacuationPlanRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEvacuationPlanRepository implements EvacuationPlanRepository {

    private final SpringDataEvacuationPlanRepository springDataRepository;

    public JpaEvacuationPlanRepository(SpringDataEvacuationPlanRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EvacuationPlan> findLatestByBuildingId(UUID buildingId) {
        return springDataRepository.findFirstByBuildingIdOrderByVersionDesc(buildingId).map(this::toDomain);
    }

    @Override
    @Transactional
    public EvacuationPlan save(EvacuationPlan plan) {
        return toDomain(springDataRepository.save(toEntity(plan)));
    }

    private EvacuationPlanJpaEntity toEntity(EvacuationPlan plan) {
        EvacuationPlanJpaEntity entity = new EvacuationPlanJpaEntity(
                plan.id(), plan.buildingId(), plan.version(), plan.updatedAt());
        plan.routes().forEach(route ->
                entity.addRoute(new EvacuationRouteJpaEntity(route.id(), route.description(), route.mapUrl())));
        plan.safePoints().forEach(safePoint -> entity.addSafePoint(new SafePointJpaEntity(
                safePoint.id(),
                safePoint.name(),
                safePoint.location() == null ? null : safePoint.location().latitude(),
                safePoint.location() == null ? null : safePoint.location().longitude())));
        return entity;
    }

    private EvacuationPlan toDomain(EvacuationPlanJpaEntity entity) {
        return new EvacuationPlan(
                entity.getId(),
                entity.getBuildingId(),
                entity.getVersion(),
                entity.getUpdatedAt(),
                entity.getRoutes().stream()
                        .map(route -> new EvacuationRoute(route.getId(), route.getDescription(), route.getMapUrl()))
                        .toList(),
                entity.getSafePoints().stream()
                        .map(safePoint -> new SafePoint(safePoint.getId(), safePoint.getName(), location(safePoint)))
                        .toList());
    }

    private GeoPoint location(SafePointJpaEntity safePoint) {
        if (safePoint.getLocationLatitude() == null || safePoint.getLocationLongitude() == null) {
            return null;
        }
        return new GeoPoint(safePoint.getLocationLatitude(), safePoint.getLocationLongitude());
    }
}
