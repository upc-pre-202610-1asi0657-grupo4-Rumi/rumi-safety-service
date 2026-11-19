package com.rumi.safety.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChecklistTemplateTest {

    @Test
    void keepsItemsOrderedByPosition() {
        ChecklistTemplate template = new ChecklistTemplate(UUID.randomUUID(), "CHECK", 1, List.of(
                new ChecklistItem(UUID.randomUUID(), 2, "Second"),
                new ChecklistItem(UUID.randomUUID(), 1, "First")));

        assertThat(template.items()).extracting(ChecklistItem::label).containsExactly("First", "Second");
    }

    @Test
    void rejectsTwoItemsInTheSamePosition() {
        assertThatThrownBy(() -> new ChecklistTemplate(UUID.randomUUID(), "CHECK", 1, List.of(
                new ChecklistItem(UUID.randomUUID(), 1, "First"),
                new ChecklistItem(UUID.randomUUID(), 1, "Also first"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("position 1");
    }

    @Test
    void rejectsAVersionBelowOne() {
        assertThatThrownBy(() -> new ChecklistTemplate(UUID.randomUUID(), "CHECK", 0, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("version must be at least 1");
    }
}
