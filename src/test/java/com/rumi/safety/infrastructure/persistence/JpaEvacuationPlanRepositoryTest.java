package com.rumi.safety.infrastructure.persistence;

import com.rumi.safety.SafetyFixtures;
import com.rumi.safety.domain.model.EvacuationPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.rumi.safety.SafetyFixtures.BUILDING_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false, properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import(JpaEvacuationPlanRepository.class)
class JpaEvacuationPlanRepositoryTest {

    @Autowired
    private JpaEvacuationPlanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAPlanWithItsRoutesAndSafePoints() {
        repository.save(SafetyFixtures.plan());
        entityManager.flush();
        entityManager.clear();

        EvacuationPlan found = repository.findLatestByBuildingId(BUILDING_ID).orElseThrow();

        assertThat(found).isEqualTo(SafetyFixtures.plan());
        SafePointJpaEntity stored = entityManager.find(SafePointJpaEntity.class, found.safePoints().get(0).id());
        assertThat(stored.getLocationLatitude()).isEqualTo(-12.0464);
        assertThat(stored.getPlan().getId()).isEqualTo(found.id());
    }

    @Test
    void returnsTheLatestVersionOfThePlan() {
        repository.save(SafetyFixtures.plan());
        repository.save(new EvacuationPlan(UUID.randomUUID(), BUILDING_ID, 2, Instant.now(), List.of(), List.of()));
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findLatestByBuildingId(BUILDING_ID)).get()
                .extracting(EvacuationPlan::version).isEqualTo(2);
    }

    @Test
    void findsNothingForABuildingWithoutPlan() {
        assertThat(repository.findLatestByBuildingId(UUID.randomUUID())).isEmpty();
    }
}
