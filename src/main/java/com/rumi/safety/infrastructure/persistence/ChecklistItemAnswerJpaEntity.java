package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "checklist_item_answers",
        uniqueConstraints = @UniqueConstraint(name = "uq_checklist_item_answers_response_item",
                columnNames = {"response_id", "item_id"})
)
public class ChecklistItemAnswerJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private ChecklistResponseJpaEntity response;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ChecklistItemJpaEntity item;

    @Column(name = "checked", nullable = false)
    private boolean checked;

    protected ChecklistItemAnswerJpaEntity() {
    }

    public ChecklistItemAnswerJpaEntity(UUID id, ChecklistItemJpaEntity item, boolean checked) {
        this.id = id;
        this.item = item;
        this.checked = checked;
    }

    void setResponse(ChecklistResponseJpaEntity response) {
        this.response = response;
    }

    public UUID getId() {
        return id;
    }

    public ChecklistResponseJpaEntity getResponse() {
        return response;
    }

    public ChecklistItemJpaEntity getItem() {
        return item;
    }

    public boolean isChecked() {
        return checked;
    }
}
