package com.rumi.safety.application;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChecklistApplicationServiceTest {

    private final InMemoryChecklistTemplateRepository templateRepository = new InMemoryChecklistTemplateRepository();
    private final ChecklistApplicationService service = new ChecklistApplicationService(templateRepository);

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
}
