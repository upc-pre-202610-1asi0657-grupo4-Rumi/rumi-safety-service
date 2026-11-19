package com.rumi.safety.infrastructure.persistence;

import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaChecklistTemplateRepository implements ChecklistTemplateRepository {

    private final SpringDataChecklistTemplateRepository springDataRepository;

    public JpaChecklistTemplateRepository(SpringDataChecklistTemplateRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChecklistTemplate> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChecklistTemplate> findCurrent() {
        return springDataRepository.findFirstByOrderByVersionDesc().map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmpty() {
        return springDataRepository.count() == 0;
    }

    @Override
    @Transactional
    public ChecklistTemplate save(ChecklistTemplate template) {
        ChecklistTemplateJpaEntity entity =
                new ChecklistTemplateJpaEntity(template.id(), template.name(), template.version());
        template.items().forEach(item ->
                entity.addItem(new ChecklistItemJpaEntity(item.id(), item.label(), item.position())));
        return toDomain(springDataRepository.save(entity));
    }

    private ChecklistTemplate toDomain(ChecklistTemplateJpaEntity entity) {
        return new ChecklistTemplate(
                entity.getId(),
                entity.getName(),
                entity.getVersion(),
                entity.getItems().stream()
                        .map(item -> new ChecklistItem(item.getId(), item.getPosition(), item.getLabel()))
                        .toList());
    }
}
