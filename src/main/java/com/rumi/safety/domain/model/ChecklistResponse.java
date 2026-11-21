package com.rumi.safety.domain.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root: the checklist a resident submitted after an earthquake.
 * The resident and the building belong to other contexts and are referenced by id only.
 */
public record ChecklistResponse(
        UUID id,
        UUID templateId,
        UUID residentUserId,
        UUID buildingId,
        Instant submittedAt,
        List<ChecklistItemAnswer> answers
) {

    public ChecklistResponse {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(templateId, "templateId is required");
        Objects.requireNonNull(residentUserId, "residentUserId is required");
        Objects.requireNonNull(buildingId, "buildingId is required");
        Objects.requireNonNull(submittedAt, "submittedAt is required");
        answers = answers == null ? List.of() : List.copyOf(answers);
    }

    /**
     * Creates a response to the given template. The answers must cover every item of the
     * template exactly once; they are stored in the order of the template items.
     *
     * @throws IllegalArgumentException if an item is unknown, repeated or missing
     */
    public static ChecklistResponse submit(
            ChecklistTemplate template,
            UUID residentUserId,
            UUID buildingId,
            List<ChecklistItemAnswer> answers,
            Instant submittedAt
    ) {
        Objects.requireNonNull(template, "template is required");
        Objects.requireNonNull(answers, "answers are required");
        Set<UUID> templateItems = template.itemIds();
        Map<UUID, ChecklistItemAnswer> answersByItem = new HashMap<>();
        for (ChecklistItemAnswer answer : answers) {
            if (!templateItems.contains(answer.itemId())) {
                throw new IllegalArgumentException(
                        "item " + answer.itemId() + " does not belong to template " + template.id());
            }
            if (answersByItem.putIfAbsent(answer.itemId(), answer) != null) {
                throw new IllegalArgumentException("item " + answer.itemId() + " is answered more than once");
            }
        }
        List<String> missing = template.items().stream()
                .map(ChecklistItem::id)
                .filter(itemId -> !answersByItem.containsKey(itemId))
                .map(UUID::toString)
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "answers must cover every item of the template; missing items: " + String.join(", ", missing));
        }
        List<ChecklistItemAnswer> ordered = template.items().stream()
                .map(item -> answersByItem.get(item.id()))
                .toList();
        return new ChecklistResponse(UUID.randomUUID(), template.id(), residentUserId, buildingId,
                submittedAt, ordered);
    }
}
