package com.rumi.safety.infrastructure.web;

import com.rumi.safety.application.SubmitChecklistResponseCommand;
import com.rumi.safety.domain.model.ChecklistResponse;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.infrastructure.web.dto.ChecklistAnswerDto;
import com.rumi.safety.infrastructure.web.dto.ChecklistItemResponseDto;
import com.rumi.safety.infrastructure.web.dto.ChecklistResponseResponseDto;
import com.rumi.safety.infrastructure.web.dto.ChecklistTemplateResponseDto;
import com.rumi.safety.infrastructure.web.dto.SubmitChecklistResponseRequestDto;

import java.util.UUID;

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

    static SubmitChecklistResponseCommand toCommand(UUID residentUserId, SubmitChecklistResponseRequestDto request) {
        return new SubmitChecklistResponseCommand(
                request.templateId(),
                residentUserId,
                request.buildingId(),
                request.answers().stream()
                        .map(answer -> new SubmitChecklistResponseCommand.ItemAnswer(answer.itemId(), answer.checked()))
                        .toList());
    }

    static ChecklistResponseResponseDto toDto(ChecklistResponse response) {
        return new ChecklistResponseResponseDto(
                response.id(),
                response.templateId(),
                response.residentUserId(),
                response.buildingId(),
                response.submittedAt(),
                response.answers().stream()
                        .map(answer -> new ChecklistAnswerDto(answer.itemId(), answer.checked()))
                        .toList());
    }
}
