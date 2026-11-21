package com.rumi.safety.application;

import com.rumi.safety.domain.model.ChecklistItemAnswer;
import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.repository.ChecklistResponseRepository;
import com.rumi.safety.domain.repository.ChecklistTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Use cases of post-earthquake checklists (US20).
 */
@Service
public class ChecklistApplicationService {

    private final ChecklistTemplateRepository templateRepository;
    private final ChecklistResponseRepository responseRepository;
    private final Clock clock;

    public ChecklistApplicationService(
            ChecklistTemplateRepository templateRepository,
            ChecklistResponseRepository responseRepository,
            Clock clock
    ) {
        this.templateRepository = templateRepository;
        this.responseRepository = responseRepository;
        this.clock = clock;
    }

    /**
     * Returns the current template: the one with the highest version, with its items ordered by position.
     *
     * @throws ChecklistTemplateNotFoundException if no template exists
     */
    public ChecklistTemplate getCurrentTemplate() {
        return templateRepository.findCurrent().orElseThrow(ChecklistTemplateNotFoundException::noTemplate);
    }

    /**
     * Saves the checklist a resident submitted. The answers must cover every item of the template exactly once.
     *
     * @throws ChecklistTemplateNotFoundException if the template does not exist
     * @throws IllegalArgumentException           if the answers do not match the items of the template
     */
    public ChecklistResponse submitResponse(SubmitChecklistResponseCommand command) {
        ChecklistTemplate template = templateRepository.findById(command.templateId())
                .orElseThrow(() -> ChecklistTemplateNotFoundException.withId(command.templateId()));
        List<ChecklistItemAnswer> answers = command.answers().stream()
                .map(answer -> new ChecklistItemAnswer(UUID.randomUUID(), answer.itemId(), answer.checked()))
                .toList();
        Instant submittedAt = Instant.now(clock).truncatedTo(ChronoUnit.SECONDS);
        ChecklistResponse response = ChecklistResponse.submit(
                template, command.residentUserId(), command.buildingId(), answers, submittedAt);
        return responseRepository.save(response);
    }
}
