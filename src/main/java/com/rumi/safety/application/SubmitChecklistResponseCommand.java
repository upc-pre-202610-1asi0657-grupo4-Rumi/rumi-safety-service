package com.rumi.safety.application;

import java.util.List;
import java.util.UUID;

/**
 * A resident submits the post-earthquake checklist of a building.
 *
 * @param residentUserId the caller, taken from the X-User-Id header set by the API gateway
 */
public record SubmitChecklistResponseCommand(
        UUID templateId,
        UUID residentUserId,
        UUID buildingId,
        List<ItemAnswer> answers
) {

    public SubmitChecklistResponseCommand {
        answers = answers == null ? List.of() : List.copyOf(answers);
    }

    public record ItemAnswer(UUID itemId, boolean checked) {
    }
}
