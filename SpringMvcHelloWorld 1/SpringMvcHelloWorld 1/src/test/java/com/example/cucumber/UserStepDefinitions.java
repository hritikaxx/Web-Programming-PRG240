package com.example.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for User CRUD operations.
 *
 * Covers Create, Read, Update, and Delete operations
 * for the User API at /api/users.
 *
 * Prerequisites: Application must be running on localhost:8080.
 */
public class UserStepDefinitions {

    private static final String BASE_URL = "http://localhost:8080/SpringMvcHelloWorld";
    private static final String ADMIN_USERNAME = "hritika.singh";
    private static final String ADMIN_PASSWORD = "12345678";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;
    private HttpResponse<String> lastResponse;
    private Map<String, Object> lastResponseBody;
    private Integer lastCreatedUserId;

    // ── Setup ──────────────────────────────────────────────────────────────────

    @Before
    public void setUp() throws IOException, InterruptedException {
        if (jwtToken != null) return;

        String loginPayload = objectMapper.writeValueAsString(
                Map.of("username", ADMIN_USERNAME, "password", ADMIN_PASSWORD));

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(
                loginRequest, HttpResponse.BodyHandlers.ofString());

        @SuppressWarnings("unchecked")
        Map<String, Object> loginBody = objectMapper.readValue(
                loginResponse.body(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

        jwtToken = (String) loginBody.get("token");
        assertNotNull(jwtToken, "Failed to obtain JWT token. Is the server running?");
    }

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the user management API is available")
    public void theUserManagementApiIsAvailable() {
        assertNotNull(jwtToken, "JWT token should be available after setup");
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @When("I send a create user request with the following data:")
    public void iSendACreateUserRequestWithTheFollowingData(DataTable dataTable)
            throws IOException, InterruptedException {

        Map<String, String> formData = dataTable.asMaps().get(0);

        java.util.HashMap<String, String> payload = new java.util.HashMap<>();
        payload.put("fullName", formData.getOrDefault("fullName", ""));
        payload.put("email",    formData.getOrDefault("email", ""));
        payload.put("username", formData.getOrDefault("username", ""));
        payload.put("password", formData.getOrDefault("password", ""));

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (lastResponse.body() != null && !lastResponse.body().isEmpty()) {
            lastResponseBody = objectMapper.readValue(
                    lastResponse.body(),
                    objectMapper.getTypeFactory().constructMapType(
                            Map.class, String.class, Object.class));

            // Save created user ID for delete scenario
            if (lastResponseBody.containsKey("user")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> user = (Map<String, Object>) lastResponseBody.get("user");
                if (user != null && user.containsKey("id")) {
                    lastCreatedUserId = (Integer) user.get("id");
                }
            }
        }
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 but got " + lastResponse.statusCode()
                + ". Body: " + lastResponse.body());
        assertNotNull(lastResponseBody.get("user"),
                "Response should contain a 'user' object");
    }

    @Then("the create response should contain the username {string}")
    public void theCreateResponseShouldContainTheUsername(String expectedUsername) {
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) lastResponseBody.get("user");
        assertNotNull(user, "Response should contain a 'user' object");
        assertEquals(expectedUsername, user.get("username"),
                "Expected username '" + expectedUsername + "' but got: " + user.get("username"));
    }

    @Then("the user creation should fail with an error")
    public void theUserCreationShouldFailWithAnError() {
        assertTrue(lastResponse.statusCode() >= 400,
                "Expected HTTP 4xx error but got " + lastResponse.statusCode());
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @When("I request all users")
    public void iRequestAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users"))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        lastResponseBody = objectMapper.readValue(
                lastResponse.body(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class, String.class, Object.class));
    }

    @Then("I should receive a list of users")
    public void iShouldReceiveAListOfUsers() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 but got " + lastResponse.statusCode());
        assertTrue(lastResponseBody.containsKey("users"),
                "Response should contain a 'users' list");
    }

    @When("I request the user with ID {int}")
    public void iRequestTheUserWithId(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/" + userId))
                .header("Authorization", "Bearer " + jwtToken)
                .GET()
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        lastResponseBody = objectMapper.readValue(
                lastResponse.body(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class, String.class, Object.class));
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 but got " + lastResponse.statusCode());
        assertNotNull(lastResponseBody.get("user"),
                "Response should contain a 'user' object");
    }

    @And("the user details should contain the username {string}")
    public void theUserDetailsShouldContainTheUsername(String expectedUsername) {
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) lastResponseBody.get("user");
        assertNotNull(user, "Response should contain a 'user' object");
        assertEquals(expectedUsername, user.get("username"),
                "Expected username '" + expectedUsername + "' but got: " + user.get("username"));
    }

    @Then("the user retrieval should fail with not found error")
    public void theUserRetrievalShouldFailWithNotFoundError() {
        assertTrue(lastResponse.statusCode() >= 400,
                "Expected HTTP 4xx error but got " + lastResponse.statusCode());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @When("I update the user with ID {int} with the following data:")
    public void iUpdateTheUserWithIdWithTheFollowingData(int userId, DataTable dataTable)
            throws IOException, InterruptedException {

        Map<String, String> formData = dataTable.asMaps().get(0);

        java.util.HashMap<String, String> payload = new java.util.HashMap<>();
        payload.put("fullName", formData.getOrDefault("fullName", ""));
        payload.put("email",    formData.getOrDefault("email", ""));
        payload.put("username", formData.getOrDefault("username", ""));
        payload.put("password", formData.getOrDefault("password", ""));

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/" + userId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        lastResponseBody = objectMapper.readValue(
                lastResponse.body(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class, String.class, Object.class));
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 but got " + lastResponse.statusCode()
                + ". Body: " + lastResponse.body());
        assertNotNull(lastResponseBody.get("user"),
                "Response should contain a 'user' object");
    }

    @And("the update response should contain the fullName {string}")
    public void theUpdateResponseShouldContainTheFullName(String expectedFullName) {
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) lastResponseBody.get("user");
        assertNotNull(user, "Response should contain a 'user' object");
        assertEquals(expectedFullName, user.get("fullName"),
                "Expected fullName '" + expectedFullName + "' but got: " + user.get("fullName"));
    }

    @Then("the user update should fail with an error")
    public void theUserUpdateShouldFailWithAnError() {
        assertTrue(lastResponse.statusCode() >= 400,
                "Expected HTTP 4xx error but got " + lastResponse.statusCode());
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @When("I delete the user that was just created")
    public void iDeleteTheUserThatWasJustCreated() throws IOException, InterruptedException {
        assertNotNull(lastCreatedUserId, "No user was created in a previous step");
        performDelete(lastCreatedUserId);
    }

    @When("I delete the user with ID {int}")
    public void iDeleteTheUserWithId(int userId) throws IOException, InterruptedException {
        performDelete(userId);
    }

    private void performDelete(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/users/" + userId))
                .header("Authorization", "Bearer " + jwtToken)
                .DELETE()
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        lastResponseBody = objectMapper.readValue(
                lastResponse.body(),
                objectMapper.getTypeFactory().constructMapType(
                        Map.class, String.class, Object.class));
    }

    @Then("the user should be deleted successfully")
    public void theUserShouldBeDeletedSuccessfully() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 but got " + lastResponse.statusCode());
        assertEquals("success", lastResponseBody.get("status"),
                "Expected status 'success' but got: " + lastResponseBody.get("status"));
    }

    @Then("the user deletion should fail with an error")
    public void theUserDeletionShouldFailWithAnError() {
        assertTrue(lastResponse.statusCode() >= 400,
                "Expected HTTP 4xx error but got " + lastResponse.statusCode());
    }
}