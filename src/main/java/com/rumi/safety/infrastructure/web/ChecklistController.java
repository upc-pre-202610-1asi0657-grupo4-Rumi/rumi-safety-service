package com.rumi.safety.infrastructure.web;

import com.rumi.safety.application.ChecklistApplicationService;
import com.rumi.safety.infrastructure.web.dto.ChecklistResponseResponseDto;
import com.rumi.safety.infrastructure.web.dto.ChecklistTemplateResponseDto;
import com.rumi.safety.infrastructure.web.dto.SubmitChecklistResponseRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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

    @Operation(
            summary = "Submit a post-earthquake checklist",
            description = "US20. Saves the answers of the calling resident to a checklist template. The resident "
                    + "is taken from the X-User-Id header, which the API gateway sets after validating the JWT. "
                    + "The answers must cover every item of the template exactly once; they are returned in the "
                    + "order of the template items. The building id is not checked against other services."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "Template, building and one answer per item of the template",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SubmitChecklistResponseRequestDto.class),
                    examples = @ExampleObject(name = "Cracks observed", value = OpenApiExamples.SUBMIT_RESPONSE_REQUEST)))
    @ApiResponse(responseCode = "201", description = "The response was saved",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChecklistResponseResponseDto.class),
                    examples = @ExampleObject(name = "Saved response", value = OpenApiExamples.SUBMITTED_RESPONSE)))
    @ApiResponse(responseCode = "400",
            description = "X-User-Id is missing or malformed, the body is invalid, or the answers do not cover "
                    + "every item of the template exactly once",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Missing X-User-Id", value = OpenApiExamples.USER_ID_REQUIRED),
                            @ExampleObject(name = "Malformed X-User-Id", value = OpenApiExamples.USER_ID_MALFORMED),
                            @ExampleObject(name = "Invalid body", value = OpenApiExamples.INVALID_BODY),
                            @ExampleObject(name = "Item not answered", value = OpenApiExamples.MISSING_ITEM),
                            @ExampleObject(name = "Item answered twice", value = OpenApiExamples.REPEATED_ITEM)
                    }))
    @ApiResponse(responseCode = "404", description = "The template does not exist",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "Unknown template", value = OpenApiExamples.UNKNOWN_TEMPLATE)))
    @PostMapping(path = "/responses", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistResponseResponseDto submitResponse(
            @Parameter(in = ParameterIn.HEADER, name = "X-User-Id", required = true,
                    description = "Id of the calling resident, set by the API gateway from the JWT",
                    example = OpenApiExamples.SAMPLE_RESIDENT_ID)
            @RequestHeader("X-User-Id") UUID residentUserId,
            @Valid @RequestBody SubmitChecklistResponseRequestDto request
    ) {
        return ChecklistDtoMapper.toDto(
                checklistService.submitResponse(ChecklistDtoMapper.toCommand(residentUserId, request)));
    }
}
