package com.rumi.safety;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.repository.EvacuationPlanRepository;
import com.rumi.safety.infrastructure.seed.DevDataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SafetyServiceApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EvacuationPlanRepository planRepository;

    @Test
    void contextLoadsAndTheHealthEndpointAnswers() throws Exception {
        mockMvc.perform(get("/api/v1/evacuation-plans/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("rumi-safety-service"));
    }

    @Test
    void theDevProfileSeedsThePlanOfTheSampleBuilding() {
        EvacuationPlan plan = planRepository.findLatestByBuildingId(DevDataSeeder.SAMPLE_BUILDING_ID).orElseThrow();

        assertThat(plan.id()).isEqualTo(DevDataSeeder.SAMPLE_PLAN_ID);
        assertThat(plan.routes()).hasSize(2);
        assertThat(plan.safePoints()).hasSize(2);
    }
}
