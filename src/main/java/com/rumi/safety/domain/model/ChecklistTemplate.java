package com.rumi.safety.domain.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aggregate root: a versioned checklist residents answer after an earthquake.
 * Its items are always kept ordered by position.
 */
public record ChecklistTemplate(UUID id, String name, int version, List<ChecklistItem> items) {

    public ChecklistTemplate {
        Objects.requireNonNull(id, "id is required");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version must be at least 1");
        }
        items = items == null ? List.of() : items.stream()
                .sorted(Comparator.comparingInt(ChecklistItem::position))
                .toList();
        Set<Integer> positions = new HashSet<>();
        for (ChecklistItem item : items) {
            if (!positions.add(item.position())) {
                throw new IllegalArgumentException("position " + item.position() + " is used by more than one item");
            }
        }
    }

    public Set<UUID> itemIds() {
        return items.stream().map(ChecklistItem::id).collect(Collectors.toUnmodifiableSet());
    }
}
