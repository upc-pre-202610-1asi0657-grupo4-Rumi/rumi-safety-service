package com.rumi.safety.application;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.repository.EvacuationPlanRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Use cases of evacuation plans (US19).
 */
@Service
public class EvacuationApplicationService {

    private final EvacuationPlanRepository planRepository;

    public EvacuationApplicationService(EvacuationPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /**
     * Returns the latest evacuation plan of the building.
     *
     * @throws EvacuationPlanNotFoundException if the building has no plan
     */
    public EvacuationPlan getPlan(UUID buildingId) {
        Objects.requireNonNull(buildingId, "buildingId is required");
        return planRepository.findLatestByBuildingId(buildingId)
                .orElseThrow(() -> new EvacuationPlanNotFoundException(buildingId));
    }
}
