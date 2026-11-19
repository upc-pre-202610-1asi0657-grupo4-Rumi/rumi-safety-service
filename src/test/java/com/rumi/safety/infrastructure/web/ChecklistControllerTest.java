package com.rumi.safety.infrastructure.web;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.application.ChecklistApplicationService;
import com.rumi.safety.application.ChecklistTemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
