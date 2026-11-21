package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evacuation_plans")
public class EvacuationPlanJpaEntity {

    @Id
    private UUID id;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("description")
    private List<EvacuationRouteJpaEntity> routes = new ArrayList<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name")
    private List<SafePointJpaEntity> safePoints = new ArrayList<>();

    protected EvacuationPlanJpaEntity() {
    }

    public EvacuationPlanJpaEntity(UUID id, UUID buildingId, int version, Instant updatedAt) {
        this.id = id;
        this.buildingId = buildingId;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public void addRoute(EvacuationRouteJpaEntity route) {
        route.setPlan(this);
        routes.add(route);
    }

    public void addSafePoint(SafePointJpaEntity safePoint) {
        safePoint.setPlan(this);
        safePoints.add(safePoint);
    }

    public UUID getId() {
        return id;
    }

    public UUID getBuildingId() {
        return buildingId;
    }

    public int getVersion() {
        return version;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<EvacuationRouteJpaEntity> getRoutes() {
        return routes;
    }

    public List<SafePointJpaEntity> getSafePoints() {
        return safePoints;
    }
}
