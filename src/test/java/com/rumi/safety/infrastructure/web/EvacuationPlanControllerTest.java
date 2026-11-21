package com.rumi.safety.infrastructure.web;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.application.EvacuationApplicationService;
import com.rumi.safety.application.EvacuationPlanNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EvacuationPlanController.class)
class EvacuationPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvacuationApplicationService evacuationService;

    @Test
    void returnsThePlanOfTheBuilding() throws Exception {
        when(evacuationService.getPlan(BUILDING_ID)).thenReturn(SafetyFixtures.plan());

        mockMvc.perform(get("/api/v1/evacuation-plans").param("buildingId", BUILDING_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SafetyFixtures.PLAN_ID.toString()))
                .andExpect(jsonPath("$.buildingId").value(BUILDING_ID.toString()))
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.updatedAt").value("2026-11-20T15:00:00Z"))
                .andExpect(jsonPath("$.routes[0].description").value("Main stairs, north wing"))
                .andExpect(jsonPath("$.routes[0].mapUrl").value("https://maps.example.com/rumi/routes/main-stairs.png"))
                .andExpect(jsonPath("$.safePoints[0].name").value("Meeting point: park in front of the building"))
                .andExpect(jsonPath("$.safePoints[0].location.latitude").value(-12.0464))
                .andExpect(jsonPath("$.safePoints[0].location.longitude").value(-77.0428));
    }

    @Test
    void rejectsAMissingBuildingId() throws Exception {
        mockMvc.perform(get("/api/v1/evacuation-plans"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("buildingId is required"));
    }

    @Test
    void rejectsAMalformedBuildingId() throws Exception {
        mockMvc.perform(get("/api/v1/evacuation-plans").param("buildingId", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("buildingId must be a valid UUID"));
    }

    @Test
    void answers404WhenTheBuildingHasNoPlan() throws Exception {
        UUID otherBuilding = UUID.randomUUID();
        when(evacuationService.getPlan(otherBuilding)).thenThrow(new EvacuationPlanNotFoundException(otherBuilding));

        mockMvc.perform(get("/api/v1/evacuation-plans").param("buildingId", otherBuilding.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("building " + otherBuilding + " has no evacuation plan"));
    }
}
