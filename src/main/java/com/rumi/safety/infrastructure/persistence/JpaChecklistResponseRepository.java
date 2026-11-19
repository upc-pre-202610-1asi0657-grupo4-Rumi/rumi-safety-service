package com.rumi.safety.infrastructure.persistence;

import com.rumi.safety.domain.model.ChecklistItemAnswer;
import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.repository.ChecklistResponseRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaChecklistResponseRepository implements ChecklistResponseRepository {

    private final SpringDataChecklistResponseRepository springDataRepository;
    private final EntityManager entityManager;

    public JpaChecklistResponseRepository(
            SpringDataChecklistResponseRepository springDataRepository,
            EntityManager entityManager
    ) {
        this.springDataRepository = springDataRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChecklistResponse> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public ChecklistResponse save(ChecklistResponse response) {
        ChecklistResponseJpaEntity entity = new ChecklistResponseJpaEntity(
                response.id(),
                entityManager.getReference(ChecklistTemplateJpaEntity.class, response.templateId()),
                response.residentUserId(),
                response.buildingId(),
                response.submittedAt());
        response.answers().forEach(answer -> entity.addAnswer(new ChecklistItemAnswerJpaEntity(
                answer.id(),
                entityManager.getReference(ChecklistItemJpaEntity.class, answer.itemId()),
                answer.checked())));
        return toDomain(springDataRepository.save(entity));
    }

    private ChecklistResponse toDomain(ChecklistResponseJpaEntity entity) {
        return new ChecklistResponse(
                entity.getId(),
                entity.getTemplate().getId(),
                entity.getResidentUserId(),
                entity.getBuildingId(),
                entity.getSubmittedAt(),
                entity.getAnswers().stream()
                        .sorted(Comparator.comparingInt(answer -> answer.getItem().getPosition()))
                        .map(answer -> new ChecklistItemAnswer(answer.getId(), answer.getItem().getId(), answer.isChecked()))
                        .toList());
    }
}
