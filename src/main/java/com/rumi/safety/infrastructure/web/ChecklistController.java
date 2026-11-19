package com.rumi.safety.infrastructure.web;

import com.rumi.safety.application.ChecklistApplicationService;
import com.rumi.safety.infrastructure.web.dto.ChecklistTemplateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checklists")
@Tag(name = "Checklists", description = "Post-earthquake self-check of residents (US20)")
public class ChecklistController {

    private final ChecklistApplicationService checklistService;

    public ChecklistController(ChecklistApplicationService checklistService) {
        this.checklistService = checklistService;
    }

    @Operation(
            summary = "Get the current checklist template",
            description = "US20. Returns the post-earthquake checklist residents must answer: the template with "
                    + "the highest version, with its items ordered by position. The default template "
                    + "POST_EARTHQUAKE_SELF_CHECK is seeded on startup when no template exists."
    )
    @ApiResponse(responseCode = "200", description = "The current checklist template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChecklistTemplateResponseDto.class),
                    examples = @ExampleObject(name = "Default template", value = OpenApiExamples.CHECKLIST_TEMPLATE)))
    @ApiResponse(responseCode = "404", description = "No checklist template exists",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "No template", value = OpenApiExamples.NO_TEMPLATE)))
    @GetMapping(path = "/templates/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChecklistTemplateResponseDto getCurrentTemplate() {
        return ChecklistDtoMapper.toDto(checklistService.getCurrentTemplate());
    }
}
