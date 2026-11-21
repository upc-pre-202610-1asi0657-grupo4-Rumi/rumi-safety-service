package com.rumi.safety.domain.model;

import com.rumi.safety.SafetyFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static com.rumi.safety.SafetyFixtures.CRACKS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.ELECTRICITY_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.GAS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.NOW;
import static com.rumi.safety.SafetyFixtures.RESIDENT_ID;
import static com.rumi.safety.SafetyFixtures.answer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChecklistResponseTest {

    private final ChecklistTemplate template = SafetyFixtures.template();

    @Test
    void acceptsOneAnswerPerItemAndStoresThemInTemplateOrder() {
        ChecklistResponse response = ChecklistResponse.submit(template, RESIDENT_ID, BUILDING_ID, List.of(
                answer(ELECTRICITY_ITEM_ID, false), answer(GAS_ITEM_ID, true), answer(CRACKS_ITEM_ID, false)), NOW);

        assertThat(response.id()).isNotNull();
        assertThat(response.templateId()).isEqualTo(template.id());
        assertThat(response.residentUserId()).isEqualTo(RESIDENT_ID);
        assertThat(response.submittedAt()).isEqualTo(NOW);
        assertThat(response.answers()).extracting(ChecklistItemAnswer::itemId)
                .containsExactly(GAS_ITEM_ID, CRACKS_ITEM_ID, ELECTRICITY_ITEM_ID);
        assertThat(response.answers()).extracting(ChecklistItemAnswer::checked).containsExactly(true, false, false);
    }

    @Test
    void rejectsAMissingItem() {
        assertThatThrownBy(() -> ChecklistResponse.submit(template, RESIDENT_ID, BUILDING_ID,
                List.of(answer(GAS_ITEM_ID, false), answer(CRACKS_ITEM_ID, true)), NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing items: " + ELECTRICITY_ITEM_ID);
    }

    @Test
    void rejectsARepeatedItem() {
        assertThatThrownBy(() -> ChecklistResponse.submit(template, RESIDENT_ID, BUILDING_ID, List.of(
                answer(GAS_ITEM_ID, false), answer(GAS_ITEM_ID, true),
                answer(CRACKS_ITEM_ID, true), answer(ELECTRICITY_ITEM_ID, false)), NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("answered more than once");
    }

    @Test
    void rejectsAnItemOfAnotherTemplate() {
        UUID foreignItem = UUID.randomUUID();
        assertThatThrownBy(() -> ChecklistResponse.submit(template, RESIDENT_ID, BUILDING_ID, List.of(
                answer(GAS_ITEM_ID, false), answer(CRACKS_ITEM_ID, true),
                answer(ELECTRICITY_ITEM_ID, false), answer(foreignItem, true)), NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to template");
    }
}
