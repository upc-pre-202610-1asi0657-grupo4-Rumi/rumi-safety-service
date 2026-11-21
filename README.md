# rumi-safety-service

Resident Safety service of **Rumi**, the structural monitoring platform by Kuntur Labs.

| | |
|---|---|
| Bounded context | Resident Safety |
| Port | `8086` |
| Gateway routes | `/api/v1/evacuation-plans/**`, `/api/v1/checklists/**` |
| Base package | `com.rumi.safety` |
| Database | PostgreSQL `evacuationdb` (H2 in memory with the `dev` profile) |

## Purpose

Owns the evacuation plans of buildings, with their routes and safe points (US19), and the
post-earthquake self-check residents fill in to report gas leaks, cracks and electrical hazards (US20).

## Origin

New service. It has no code in the modular monolith
[`rumi-backend`](https://github.com/upc-pre-202610-1asi0657-grupo4-Rumi/rumi-backend); it is one of the
bounded contexts of the target architecture defined when the monolith was decomposed.

## Endpoints

| # | Verb | Path | Description | User story | Status |
|---|---|---|---|---|---|
| SF1 | GET | `/api/v1/evacuation-plans?buildingId={uuid}` | Evacuation plan of a building with its routes and safe points | US19 | implemented |
| SF2 | GET | `/api/v1/checklists/templates/current` | Current post-earthquake checklist with its items in order | US20 | implemented |
| SF3 | POST | `/api/v1/checklists/responses` | Submit the checklist of the calling resident (`X-User-Id` header) | US20 | implemented |
| | GET | `/api/v1/evacuation-plans/health` | Check that the service is running | none | not counted |

Implemented functional endpoints: 3.

### Caller identity

The API gateway validates the JWT and forwards `X-User-Id`, `X-Role` and `X-Building-Id`. This service does
not validate tokens. `POST /api/v1/checklists/responses` takes the resident from `X-User-Id` (required, UUID).
Building and user ids are plain UUIDs and are not checked against other services.

### Errors

Every error is an RFC 7807 problem detail (`application/problem+json`):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "buildingId is required",
  "instance": "/api/v1/evacuation-plans"
}
```

| Endpoint | 400 | 404 |
|---|---|---|
| SF1 | `buildingId` missing or not a UUID | the building has no plan |
| SF2 | | no template exists |
| SF3 | `X-User-Id` missing or not a UUID; invalid body; answers that do not cover every item of the template exactly once | unknown `templateId` |

### Example: submit a checklist

```sh
curl -X POST http://localhost:8086/api/v1/checklists/responses \
  -H 'Content-Type: application/json' \
  -H 'X-User-Id: 3f2504e0-4f89-41d3-9a0c-0305e82c3301' \
  -d '{
        "templateId": "b1b2c3d4-0001-4000-8000-000000000001",
        "buildingId": "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d",
        "answers": [
          { "itemId": "b1b2c3d4-0002-4000-8000-000000000001", "checked": false },
          { "itemId": "b1b2c3d4-0002-4000-8000-000000000002", "checked": true },
          { "itemId": "b1b2c3d4-0002-4000-8000-000000000003", "checked": false }
        ]
      }'
```

## Seed data

| Data | Profiles | When |
|---|---|---|
| Checklist template `POST_EARTHQUAKE_SELF_CHECK` version 1 (`b1b2c3d4-0001-4000-8000-000000000001`) with the items Gas, Cracks and Electricity | all | only when the template table is empty |
| Evacuation plan of sample building `7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d`: routes "Main stairs, north wing" and "Rear exit to the courtyard"; safe points "Meeting point: park in front of the building" and "Meeting point: courtyard" | `dev` | only when the building has no plan |

## API documentation

- Swagger UI: <http://localhost:8086/swagger-ui.html>
- OpenAPI spec: <http://localhost:8086/v3/api-docs>
- Exported spec: [`docs/openapi.json`](docs/openapi.json)

Every operation has a description and examples of its request, its response and each of its errors.

## Run

Requirements: JDK 21 (the Maven wrapper is included; use `mvnw.cmd` on Windows).

Without any infrastructure (H2 in memory and sample data):

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# or, from the jar
./mvnw package -DskipTests && java -jar target/rumi-safety-service-0.1.0.jar --spring.profiles.active=dev
```

With PostgreSQL (default profile):

```sh
./mvnw spring-boot:run
```

The default profile uses `ddl-auto: validate`, as the other Rumi services: the tables of
`er_06_evacuation_db` must already exist in `evacuationdb`.

| Variable | Default |
|---|---|
| `SERVER_PORT` | `8086` |
| `SAFETY_DB_URL` | `jdbc:postgresql://localhost:5432/evacuationdb` |
| `SAFETY_DB_USERNAME` | `postgres` |
| `SAFETY_DB_PASSWORD` | `postgres` |

## Test

```sh
./mvnw test
```

| Layer | What is tested |
|---|---|
| Domain | invariants of plans, templates and responses (answers cover every item exactly once) |
| Application | `EvacuationApplicationService` and `ChecklistApplicationService` with in-memory repositories |
| Persistence | JPA repositories and the template seeder on H2 |
| REST | `@WebMvcTest` of each controller: success, 400 and 404 |
| Gherkin | `src/test/resources/com/rumi/safety/resident_safety.feature`, every scenario tagged `@US19` or `@US20` |

Run the scenarios of one story: `./mvnw test -Dtest=SafetyCucumberTest -Dcucumber.filter.tags=@US20`.

## Structure

```
com.rumi.safety
├── domain
│   ├── model          EvacuationPlan, EvacuationRoute, SafePoint, GeoPoint,
│   │                  ChecklistTemplate, ChecklistItem, ChecklistResponse, ChecklistItemAnswer
│   └── repository     repository interfaces
├── application        EvacuationApplicationService, ChecklistApplicationService
└── infrastructure     OpenAPI and clock configuration
    ├── persistence    JPA entities and repositories (evacuationdb)
    ├── seed           default checklist template, dev sample plan
    └── web            REST controllers, DTOs, error handling
```
