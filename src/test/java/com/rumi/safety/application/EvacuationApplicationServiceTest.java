package com.rumi.safety.application;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.domain.model.EvacuationPlan;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvacuationApplicationServiceTest {

    private final InMemoryEvacuationPlanRepository repository = new InMemoryEvacuationPlanRepository();
    private final EvacuationApplicationService service = new EvacuationApplicationService(repository);

    @Test
    void returnsThePlanOfTheBuilding() {
        repository.save(SafetyFixtures.plan());

        assertThat(service.getPlan(BUILDING_ID)).isEqualTo(SafetyFixtures.plan());
    }

    @Test
    void returnsTheLatestVersion() {
        repository.save(SafetyFixtures.plan());
        repository.save(new EvacuationPlan(UUID.randomUUID(), BUILDING_ID, 2, Instant.now(), List.of(), List.of()));

        assertThat(service.getPlan(BUILDING_ID).version()).isEqualTo(2);
    }

    @Test
    void failsWhenTheBuildingHasNoPlan() {
        UUID otherBuilding = UUID.randomUUID();

        assertThatThrownBy(() -> service.getPlan(otherBuilding))
                .isInstanceOf(EvacuationPlanNotFoundException.class)
                .hasMessage("building " + otherBuilding + " has no evacuation plan");
    }
}
