package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "checklist_items")
public class ChecklistItemJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private ChecklistTemplateJpaEntity template;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Column(name = "position", nullable = false)
    private int position;

    protected ChecklistItemJpaEntity() {
    }

    public ChecklistItemJpaEntity(UUID id, String label, int position) {
        this.id = id;
        this.label = label;
        this.position = position;
    }

    void setTemplate(ChecklistTemplateJpaEntity template) {
        this.template = template;
    }

    public UUID getId() {
        return id;
    }

    public ChecklistTemplateJpaEntity getTemplate() {
        return template;
    }

    public String getLabel() {
        return label;
    }

    public int getPosition() {
        return position;
    }
}
