package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "evacuation_routes")
public class EvacuationRouteJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private EvacuationPlanJpaEntity plan;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "map_url", length = 300)
    private String mapUrl;

    protected EvacuationRouteJpaEntity() {
    }

    public EvacuationRouteJpaEntity(UUID id, String description, String mapUrl) {
        this.id = id;
        this.description = description;
        this.mapUrl = mapUrl;
    }

    void setPlan(EvacuationPlanJpaEntity plan) {
        this.plan = plan;
    }

    public UUID getId() {
        return id;
    }

    public EvacuationPlanJpaEntity getPlan() {
        return plan;
    }

    public String getDescription() {
        return description;
    }

    public String getMapUrl() {
        return mapUrl;
    }
}
