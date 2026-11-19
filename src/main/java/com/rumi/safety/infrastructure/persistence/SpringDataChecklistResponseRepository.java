package com.rumi.safety.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataChecklistResponseRepository extends JpaRepository<ChecklistResponseJpaEntity, UUID> {
}
