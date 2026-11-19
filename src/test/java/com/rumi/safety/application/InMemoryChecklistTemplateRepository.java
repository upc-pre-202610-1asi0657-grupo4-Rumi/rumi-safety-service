package com.rumi.safety.application;

import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class InMemoryChecklistTemplateRepository implements ChecklistTemplateRepository {

    private final Map<UUID, ChecklistTemplate> templates = new HashMap<>();

    @Override
    public Optional<ChecklistTemplate> findById(UUID id) {
        return Optional.ofNullable(templates.get(id));
    }

    @Override
    public Optional<ChecklistTemplate> findCurrent() {
        return templates.values().stream().max(Comparator.comparingInt(ChecklistTemplate::version));
    }

    @Override
    public boolean isEmpty() {
        return templates.isEmpty();
    }

    @Override
    public ChecklistTemplate save(ChecklistTemplate template) {
        templates.put(template.id(), template);
        return template;
    }
}
