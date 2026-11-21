package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(name = "SubmitChecklistResponseRequest",
        description = "Answers of a resident to the post-earthquake checklist")
public record SubmitChecklistResponseRequestDto(
        @Schema(description = "Id of the template being answered",
                example = "b1b2c3d4-0001-4000-8000-000000000001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID templateId,
        @Schema(description = "Id of the building of the resident (Building Management context)",
                example = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID buildingId,
        @Schema(description = "One answer for every item of the template, each item exactly once",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty
        List<@Valid @NotNull ChecklistAnswerDto> answers
) {
}
