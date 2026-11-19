package com.rumi.safety.application;

import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;
import org.springframework.stereotype.Service;

/**
 * Use cases of post-earthquake checklists (US20).
 */
@Service
public class ChecklistApplicationService {

    private final ChecklistTemplateRepository templateRepository;

    public ChecklistApplicationService(ChecklistTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Returns the current template: the one with the highest version, with its items ordered by position.
     *
     * @throws ChecklistTemplateNotFoundException if no template exists
     */
    public ChecklistTemplate getCurrentTemplate() {
        return templateRepository.findCurrent().orElseThrow(ChecklistTemplateNotFoundException::noTemplate);
    }
}
