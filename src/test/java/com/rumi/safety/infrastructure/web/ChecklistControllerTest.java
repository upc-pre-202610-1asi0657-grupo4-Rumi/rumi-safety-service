package com.rumi.safety.infrastructure.web;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.application.ChecklistApplicationService;
import com.rumi.safety.application.ChecklistTemplateNotFoundException;
import com.rumi.safety.application.SubmitChecklistResponseCommand;
import com.rumi.safety.domain.model.ChecklistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static com.rumi.safety.SafetyFixtures.CRACKS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.ELECTRICITY_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.GAS_ITEM_ID;
import static com.rumi.safety.SafetyFixtures.NOW;
import static com.rumi.safety.SafetyFixtures.RESIDENT_ID;
import static com.rumi.safety.SafetyFixtures.TEMPLATE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChecklistController.class)
class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChecklistApplicationService checklistService;

    @Test
    void returnsTheCurrentTemplateWithItsItemsInOrder() throws Exception {
        when(checklistService.getCurrentTemplate()).thenReturn(SafetyFixtures.template());

        mockMvc.perform(get("/api/v1/checklists/templates/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SafetyFixtures.TEMPLATE_ID.toString()))
                .andExpect(jsonPath("$.name").value("POST_EARTHQUAKE_SELF_CHECK"))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.items[0].position").value(1))
                .andExpect(jsonPath("$.items[0].label").value("Gas: do you smell gas or hear a leak?"))
                .andExpect(jsonPath("$.items[2].id").value(SafetyFixtures.ELECTRICITY_ITEM_ID.toString()));
    }

    @Test
    void answers404WhenNoTemplateExists() throws Exception {
        when(checklistService.getCurrentTemplate()).thenThrow(ChecklistTemplateNotFoundException.noTemplate());

        mockMvc.perform(get("/api/v1/checklists/templates/current"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("no checklist template exists"));
    }

    private static final String VALID_BODY = """
            {
              "templateId": "%s",
              "buildingId": "%s",
              "answers": [
                { "itemId": "%s", "checked": false },
                { "itemId": "%s", "checked": true },
                { "itemId": "%s", "checked": false }
              ]
            }""".formatted(TEMPLATE_ID, BUILDING_ID, GAS_ITEM_ID, CRACKS_ITEM_ID, ELECTRICITY_ITEM_ID);

    @Test
    void savesAResponseAndAnswers201() throws Exception {
        ChecklistResponse saved = ChecklistResponse.submit(
                SafetyFixtures.template(), RESIDENT_ID, BUILDING_ID, SafetyFixtures.completeAnswers(), NOW);
        when(checklistService.submitResponse(new SubmitChecklistResponseCommand(TEMPLATE_ID, RESIDENT_ID, BUILDING_ID,
                List.of(
                        new SubmitChecklistResponseCommand.ItemAnswer(GAS_ITEM_ID, false),
                        new SubmitChecklistResponseCommand.ItemAnswer(CRACKS_ITEM_ID, true),
                        new SubmitChecklistResponseCommand.ItemAnswer(ELECTRICITY_ITEM_ID, false)))))
                .thenReturn(saved);

        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.id().toString()))
                .andExpect(jsonPath("$.templateId").value(TEMPLATE_ID.toString()))
                .andExpect(jsonPath("$.residentUserId").value(RESIDENT_ID.toString()))
                .andExpect(jsonPath("$.buildingId").value(BUILDING_ID.toString()))
                .andExpect(jsonPath("$.submittedAt").value("2026-11-20T15:05:00Z"))
                .andExpect(jsonPath("$.answers[1].itemId").value(CRACKS_ITEM_ID.toString()))
                .andExpect(jsonPath("$.answers[1].checked").value(true));
    }

    @Test
    void rejectsARequestWithoutXUserId() throws Exception {
        mockMvc.perform(post("/api/v1/checklists/responses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("X-User-Id header is required"));
        verify(checklistService, never()).submitResponse(any());
    }

    @Test
    void rejectsAMalformedXUserId() throws Exception {
        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", "resident-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("X-User-Id must be a valid UUID"));
    }

    @Test
    void rejectsABodyWithoutTemplateAndAnswers() throws Exception {
        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"buildingId\": \"" + BUILDING_ID + "\", \"answers\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("answers must not be empty; templateId must not be null"))
                .andExpect(jsonPath("$.errors.templateId").value("must not be null"))
                .andExpect(jsonPath("$.errors.answers").value("must not be empty"));
        verify(checklistService, never()).submitResponse(any());
    }

    @Test
    void rejectsAnAnswerWithoutChecked() throws Exception {
        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replaceFirst("\"checked\": false", "\"checked\": null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['answers[0].checked']").value("must not be null"));
    }

    @Test
    void rejectsAMalformedUuidInTheBody() throws Exception {
        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replace(TEMPLATE_ID.toString(), "not-a-uuid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("templateId must be a valid UUID"));
    }

    @Test
    void rejectsAnswersThatDoNotCoverTheTemplate() throws Exception {
        when(checklistService.submitResponse(any())).thenThrow(new IllegalArgumentException(
                "answers must cover every item of the template; missing items: " + ELECTRICITY_ITEM_ID));

        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "answers must cover every item of the template; missing items: " + ELECTRICITY_ITEM_ID));
    }

    @Test
    void answers404ForAnUnknownTemplate() throws Exception {
        UUID unknownTemplate = UUID.randomUUID();
        when(checklistService.submitResponse(any()))
                .thenThrow(ChecklistTemplateNotFoundException.withId(unknownTemplate));

        mockMvc.perform(post("/api/v1/checklists/responses")
                        .header("X-User-Id", RESIDENT_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replace(TEMPLATE_ID.toString(), unknownTemplate.toString())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("checklist template " + unknownTemplate + " does not exist"));
    }
}
