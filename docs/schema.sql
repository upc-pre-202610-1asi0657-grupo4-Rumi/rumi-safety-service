-- Schema of evacuationdb (PostgreSQL), the database of rumi-safety-service.
-- The default profile runs Hibernate with ddl-auto=validate, so these tables must exist.
-- Create the database first:  CREATE DATABASE evacuationdb;
-- Buildings and users belong to other services: they are referenced by identifier only, without
-- foreign keys (one database per service).

-- Evacuation plans (US17). A building keeps every version of its plan; the latest version is the current one.
CREATE TABLE evacuation_plans (
    id          uuid      PRIMARY KEY,
    building_id uuid      NOT NULL,
    version     integer   NOT NULL,
    updated_at  timestamp NOT NULL
);

CREATE TABLE evacuation_routes (
    id          uuid         PRIMARY KEY,
    plan_id     uuid         NOT NULL REFERENCES evacuation_plans (id) ON DELETE CASCADE,
    description varchar(300) NOT NULL,
    map_url     varchar(300)
);

CREATE TABLE safe_points (
    id                 uuid             PRIMARY KEY,
    plan_id            uuid             NOT NULL REFERENCES evacuation_plans (id) ON DELETE CASCADE,
    name               varchar(100)     NOT NULL,
    location_latitude  double precision,
    location_longitude double precision
);

-- Preparedness checklists (US18). ChecklistTemplateSeeder creates the default template on startup.
CREATE TABLE checklist_templates (
    id      uuid         PRIMARY KEY,
    name    varchar(100) NOT NULL,
    version integer      NOT NULL
);

CREATE TABLE checklist_items (
    id          uuid         PRIMARY KEY,
    template_id uuid         NOT NULL REFERENCES checklist_templates (id) ON DELETE CASCADE,
    position    integer      NOT NULL,
    label       varchar(150) NOT NULL
);

CREATE TABLE checklist_responses (
    id               uuid      PRIMARY KEY,
    template_id      uuid      NOT NULL REFERENCES checklist_templates (id),
    building_id      uuid      NOT NULL,
    resident_user_id uuid      NOT NULL,
    submitted_at     timestamp NOT NULL
);

CREATE TABLE checklist_item_answers (
    id          uuid    PRIMARY KEY,
    response_id uuid    NOT NULL REFERENCES checklist_responses (id) ON DELETE CASCADE,
    item_id     uuid    NOT NULL REFERENCES checklist_items (id),
    checked     boolean NOT NULL,
    CONSTRAINT uq_checklist_item_answers_response_item UNIQUE (response_id, item_id)
);

CREATE INDEX evacuation_plans_building_id_version_idx ON evacuation_plans (building_id, version DESC);
CREATE INDEX evacuation_routes_plan_id_idx ON evacuation_routes (plan_id);
CREATE INDEX safe_points_plan_id_idx ON safe_points (plan_id);
CREATE INDEX checklist_items_template_id_idx ON checklist_items (template_id);
CREATE INDEX checklist_responses_building_id_idx ON checklist_responses (building_id);
CREATE INDEX checklist_item_answers_item_id_idx ON checklist_item_answers (item_id);
