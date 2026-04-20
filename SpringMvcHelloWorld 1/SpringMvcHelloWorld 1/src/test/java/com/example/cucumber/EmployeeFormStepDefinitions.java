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
 * Cucumber step definitions for the Employee registration form workflow.
 *
 * These steps simulate the client-side workflow of filling in the Employee form
 * and submitting it to the REST API (POST /api/employees), similar to how
 * Postman would send requests. The application must be running on localhost:8080
 * for these tests to work.
 *
 * To run with Postman instead:
 * 1. Import the Cucumber scenarios as a Postman collection
 * 2. Use the same JSON payloads shown in each step
 * 3. Send POST requests to http://localhost:8080/SpringMvcHelloWorld/api/employees
 */
public class EmployeeFormStepDefinitions {

    private static final String BASE_URL = "http://localhost:8080/SpringMvcHelloWorld";
        private static final String TEST_USERNAME = "hritika.singh";   
        private static final String TEST_PASSWORD = "12345678";         
        private static final String TEST_EMAIL = "hritika@example.com"; 

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;
    private HttpResponse<String> lastResponse;
    private Map<String, Object> lastResponseBody;

    @Before
    public void setUp() throws IOException, InterruptedException {
        if (jwtToken != null) return;

        // Step 1: Sign up a test user (ignore errors if user already exists)
        String signupPayload = String.format(
                "username=%s&password=%s&email=%s", TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        HttpRequest signupRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(signupPayload))
                .build();
        httpClient.send(signupRequest, HttpResponse.BodyHandlers.ofString());

        // Step 2: Login via the API to get a JWT token
        String loginPayload = objectMapper.writeValueAsString(
                Map.of("username", TEST_USERNAME, "password", TEST_PASSWORD));
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginPayload))
                .build();
        HttpResponse<String> loginResponse = httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());

        @SuppressWarnings("unchecked")
        Map<String, Object> loginBody = objectMapper.readValue(loginResponse.body(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        jwtToken = (String) loginBody.get("token");
        assertNotNull(jwtToken, "Failed to obtain JWT token. Is the server running?");
    }

    @Given("the employee registration API is available")
    public void theEmployeeRegistrationApiIsAvailable() {
        assertNotNull(jwtToken, "JWT token should be available after setup");
    }

    @When("I fill in the employee form with the following data:")
    public void iFillInTheEmployeeFormWithTheFollowingData(DataTable dataTable) throws IOException, InterruptedException {
        Map<String, String> formData = dataTable.asMaps().get(0);

        // Use HashMap instead of Map.of() because values may be null (blank fields in feature table)
        java.util.HashMap<String, String> payload = new java.util.HashMap<>();
        payload.put("name", formData.getOrDefault("name", ""));
        payload.put("email", formData.getOrDefault("email", ""));
        payload.put("contactNumber", formData.getOrDefault("contactNumber", ""));
        payload.put("position", formData.getOrDefault("position", ""));

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/employees"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        lastResponseBody = objectMapper.readValue(lastResponse.body(),
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
    }

    @Then("the employee should be registered successfully")
    public void theEmployeeShouldBeRegisteredSuccessfully() {
        assertEquals(200, lastResponse.statusCode(),
                "Expected HTTP 200 OK but got " + lastResponse.statusCode() +
                ". Body: " + lastResponse.body());
        assertEquals("success", lastResponseBody.get("status"));
    }

    @Then("the registration should fail with validation errors")
    public void theRegistrationShouldFailWithValidationErrors() {
        assertEquals(400, lastResponse.statusCode(),
                "Expected HTTP 400 Bad Request but got " + lastResponse.statusCode());
        assertEquals("error", lastResponseBody.get("status"));
    }

    @And("the response should contain the employee name {string}")
    public void theResponseShouldContainTheEmployeeName(String expectedName) {
        @SuppressWarnings("unchecked")
        Map<String, Object> employee = (Map<String, Object>) lastResponseBody.get("employee");
        assertNotNull(employee, "Response should contain an 'employee' object");
        assertEquals(expectedName, employee.get("name"));
    }

    @And("the error response should contain a message for field {string}")
    public void theErrorResponseShouldContainAMessageForField(String fieldName) {
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) lastResponseBody.get("errors");
        assertNotNull(errors, "Response should contain an 'errors' map");
        assertTrue(errors.containsKey(fieldName),
                "Errors should include field '" + fieldName + "', but got: " + errors.keySet());
    }
}
