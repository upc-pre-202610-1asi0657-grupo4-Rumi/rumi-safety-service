package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(name = "ChecklistResponse", description = "A checklist submitted by a resident")
public record ChecklistResponseResponseDto(
        @Schema(description = "Id of the saved response", example = "c1b2c3d4-0001-4000-8000-000000000001")
        UUID id,
        @Schema(description = "Id of the answered template", example = "b1b2c3d4-0001-4000-8000-000000000001")
        UUID templateId,
        @Schema(description = "Id of the resident, taken from the X-User-Id header",
                example = "3f2504e0-4f89-41d3-9a0c-0305e82c3301")
        UUID residentUserId,
        @Schema(description = "Id of the building", example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d")
        UUID buildingId,
        @Schema(description = "When the response was saved, in UTC", example = "2026-11-20T15:05:00Z")
        Instant submittedAt,
        @Schema(description = "Answers in the order of the template items")
        List<ChecklistAnswerDto> answers
) {
}
