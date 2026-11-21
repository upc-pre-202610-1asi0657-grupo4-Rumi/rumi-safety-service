package com.rumi.safety;

import com.rumi.safety.domain.model.ChecklistItem;
import com.rumi.safety.domain.model.ChecklistItemAnswer;
import com.rumi.safety.domain.model.ChecklistTemplate;
import com.rumi.safety.domain.model.EvacuationPlan;
import com.rumi.safety.domain.model.EvacuationRoute;
import com.rumi.safety.domain.model.GeoPoint;
import com.rumi.safety.domain.model.SafePoint;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class SafetyFixtures {

    public static final UUID BUILDING_ID = UUID.fromString("7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d");
    public static final UUID RESIDENT_ID = UUID.fromString("3f2504e0-4f89-41d3-9a0c-0305e82c3301");
    public static final UUID PLAN_ID = UUID.fromString("a1b2c3d4-0001-4000-8000-000000000001");
    public static final UUID TEMPLATE_ID = UUID.fromString("b1b2c3d4-0001-4000-8000-000000000001");
    public static final UUID GAS_ITEM_ID = UUID.fromString("b1b2c3d4-0002-4000-8000-000000000001");
    public static final UUID CRACKS_ITEM_ID = UUID.fromString("b1b2c3d4-0002-4000-8000-000000000002");
    public static final UUID ELECTRICITY_ITEM_ID = UUID.fromString("b1b2c3d4-0002-4000-8000-000000000003");
    public static final Instant NOW = Instant.parse("2026-11-20T15:05:00Z");

    private SafetyFixtures() {
    }

    public static EvacuationPlan plan() {
        return new EvacuationPlan(PLAN_ID, BUILDING_ID, 1, Instant.parse("2026-11-20T15:00:00Z"),
                List.of(new EvacuationRoute(UUID.fromString("a1b2c3d4-0002-4000-8000-000000000001"),
                        "Main stairs, north wing", "https://maps.example.com/rumi/routes/main-stairs.png")),
                List.of(new SafePoint(UUID.fromString("a1b2c3d4-0003-4000-8000-000000000001"),
                        "Meeting point: park in front of the building", new GeoPoint(-12.0464, -77.0428))));
    }

    public static ChecklistTemplate template() {
        return new ChecklistTemplate(TEMPLATE_ID, "POST_EARTHQUAKE_SELF_CHECK", 1, List.of(
                new ChecklistItem(GAS_ITEM_ID, 1, "Gas: do you smell gas or hear a leak?"),
                new ChecklistItem(CRACKS_ITEM_ID, 2, "Cracks: do you see new cracks in walls, floors or columns?"),
                new ChecklistItem(ELECTRICITY_ITEM_ID, 3,
                        "Electricity: are there exposed wires, sparks or a power failure?")));
    }

    public static ChecklistItemAnswer answer(UUID itemId, boolean checked) {
        return new ChecklistItemAnswer(UUID.randomUUID(), itemId, checked);
    }

    public static List<ChecklistItemAnswer> completeAnswers() {
        return List.of(answer(GAS_ITEM_ID, false), answer(CRACKS_ITEM_ID, true), answer(ELECTRICITY_ITEM_ID, false));
    }
}
