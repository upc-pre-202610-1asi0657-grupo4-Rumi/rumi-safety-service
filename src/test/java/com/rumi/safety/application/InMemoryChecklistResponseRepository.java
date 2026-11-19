package com.rumi.safety.application;

import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.repository.ChecklistResponseRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryChecklistResponseRepository implements ChecklistResponseRepository {

    private final Map<UUID, ChecklistResponse> responses = new HashMap<>();

    @Override
    public Optional<ChecklistResponse> findById(UUID id) {
        return Optional.ofNullable(responses.get(id));
    }

    @Override
    public ChecklistResponse save(ChecklistResponse response) {
        responses.put(response.id(), response);
        return response;
    }

    int count() {
        return responses.size();
    }
}
