package com.example.gatling;

import java.util.Iterator;
import java.util.Map;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class EmployeeFormSimulation extends Simulation {

    private static final String[][] EMPLOYEE_DATA = {
            {"Abiral Khanal",  "abiral@example.com",  "1234567890", "Developer"},
            {"Jane Smith",     "jane@example.com",     "9876543210", "Manager"},
            {"Bob Johnson",    "bob@example.com",      "5551234567", "Designer"},
            {"Alice Williams", "alice@example.com",    "5559876543", "Tester"},
            {"Charlie Brown",  "charlie@example.com",  "5551112222", "DevOps"},
            {"Diana Prince",   "diana@example.com",    "5553334444", "Analyst"},
            {"Evan Davis",     "evan@example.com",     "5555556666", "Architect"},
            {"Fiona Green",    "fiona@example.com",    "5557778888", "Intern"},
            {"George Harris",  "george@example.com",   "5559990000", "Lead"},
            {"Hannah Lee",     "hannah@example.com",   "5551010101", "Support"},
    };

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080/SpringMvcHelloWorld")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    Iterator<Map<String, Object>> employeeFeeder = new Iterator<>() {
        private int index = 0;

        @Override public boolean hasNext() { return true; }

        @Override
        public Map<String, Object> next() {
            String[] row = EMPLOYEE_DATA[index % EMPLOYEE_DATA.length];
            index++;
            return Map.of(
                    "name",          row[0],
                    "email",         row[1],
                    "contactNumber", row[2],
                    "position",      row[3]
            );
        }
    };

    ScenarioBuilder registerEmployeeScenario = scenario("Register Employee")
            // Step 1: Login with existing user to get JWT token
            .exec(
                    http("Login to Get JWT")
                            .post("/api/auth/login")
                            .body(StringBody("{\"username\":\"hritika.singh\",\"password\":\"12345678\"}"))
                            .check(status().is(200))
                            .check(jsonPath("$.token").saveAs("jwtToken"))
            )
            // Step 2: Submit employee form with JWT auth
            .feed(employeeFeeder)
            .exec(
                    http("Submit Employee Form")
                            .post("/api/employees")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(StringBody(
                                    "{\"name\":\"#{name}\",\"email\":\"#{email}\"," +
                                    "\"contactNumber\":\"#{contactNumber}\",\"position\":\"#{position}\"}"
                            ))
                            .check(status().is(200))
                            .check(jsonPath("$.status").is("success"))
            );

    {
        setUp(
                registerEmployeeScenario.injectOpen(
                        rampUsers(10).during(10)
                )
        ).protocols(httpProtocol)
         .assertions(
                 global().responseTime().max().lt(5000),
                 global().successfulRequests().percent().gt(95.0)
         );
    }
}