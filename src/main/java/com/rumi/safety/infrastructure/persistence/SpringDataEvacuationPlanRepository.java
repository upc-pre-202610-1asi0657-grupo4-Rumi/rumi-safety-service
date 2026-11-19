package com.rumi.safety.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEvacuationPlanRepository extends JpaRepository<EvacuationPlanJpaEntity, UUID> {

    Optional<EvacuationPlanJpaEntity> findFirstByBuildingIdOrderByVersionDesc(UUID buildingId);
}
