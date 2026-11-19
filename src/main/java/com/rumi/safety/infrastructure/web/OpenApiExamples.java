package com.rumi.safety.infrastructure.web;

/**
 * Example payloads shown in the OpenAPI documentation. They match the data seeded by the dev profile.
 */
final class OpenApiExamples {

    static final String SAMPLE_BUILDING_ID = "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d";

    static final String EVACUATION_PLAN = """
            {
              "id": "a1b2c3d4-0001-4000-8000-000000000001",
              "buildingId": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "version": 1,
              "updatedAt": "2026-11-20T15:00:00Z",
              "routes": [
                {
                  "id": "a1b2c3d4-0002-4000-8000-000000000001",
                  "description": "Main stairs, north wing",
                  "mapUrl": "https://maps.example.com/rumi/routes/main-stairs.png"
                },
                {
                  "id": "a1b2c3d4-0002-4000-8000-000000000002",
                  "description": "Rear exit to the courtyard",
                  "mapUrl": "https://maps.example.com/rumi/routes/rear-exit.png"
                }
              ],
              "safePoints": [
                {
                  "id": "a1b2c3d4-0003-4000-8000-000000000002",
                  "name": "Meeting point: courtyard",
                  "location": { "latitude": -12.0466, "longitude": -77.0431 }
                },
                {
                  "id": "a1b2c3d4-0003-4000-8000-000000000001",
                  "name": "Meeting point: park in front of the building",
                  "location": { "latitude": -12.0464, "longitude": -77.0428 }
                }
              ]
            }""";

    static final String BUILDING_ID_REQUIRED = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "buildingId is required",
              "instance": "/api/v1/evacuation-plans"
            }""";

    static final String BUILDING_ID_MALFORMED = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "buildingId must be a valid UUID",
              "instance": "/api/v1/evacuation-plans"
            }""";

    static final String PLAN_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "building 0b6c7e2a-9d1f-4c3b-8a5e-2f4d6b8c0e1a has no evacuation plan",
              "instance": "/api/v1/evacuation-plans"
            }""";

    static final String CHECKLIST_TEMPLATE = """
            {
              "id": "b1b2c3d4-0001-4000-8000-000000000001",
              "name": "POST_EARTHQUAKE_SELF_CHECK",
              "version": 1,
              "items": [
                {
                  "id": "b1b2c3d4-0002-4000-8000-000000000001",
                  "position": 1,
                  "label": "Gas: do you smell gas or hear a leak?"
                },
                {
                  "id": "b1b2c3d4-0002-4000-8000-000000000002",
                  "position": 2,
                  "label": "Cracks: do you see new cracks in walls, floors or columns?"
                },
                {
                  "id": "b1b2c3d4-0002-4000-8000-000000000003",
                  "position": 3,
                  "label": "Electricity: are there exposed wires, sparks or a power failure?"
                }
              ]
            }""";

    static final String NO_TEMPLATE = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "no checklist template exists",
              "instance": "/api/v1/checklists/templates/current"
            }""";

    private OpenApiExamples() {
    }
}
