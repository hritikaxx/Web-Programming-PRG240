package com.example.gatling;

import java.util.Iterator;
import java.util.Map;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Gatling performance simulation for User CRUD operations.
 *
 * Tests Create, Read, Update, and Delete operations
 * for the User API under load with 10 concurrent users.
 *
 * Prerequisites: Application must be running on localhost:8080.
 *
 * Run with:
 * mvn clean gatling:test "-Dgatling.simulationClass=com.example.gatling.UserCrudSimulation"
 */
public class UserCrudSimulation extends Simulation {

    // Predefined user data — each virtual user picks the next row
    private static final String[][] USER_DATA = {
            {"Alice Johnson",   "alice@example.com",   "alicejohn",   "pass1234"},
            {"Bob Williams",    "bob@example.com",     "bobwill",     "pass1234"},
            {"Carol Martinez",  "carol@example.com",   "carolmart",   "pass1234"},
            {"David Brown",     "david@example.com",   "davidbrown",  "pass1234"},
            {"Eva Davis",       "eva@example.com",     "evadavis",    "pass1234"},
            {"Frank Wilson",    "frank@example.com",   "frankwils",   "pass1234"},
            {"Grace Taylor",    "grace@example.com",   "gracetay",    "pass1234"},
            {"Henry Anderson",  "henry@example.com",   "henryan",     "pass1234"},
            {"Isla Thomas",     "isla@example.com",    "islathom",    "pass1234"},
            {"Jack Jackson",    "jack@example.com",    "jackjack",    "pass1234"},
    };

    // HTTP protocol configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080/SpringMvcHelloWorld")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Build a feeder (data source) from the predefined array
    Iterator<Map<String, Object>> userFeeder = new Iterator<>() {
        private int index = 0;

        @Override public boolean hasNext() { return true; }

        @Override
        public Map<String, Object> next() {
            String[] row = USER_DATA[index % USER_DATA.length];
            index++;
            return Map.of(
                    "fullName", row[0],
                    "email",    row[1],
                    "username", row[2],
                    "password", row[3]
            );
        }
    };

    // Scenario: login, then perform full CRUD on users
    ScenarioBuilder userCrudScenario = scenario("User CRUD Operations")

            // Step 1: Login with existing user to get JWT token
            .exec(
                    http("Login to Get JWT")
                            .post("/api/auth/login")
                            .body(StringBody("{\"username\":\"hritika.singh\",\"password\":\"12345678\"}"))
                            .check(status().is(200))
                            .check(jsonPath("$.token").saveAs("jwtToken"))
            )

            // Step 2: CREATE — register a new user
            .feed(userFeeder)
            .exec(
                    http("Create User")
                            .post("/api/users")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(StringBody(
                                    "{\"fullName\":\"#{fullName}\"," +
                                    "\"email\":\"#{email}\"," +
                                    "\"username\":\"#{username}\"," +
                                    "\"password\":\"#{password}\"}"
                            ))
                            .check(status().is(200))
                            .check(jsonPath("$.user.id").saveAs("createdUserId"))
            )

            // Step 3: READ ALL — get all users
            .exec(
                    http("Get All Users")
                            .get("/api/users")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.users").exists())
            )

            // Step 4: READ ONE — get the created user by ID
            .exec(
                    http("Get User by ID")
                            .get("/api/users/#{createdUserId}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.user.id").exists())
            )

            // Step 5: UPDATE — update the created user
            .exec(
                    http("Update User")
                            .put("/api/users/#{createdUserId}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(StringBody(
                                    "{\"fullName\":\"Updated #{fullName}\"," +
                                    "\"email\":\"#{email}\"," +
                                    "\"username\":\"#{username}\"," +
                                    "\"password\":\"newpass123\"}"
                            ))
                            .check(status().is(200))
                            .check(jsonPath("$.user.id").exists())
            )

            // Step 6: DELETE — delete the created user
            .exec(
                    http("Delete User")
                            .delete("/api/users/#{createdUserId}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .check(status().is(200))
                            .check(jsonPath("$.status").is("success"))
            );

    // Load profile: ramp up 10 users over 10 seconds
    {
        setUp(
                userCrudScenario.injectOpen(
                        rampUsers(10).during(10)
                )
        ).protocols(httpProtocol)
         .assertions(
                 global().responseTime().max().lt(5000),
                 global().successfulRequests().percent().gt(95.0)
         );
    }
}