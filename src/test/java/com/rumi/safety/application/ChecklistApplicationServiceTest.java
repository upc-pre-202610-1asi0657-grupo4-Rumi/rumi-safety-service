package com.rumi.safety.application;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.application.SubmitChecklistResponseCommand.ItemAnswer;
import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistItemAnswer;
import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.model.ChecklistTemplate;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static com.rumi.safety.SafetyFixtures.CRACKS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.ELECTRICITY_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.GAS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.NOW;
import static com.rumi.safety.SafetyFixtures.RESIDENT_ID;
import static com.rumi.safety.SafetyFixtures.TEMPLATE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChecklistApplicationServiceTest {

    private final InMemoryChecklistTemplateRepository templateRepository = new InMemoryChecklistTemplateRepository();
    private final InMemoryChecklistResponseRepository responseRepository = new InMemoryChecklistResponseRepository();
    private final ChecklistApplicationService service = new ChecklistApplicationService(
            templateRepository, responseRepository, Clock.fixed(NOW.plusMillis(250), ZoneOffset.UTC));

    @Test
    void theCurrentTemplateIsTheOneWithTheHighestVersion() {
        templateRepository.save(SafetyFixtures.template());
        ChecklistTemplate second = new ChecklistTemplate(UUID.randomUUID(), "POST_EARTHQUAKE_SELF_CHECK", 2,
                List.of(new ChecklistItem(UUID.randomUUID(), 1, "Gas")));
        templateRepository.save(second);

        assertThat(service.getCurrentTemplate()).isEqualTo(second);
    }

    @Test
    void failsWhenNoTemplateExists() {
        assertThatThrownBy(service::getCurrentTemplate)
                .isInstanceOf(ChecklistTemplateNotFoundException.class)
                .hasMessage("no checklist template exists");
    }

    @Test
    void savesAResponseThatAnswersEveryItem() {
        templateRepository.save(SafetyFixtures.template());

        ChecklistResponse response = service.submitResponse(command(TEMPLATE_ID, List.of(
                new ItemAnswer(GAS_ITEM_ID, false),
                new ItemAnswer(CRACKS_ITEM_ID, true),
                new ItemAnswer(ELECTRICITY_ITEM_ID, false))));

        assertThat(responseRepository.findById(response.id())).contains(response);
        assertThat(response.residentUserId()).isEqualTo(RESIDENT_ID);
        assertThat(response.buildingId()).isEqualTo(BUILDING_ID);
        assertThat(response.submittedAt()).isEqualTo(NOW);
        assertThat(response.answers()).extracting(ChecklistItemAnswer::checked).containsExactly(false, true, false);
    }

    @Test
    void rejectsAResponseToAnUnknownTemplate() {
        UUID unknownTemplate = UUID.randomUUID();

        assertThatThrownBy(() -> service.submitResponse(command(unknownTemplate, List.of())))
                .isInstanceOf(ChecklistTemplateNotFoundException.class)
                .hasMessage("checklist template " + unknownTemplate + " does not exist");
        assertThat(responseRepository.count()).isZero();
    }

    @Test
    void rejectsAResponseThatLeavesAnItemUnanswered() {
        templateRepository.save(SafetyFixtures.template());

        assertThatThrownBy(() -> service.submitResponse(command(TEMPLATE_ID, List.of(
                new ItemAnswer(GAS_ITEM_ID, false), new ItemAnswer(CRACKS_ITEM_ID, true)))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing items: " + ELECTRICITY_ITEM_ID);
        assertThat(responseRepository.count()).isZero();
    }

    private SubmitChecklistResponseCommand command(UUID templateId, List<ItemAnswer> answers) {
        return new SubmitChecklistResponseCommand(templateId, RESIDENT_ID, BUILDING_ID, answers);
    }
}
