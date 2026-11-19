package com.rumi.safety.infrastructure.seed;

import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;
import com.rumi.safety.infrastructure.persistence.JpaChecklistTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false, properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(JpaChecklistTemplateRepository.class)
class ChecklistTemplateSeederTest {

    @Autowired
    private ChecklistTemplateRepository templateRepository;

    @Test
    void seedsTheDefaultTemplateWhenTheTableIsEmpty() {
        ChecklistTemplateSeeder seeder = new ChecklistTemplateSeeder(templateRepository);

        seeder.run(null);
        seeder.run(null);

        ChecklistTemplate current = templateRepository.findCurrent().orElseThrow();
        assertThat(current.id()).isEqualTo(ChecklistTemplateSeeder.DEFAULT_TEMPLATE_ID);
        assertThat(current.name()).isEqualTo("POST_EARTHQUAKE_SELF_CHECK");
        assertThat(current.items()).extracting(ChecklistItem::label).containsExactly(
                "Gas: do you smell gas or hear a leak?",
                "Cracks: do you see new cracks in walls, floors or columns?",
                "Electricity: are there exposed wires, sparks or a power failure?");
    }

    @Test
    void leavesAnExistingTemplateUntouched() {
        UUID existingId = UUID.randomUUID();
        templateRepository.save(new ChecklistTemplate(existingId, "CUSTOM", 1,
                List.of(new ChecklistItem(UUID.randomUUID(), 1, "Custom question"))));

        new ChecklistTemplateSeeder(templateRepository).run(null);

        assertThat(templateRepository.findCurrent()).get().extracting(ChecklistTemplate::id).isEqualTo(existingId);
        assertThat(templateRepository.findById(ChecklistTemplateSeeder.DEFAULT_TEMPLATE_ID)).isEmpty();
    }
}
