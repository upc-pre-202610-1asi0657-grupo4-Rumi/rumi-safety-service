package com.rumi.safety.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "ChecklistItem", description = "One question of the checklist")
public record ChecklistItemResponseDto(
        @Schema(description = "Id of the item; send it back as itemId in the answers",
                example = "b1b2c3d4-0002-4000-8000-000000000001")
        UUID id,
        @Schema(description = "Order in which the item is shown, starting at 1", example = "1")
        int position,
        @Schema(description = "Question shown to the resident", example = "Gas: do you smell gas or hear a leak?")
        String label
) {
}
