package com.rumi.safety.infrastructure.web;

import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.infrastructure.web.dto.ChecklistItemResponseDto;
import com.rumi.safety.infrastructure.web.dto.ChecklistTemplateResponseDto;

final class ChecklistDtoMapper {

    private ChecklistDtoMapper() {
    }

    static ChecklistTemplateResponseDto toDto(ChecklistTemplate template) {
        return new ChecklistTemplateResponseDto(
                template.id(),
                template.name(),
                template.version(),
                template.items().stream()
                        .map(item -> new ChecklistItemResponseDto(item.id(), item.position(), item.label()))
                        .toList());
    }
}
