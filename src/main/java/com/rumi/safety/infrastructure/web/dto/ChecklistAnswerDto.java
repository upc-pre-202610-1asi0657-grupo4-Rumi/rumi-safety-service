package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "ChecklistAnswer", description = "The answer of the resident to one item of the checklist")
public record ChecklistAnswerDto(
        @Schema(description = "Id of the template item being answered",
                example = "b1b2c3d4-0002-4000-8000-000000000001", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID itemId,
        @Schema(description = "true when the resident observed the hazard of the item", example = "false",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Boolean checked
) {
}
