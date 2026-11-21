package com.rumi.safety.domain.repository;

import com.rumi.safety.domain.model.ChecklistResponse;

import java.util.Optional;
import java.util.UUID;

public interface ChecklistResponseRepository {

    Optional<ChecklistResponse> findById(UUID id);

    ChecklistResponse save(ChecklistResponse response);
}
