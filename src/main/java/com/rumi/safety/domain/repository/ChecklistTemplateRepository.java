package com.rumi.safety.domain.repository;

import com.rumi.safety.domain.model.ChecklistTemplate;

import java.util.Optional;
import java.util.UUID;

public interface ChecklistTemplateRepository {

    Optional<ChecklistTemplate> findById(UUID id);

    /**
     * Returns the template with the highest version, if any template exists.
     */
    Optional<ChecklistTemplate> findCurrent();

    boolean isEmpty();

    ChecklistTemplate save(ChecklistTemplate template);
}
