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
@Table(name = "safe_points")
public class SafePointJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private EvacuationPlanJpaEntity plan;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "location_latitude")
    private Double locationLatitude;

    @Column(name = "location_longitude")
    private Double locationLongitude;

    protected SafePointJpaEntity() {
    }

    public SafePointJpaEntity(UUID id, String name, Double locationLatitude, Double locationLongitude) {
        this.id = id;
        this.name = name;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
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

    public String getName() {
        return name;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }
}
