package com.rumi.safety.application;

import java.util.UUID;

public class EvacuationPlanNotFoundException extends ResourceNotFoundException {

    public EvacuationPlanNotFoundException(UUID buildingId) {
        super("building " + buildingId + " has no evacuation plan");
    }
}
