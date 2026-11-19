Feature: Resident safety after an earthquake
  Residents look up how to leave their building and report the hazards
  they observe in a post-earthquake self-check.

  Background:
    Given the service runs with the dev sample data

  @US19
  Scenario: A resident gets the evacuation plan of the building
    When the resident requests the evacuation plan of building "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d"
    Then the response status is 200
    And the plan has 2 routes and 2 safe points
    And the plan includes the route "Main stairs, north wing"
    And the plan includes the safe point "Meeting point: courtyard" at latitude -12.0466 and longitude -77.0431

  @US19
  Scenario: A building without an evacuation plan
    When the resident requests the evacuation plan of building "0b6c7e2a-9d1f-4c3b-8a5e-2f4d6b8c0e1a"
    Then the response status is 404
    And the problem detail is "building 0b6c7e2a-9d1f-4c3b-8a5e-2f4d6b8c0e1a has no evacuation plan"

  @US20
  Scenario: The current checklist is returned with its items in order
    When the resident requests the current checklist template
    Then the response status is 200
    And the checklist is "POST_EARTHQUAKE_SELF_CHECK" version 1
    And the checklist items are, in order:
      | position | label                                                            |
      | 1        | Gas: do you smell gas or hear a leak?                            |
      | 2        | Cracks: do you see new cracks in walls, floors or columns?       |
      | 3        | Electricity: are there exposed wires, sparks or a power failure? |

  @US20
  Scenario: A complete checklist is accepted
    Given the resident "3f2504e0-4f89-41d3-9a0c-0305e82c3301" has opened the current checklist
    When the resident submits the checklist for building "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d" answering:
      | position | checked |
      | 1        | false   |
      | 2        | true    |
      | 3        | false   |
    Then the response status is 201
    And the saved response belongs to resident "3f2504e0-4f89-41d3-9a0c-0305e82c3301"
    And the saved answers are, in order:
      | position | checked |
      | 1        | false   |
      | 2        | true    |
      | 3        | false   |

  @US20
  Scenario: A checklist with an unanswered item is rejected
    Given the resident "3f2504e0-4f89-41d3-9a0c-0305e82c3301" has opened the current checklist
    When the resident submits the checklist for building "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d" answering:
      | position | checked |
      | 1        | false   |
      | 2        | true    |
    Then the response status is 400
    And the problem detail says the item in position 3 is missing

  @US20
  Scenario: A checklist sent without the X-User-Id header is rejected
    Given an anonymous caller has opened the current checklist
    When the resident submits the checklist for building "7a9b3c1d-2e4f-4b6a-8c0d-1e2f3a4b5c6d" answering:
      | position | checked |
      | 1        | false   |
      | 2        | false   |
      | 3        | false   |
    Then the response status is 400
    And the problem detail is "X-User-Id header is required"
