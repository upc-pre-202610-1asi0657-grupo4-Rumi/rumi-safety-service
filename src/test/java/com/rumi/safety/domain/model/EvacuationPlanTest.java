package com.rumi.safety.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvacuationPlanTest {

    @Test
    void treatsMissingRoutesAndSafePointsAsEmpty() {
        EvacuationPlan plan = new EvacuationPlan(UUID.randomUUID(), UUID.randomUUID(), 1, Instant.now(), null, null);

        assertThat(plan.routes()).isEmpty();
        assertThat(plan.safePoints()).isEmpty();
    }

    @Test
    void rejectsAVersionBelowOne() {
        assertThatThrownBy(() -> new EvacuationPlan(UUID.randomUUID(), UUID.randomUUID(), 0, Instant.now(), null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsCoordinatesOutOfRange() {
        assertThatThrownBy(() -> new GeoPoint(-91, 0)).hasMessageContaining("latitude");
        assertThatThrownBy(() -> new GeoPoint(0, 181)).hasMessageContaining("longitude");
    }

    @Test
    void rejectsARouteWithoutDescription() {
        assertThatThrownBy(() -> new EvacuationRoute(UUID.randomUUID(), " ", null))
                .hasMessage("description is required");
    }
}
