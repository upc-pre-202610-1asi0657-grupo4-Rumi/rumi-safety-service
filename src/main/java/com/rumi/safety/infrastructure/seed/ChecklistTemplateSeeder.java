package com.rumi.safety.infrastructure.seed;

import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Seeds the default post-earthquake checklist in every profile, because there is no endpoint to
 * create templates. It does nothing when the template table already has rows.
 */
@Component
public class ChecklistTemplateSeeder implements ApplicationRunner {

    public static final UUID DEFAULT_TEMPLATE_ID = UUID.fromString("b1b2c3d4-0001-4000-8000-000000000001");
    public static final String DEFAULT_TEMPLATE_NAME = "POST_EARTHQUAKE_SELF_CHECK";

    private static final Logger log = LoggerFactory.getLogger(ChecklistTemplateSeeder.class);

    private final ChecklistTemplateRepository templateRepository;

    public ChecklistTemplateSeeder(ChecklistTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!templateRepository.isEmpty()) {
            return;
        }
        templateRepository.save(defaultTemplate());
        log.info("Seeded checklist template {} version 1", DEFAULT_TEMPLATE_NAME);
    }

    static ChecklistTemplate defaultTemplate() {
        return new ChecklistTemplate(DEFAULT_TEMPLATE_ID, DEFAULT_TEMPLATE_NAME, 1, List.of(
                new ChecklistItem(UUID.fromString("b1b2c3d4-0002-4000-8000-000000000001"), 1,
                        "Gas: do you smell gas or hear a leak?"),
                new ChecklistItem(UUID.fromString("b1b2c3d4-0002-4000-8000-000000000002"), 2,
                        "Cracks: do you see new cracks in walls, floors or columns?"),
                new ChecklistItem(UUID.fromString("b1b2c3d4-0002-4000-8000-000000000003"), 3,
                        "Electricity: are there exposed wires, sparks or a power failure?")));
    }
}
