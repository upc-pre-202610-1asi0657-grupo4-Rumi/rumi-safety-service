package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "ChecklistTemplate", description = "Checklist residents answer after an earthquake")
public record ChecklistTemplateResponseDto(
        @Schema(description = "Id of the template; send it back as templateId in the response",
                example = "b1b2c3d4-0001-4000-8000-000000000001")
        UUID id,
        @Schema(description = "Name of the template", example = "POST_EARTHQUAKE_SELF_CHECK")
        String name,
        @Schema(description = "Version of the template; the highest version is the current one", example = "1")
        int version,
        @Schema(description = "Items ordered by position")
        List<ChecklistItemResponseDto> items
) {
}
