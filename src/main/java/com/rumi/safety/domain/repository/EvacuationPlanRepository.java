package com.rumi.safety.domain.repository;

import com.rumi.safety.domain.model.EvacuationPlan;

import java.util.Optional;
import java.util.UUID;

public interface EvacuationPlanRepository {

    /**
     * Returns the latest version of the plan of the building, if it has one.
     */
    Optional<EvacuationPlan> findLatestByBuildingId(UUID buildingId);

    EvacuationPlan save(EvacuationPlan plan);
}
