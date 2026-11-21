package com.rumi.safety.infrastructure.seed;

import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.model.EvacuationRoute;
import com.rumi.safety.domain.model.GeoPoint;
import com.rumi.safety.domain.model.SafePoint;
import com.rumi.safety.domain.repository.EvacuationPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Seeds the evacuation plan of the sample building of the dev profile, unless it already has one.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    public static final UUID SAMPLE_BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    public static final UUID SAMPLE_PLAN_ID = UUID.fromString("a1b2c3d4-0001-4000-8000-000000000001");

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final EvacuationPlanRepository planRepository;

    public DevDataSeeder(EvacuationPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (planRepository.findLatestByBuildingId(SAMPLE_BUILDING_ID).isPresent()) {
            return;
        }
        planRepository.save(new EvacuationPlan(
                SAMPLE_PLAN_ID,
                SAMPLE_BUILDING_ID,
                1,
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                List.of(
                        new EvacuationRoute(UUID.fromString("a1b2c3d4-0002-4000-8000-000000000001"),
                                "Main stairs, north wing",
                                "https://maps.example.com/rumi/routes/main-stairs.png"),
                        new EvacuationRoute(UUID.fromString("a1b2c3d4-0002-4000-8000-000000000002"),
                                "Rear exit to the courtyard",
                                "https://maps.example.com/rumi/routes/rear-exit.png")),
                List.of(
                        new SafePoint(UUID.fromString("a1b2c3d4-0003-4000-8000-000000000001"),
                                "Meeting point: park in front of the building", new GeoPoint(-12.0464, -77.0428)),
                        new SafePoint(UUID.fromString("a1b2c3d4-0003-4000-8000-000000000002"),
                                "Meeting point: courtyard", new GeoPoint(-12.0466, -77.0431)))));
        log.info("Seeded the evacuation plan of sample building {}", SAMPLE_BUILDING_ID);
    }
}
