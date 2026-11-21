package com.rumi.safety.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Every step goes through the REST API, as the gateway would call it.
 */
public class ResidentSafetyStepDefinitions {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    private MvcResult result;
    private JsonNode body;
    private String residentUserId;
    private JsonNode template;
    private final Map<Integer, String> itemIdByPosition = new HashMap<>();

    @Given("the service runs with the dev sample data")
    public void theServiceRunsWithTheDevSampleData() throws Exception {
        perform(get("/api/v1/evacuation-plans/health"));
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @When("the resident requests the evacuation plan of building {string}")
    public void theResidentRequestsTheEvacuationPlanOfBuilding(String buildingId) throws Exception {
        perform(get("/api/v1/evacuation-plans").param("buildingId", buildingId));
    }

    @When("the resident requests the current checklist template")
    public void theResidentRequestsTheCurrentChecklistTemplate() throws Exception {
        perform(get("/api/v1/checklists/templates/current"));
    }

    @Given("the resident {string} has opened the current checklist")
    public void theResidentHasOpenedTheCurrentChecklist(String userId) throws Exception {
        residentUserId = userId;
        openTheCurrentChecklist();
    }

    @Given("an anonymous caller has opened the current checklist")
    public void anAnonymousCallerHasOpenedTheCurrentChecklist() throws Exception {
        residentUserId = null;
        openTheCurrentChecklist();
    }

    @When("the resident submits the checklist for building {string} answering:")
    public void theResidentSubmitsTheChecklistAnswering(String buildingId, DataTable answers) throws Exception {
        ObjectNode request = objectMapper.createObjectNode()
                .put("templateId", template.get("id").asText())
                .put("buildingId", buildingId);
        ArrayNode answerNodes = request.putArray("answers");
        for (Map<String, String> row : answers.asMaps()) {
            answerNodes.addObject()
                    .put("itemId", itemIdByPosition.get(Integer.parseInt(row.get("position"))))
                    .put("checked", Boolean.parseBoolean(row.get("checked")));
        }
        MockHttpServletRequestBuilder submit = post("/api/v1/checklists/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        if (residentUserId != null) {
            submit.header("X-User-Id", residentUserId);
        }
        perform(submit);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int status) {
        assertThat(result.getResponse().getStatus()).isEqualTo(status);
    }

    @And("the plan has {int} routes and {int} safe points")
    public void thePlanHasRoutesAndSafePoints(int routes, int safePoints) {
        assertThat(body.get("routes")).hasSize(routes);
        assertThat(body.get("safePoints")).hasSize(safePoints);
    }

    @And("the plan includes the route {string}")
    public void thePlanIncludesTheRoute(String description) {
        assertThat(texts(body.get("routes"), "description")).contains(description);
    }

    @And("the plan includes the safe point {string} at latitude {double} and longitude {double}")
    public void thePlanIncludesTheSafePoint(String name, double latitude, double longitude) {
        JsonNode safePoint = StreamSupport.stream(body.get("safePoints").spliterator(), false)
                .filter(node -> node.get("name").asText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no safe point named " + name));
        assertThat(safePoint.at("/location/latitude").asDouble()).isEqualTo(latitude);
        assertThat(safePoint.at("/location/longitude").asDouble()).isEqualTo(longitude);
    }

    @And("the problem detail is {string}")
    public void theProblemDetailIs(String detail) {
        assertThat(result.getResponse().getContentType()).startsWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(body.get("detail").asText()).isEqualTo(detail);
    }

    @And("the problem detail says the item in position {int} is missing")
    public void theProblemDetailSaysTheItemIsMissing(int position) {
        assertThat(body.get("detail").asText())
                .isEqualTo("answers must cover every item of the template; missing items: "
                        + itemIdByPosition.get(position));
    }

    @And("the checklist is {string} version {int}")
    public void theChecklistIs(String name, int version) {
        assertThat(body.get("name").asText()).isEqualTo(name);
        assertThat(body.get("version").asInt()).isEqualTo(version);
    }

    @And("the checklist items are, in order:")
    public void theChecklistItemsAreInOrder(DataTable expected) {
        List<List<String>> actual = StreamSupport.stream(body.get("items").spliterator(), false)
                .map(item -> List.of(item.get("position").asText(), item.get("label").asText()))
                .toList();
        assertThat(actual).isEqualTo(expected.cells().subList(1, expected.height()));
    }

    @And("the saved response belongs to resident {string}")
    public void theSavedResponseBelongsToResident(String userId) {
        assertThat(body.get("id").asText()).isNotBlank();
        assertThat(body.get("submittedAt").asText()).isNotBlank();
        assertThat(body.get("residentUserId").asText()).isEqualTo(userId);
        assertThat(body.get("templateId").asText()).isEqualTo(template.get("id").asText());
    }

    @And("the saved answers are, in order:")
    public void theSavedAnswersAreInOrder(DataTable expected) {
        List<List<String>> actual = StreamSupport.stream(body.get("answers").spliterator(), false)
                .map(answer -> List.of(
                        positionOf(answer.get("itemId").asText()),
                        answer.get("checked").asText()))
                .toList();
        assertThat(actual).isEqualTo(expected.cells().subList(1, expected.height()));
    }

    private void openTheCurrentChecklist() throws Exception {
        perform(get("/api/v1/checklists/templates/current"));
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        template = body;
        itemIdByPosition.clear();
        template.get("items").forEach(item ->
                itemIdByPosition.put(item.get("position").asInt(), item.get("id").asText()));
    }

    private String positionOf(String itemId) {
        return itemIdByPosition.entrySet().stream()
                .filter(entry -> entry.getValue().equals(itemId))
                .map(entry -> String.valueOf(entry.getKey()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("item " + itemId + " is not in the template"));
    }

    private void perform(MockHttpServletRequestBuilder request) throws Exception {
        result = mockMvc.perform(request).andReturn();
        String content = result.getResponse().getContentAsString();
        body = content.isEmpty() ? objectMapper.createObjectNode() : objectMapper.readTree(content);
    }

    private static List<String> texts(JsonNode array, String field) {
        return StreamSupport.stream(array.spliterator(), false).map(node -> node.get(field).asText()).toList();
    }
}
