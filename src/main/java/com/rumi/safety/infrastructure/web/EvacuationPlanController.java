package com.rumi.safety.infrastructure.web;

import com.rumi.safety.application.EvacuationApplicationService;
import com.rumi.safety.infrastructure.web.dto.EvacuationPlanResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evacuation-plans")
@Tag(name = "Evacuation plans", description = "Evacuation routes and safe points of a building (US19)")
public class EvacuationPlanController {

    private final EvacuationApplicationService evacuationService;

    public EvacuationPlanController(EvacuationApplicationService evacuationService) {
        this.evacuationService = evacuationService;
    }

    @Operation(
            summary = "Get the evacuation plan of a building",
            description = "US19. Returns the latest version of the evacuation plan of the building, with its "
                    + "evacuation routes (ordered by description) and its safe points (ordered by name). "
                    + "The building id is not checked against the Building Management service."
    )
    @ApiResponse(responseCode = "200", description = "The evacuation plan of the building",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = EvacuationPlanResponseDto.class),
                    examples = @ExampleObject(name = "Plan of the sample building",
                            value = OpenApiExamples.EVACUATION_PLAN)))
    @ApiResponse(responseCode = "400", description = "buildingId is missing or is not a valid UUID",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Missing buildingId", value = OpenApiExamples.BUILDING_ID_REQUIRED),
                            @ExampleObject(name = "Malformed buildingId", value = OpenApiExamples.BUILDING_ID_MALFORMED)
                    }))
    @ApiResponse(responseCode = "404", description = "The building has no evacuation plan",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(name = "No plan", value = OpenApiExamples.PLAN_NOT_FOUND)))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public EvacuationPlanResponseDto getPlan(
            @Parameter(description = "Id of the building whose plan is requested", required = true,
                    example = OpenApiExamples.SAMPLE_BUILDING_ID)
            @RequestParam UUID buildingId
    ) {
        return EvacuationPlanDtoMapper.toDto(evacuationService.getPlan(buildingId));
    }
}
