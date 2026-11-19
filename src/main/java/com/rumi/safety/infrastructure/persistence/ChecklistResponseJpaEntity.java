package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "checklist_responses")
public class ChecklistResponseJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private ChecklistTemplateJpaEntity template;

    @Column(name = "resident_user_id", nullable = false)
    private UUID residentUserId;

    @Column(name = "building_id", nullable = false)
    private UUID buildingId;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemAnswerJpaEntity> answers = new ArrayList<>();

    protected ChecklistResponseJpaEntity() {
    }

    public ChecklistResponseJpaEntity(
            UUID id,
            ChecklistTemplateJpaEntity template,
            UUID residentUserId,
            UUID buildingId,
            Instant submittedAt
    ) {
        this.id = id;
        this.template = template;
        this.residentUserId = residentUserId;
        this.buildingId = buildingId;
        this.submittedAt = submittedAt;
    }

    public void addAnswer(ChecklistItemAnswerJpaEntity answer) {
        answer.setResponse(this);
        answers.add(answer);
    }

    public UUID getId() {
        return id;
    }

    public ChecklistTemplateJpaEntity getTemplate() {
        return template;
    }

    public UUID getResidentUserId() {
        return residentUserId;
    }

    public UUID getBuildingId() {
        return buildingId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public List<ChecklistItemAnswerJpaEntity> getAnswers() {
        return answers;
    }
}
