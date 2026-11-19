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

    static final String SAMPLE_RESIDENT_ID = "3f2504e0-4f89-41d3-9a0c-0305e82c3301";

    static final String SUBMIT_RESPONSE_REQUEST = """
            {
              "templateId": "b1b2c3d4-0001-4000-8000-000000000001",
              "buildingId": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "answers": [
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000001", "checked": false },
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000002", "checked": true },
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000003", "checked": false }
              ]
            }""";

    static final String SUBMITTED_RESPONSE = """
            {
              "id": "c1b2c3d4-0001-4000-8000-000000000001",
              "templateId": "b1b2c3d4-0001-4000-8000-000000000001",
              "residentUserId": "3f2504e0-4f89-41d3-9a0c-0305e82c3301",
              "buildingId": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
              "submittedAt": "2026-11-20T15:05:00Z",
              "answers": [
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000001", "checked": false },
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000002", "checked": true },
                { "itemId": "b1b2c3d4-0002-4000-8000-000000000003", "checked": false }
              ]
            }""";

    static final String USER_ID_REQUIRED = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "X-User-Id header is required",
              "instance": "/api/v1/checklists/responses"
            }""";

    static final String USER_ID_MALFORMED = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "X-User-Id must be a valid UUID",
              "instance": "/api/v1/checklists/responses"
            }""";

    static final String INVALID_BODY = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "answers must not be empty; templateId must not be null",
              "instance": "/api/v1/checklists/responses",
              "errors": {
                "answers": "must not be empty",
                "templateId": "must not be null"
              }
            }""";

    static final String MISSING_ITEM = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "answers must cover every item of the template; missing items: b1b2c3d4-0002-4000-8000-000000000003",
              "instance": "/api/v1/checklists/responses"
            }""";

    static final String REPEATED_ITEM = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "item b1b2c3d4-0002-4000-8000-000000000001 is answered more than once",
              "instance": "/api/v1/checklists/responses"
            }""";

    static final String UNKNOWN_TEMPLATE = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "checklist template 9c0e8f3a-1b2d-4e5f-8a6b-7c8d9e0f1a2b does not exist",
              "instance": "/api/v1/checklists/responses"
            }""";

    private OpenApiExamples() {
    }
}
