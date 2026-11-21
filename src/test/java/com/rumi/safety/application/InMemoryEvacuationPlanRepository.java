package com.rumi.safety.application;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.repository.EvacuationPlanRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryEvacuationPlanRepository implements EvacuationPlanRepository {

    private final Map<UUID, EvacuationPlan> plans = new HashMap<>();

    @Override
    public Optional<EvacuationPlan> findLatestByBuildingId(UUID buildingId) {
        return plans.values().stream()
                .filter(plan -> plan.buildingId().equals(buildingId))
                .max(Comparator.comparingInt(EvacuationPlan::version));
    }

    @Override
    public EvacuationPlan save(EvacuationPlan plan) {
        plans.put(plan.id(), plan);
        return plan;
    }
}
