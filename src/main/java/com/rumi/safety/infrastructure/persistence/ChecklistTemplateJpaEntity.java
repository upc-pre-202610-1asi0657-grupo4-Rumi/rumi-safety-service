package com.rumi.safety.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "checklist_templates")
public class ChecklistTemplateJpaEntity {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "version", nullable = false)
    private int version;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    private List<ChecklistItemJpaEntity> items = new ArrayList<>();

    protected ChecklistTemplateJpaEntity() {
    }

    public ChecklistTemplateJpaEntity(UUID id, String name, int version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public void addItem(ChecklistItemJpaEntity item) {
        item.setTemplate(this);
        items.add(item);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public List<ChecklistItemJpaEntity> getItems() {
        return items;
    }
}
