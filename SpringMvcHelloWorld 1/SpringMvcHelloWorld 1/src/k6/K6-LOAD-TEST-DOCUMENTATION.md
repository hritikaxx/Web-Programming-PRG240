# K6 Load Test Documentation — Employee API

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Test Architecture](#test-architecture)
4. [Test Configuration](#test-configuration)
5. [API Endpoints Tested](#api-endpoints-tested)
6. [Test Script Breakdown](#test-script-breakdown)
7. [How to Run the Test](#how-to-run-the-test)
8. [Monitoring System Resources](#monitoring-system-resources)
9. [Understanding the Results](#understanding-the-results)
10. [Thresholds and Pass/Fail Criteria](#thresholds-and-passfail-criteria)
11. [Custom Metrics](#custom-metrics)
12. [Troubleshooting](#troubleshooting)

---

## Overview

This K6 load test evaluates the performance and reliability of the **Employee REST API** in the SpringMvcHelloWorld application. The test simulates realistic traffic by sending **1000+ API calls over 5 minutes**, targeting the core CRUD endpoints for employee management.

**Tool:** [K6](https://k6.io/) — an open-source load testing tool built for developer experience and performance. K6 scripts are written in JavaScript (ES6).

**Test file location:** `src/test/k6/employee-api-load-test.js`

---

## Prerequisites

### 1. Install K6

**macOS (Homebrew):**
```bash
brew install k6
```

**Windows (Chocolatey):**
```bash
choco install k6
```

**Linux (Debian/Ubuntu):**
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
    --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D68
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" \
    | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update && sudo apt-get install k6
```

**Verify installation:**
```bash
k6 version
```

### 2. Start the Application

The Spring MVC application must be running on `http://localhost:8080/SpringMvcHelloWorld` before executing the test.

```bash
# Option 1: Start via the embedded Tomcat in the project
cd "SpringMvcHelloWorld  Week 3"
./apache-tomcat-10.1.52/bin/startup.sh

# Option 2: Deploy WAR to Tomcat
mvn clean package
cp target/SpringMvcHelloWorld.war apache-tomcat-10.1.52/webapps/
./apache-tomcat-10.1.52/bin/startup.sh
```

### 3. Verify the Application is Running

```bash
curl http://localhost:8080/SpringMvcHelloWorld/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

You should get a JSON response (200 or 401), not a connection error.

---

## Test Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        K6 Load Test                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────┐    Runs once before all iterations                  │
│  │  SETUP  │──► Register test user (POST /api/auth/register)     │
│  │         │──► Login & get JWT token (POST /api/auth/login)     │
│  └────┬────┘                                                     │
│       │ JWT token passed to all virtual users                    │
│       ▼                                                          │
│  ┌──────────────────────────────────────────────────────────┐    │
│  │  DEFAULT FUNCTION (runs per iteration, 4 times/second)   │    │
│  │                                                          │    │
│  │  Test 1: POST /api/employees     (Create employee)       │    │
│  │  Test 2: GET  /api/employees     (List all employees)    │    │
│  │  Test 3: GET  /api/employees/:id (Get by ID)             │    │
│  │                                                          │    │
│  │  × 1200 iterations over 5 minutes                        │    │
│  │  × 10-50 concurrent virtual users                        │    │
│  └──────────────────────────────────────────────────────────┘    │
│       │                                                          │
│       ▼                                                          │
│  ┌──────────┐    Runs once after all iterations                  │
│  │ TEARDOWN │──► Print completion message                        │
│  └──────────┘                                                    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Test Configuration

```javascript
export const options = {
    scenarios: {
        load_test: {
            executor: 'constant-arrival-rate',
            rate: 4,              // 4 iterations per second
            timeUnit: '1s',
            duration: '5m',       // 5 minutes
            preAllocatedVUs: 10,  // start with 10 virtual users
            maxVUs: 50,           // scale up to 50 if needed
        },
    },
};
```

| Parameter         | Value  | Explanation                                                     |
|-------------------|--------|-----------------------------------------------------------------|
| `executor`        | `constant-arrival-rate` | Ensures a fixed number of requests per second regardless of response time |
| `rate`            | `4`    | 4 iterations (function executions) per second                   |
| `timeUnit`        | `1s`   | Rate is measured per second                                     |
| `duration`        | `5m`   | Test runs for 5 minutes (300 seconds)                           |
| `preAllocatedVUs` | `10`   | 10 virtual users are created at start                           |
| `maxVUs`          | `50`   | Up to 50 virtual users if the pre-allocated ones can't keep up  |

**Total iterations:** 4 iterations/sec × 300 sec = **1,200 iterations** (exceeds the 1000 requirement)

**Total HTTP requests:** Each iteration makes 3 API calls (create + list + get-by-id) = **~3,600 HTTP requests**

### Why `constant-arrival-rate`?

Unlike `ramping-vus` or `shared-iterations`, this executor guarantees a **fixed request rate**. If one request is slow, K6 spins up more virtual users to maintain the rate. This simulates real-world traffic more accurately — real users don't slow down just because your server is slow.

---

## API Endpoints Tested

### 1. POST /api/auth/register (Setup only)

**Purpose:** Create a test user account for authentication.

| Field      | Value              |
|------------|--------------------|
| `username` | `k6testuser`       |
| `password` | `k6testpass123`    |
| `email`    | `k6test@example.com` |

**Expected response:** `200 OK` (first run) or `400 Bad Request` (user already exists — acceptable).

---

### 2. POST /api/auth/login (Setup only)

**Purpose:** Authenticate and obtain a JWT token.

**Request body:**
```json
{
    "username": "k6testuser",
    "password": "k6testpass123"
}
```

**Expected response:**
```json
{
    "status": "success",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

The JWT token is extracted and passed to all virtual users via K6's `setup()` return value.

---

### 3. POST /api/employees (Test 1 — Create Employee)

**Purpose:** Create a new employee record. Each iteration generates unique random data.

**Request body example:**
```json
{
    "name": "K6 User 17137284561234",
    "email": "k6user17137284561234@example.com",
    "contactNumber": "5551234567",
    "position": "Developer"
}
```

**Validation rules on server:**
- `name` — required, not blank
- `email` — required, valid email format
- `contactNumber` — required, exactly 10 digits
- `position` — required, not blank

**Expected response:**
```json
{
    "status": "success",
    "message": "Employee registered successfully",
    "employee": { "id": 42, "name": "...", ... }
}
```

**Checks performed:**
- HTTP status is 200
- Response JSON `status` field equals `"success"`

---

### 4. GET /api/employees (Test 2 — List Employees)

**Purpose:** Retrieve all employee records from the database.

**Expected response:**
```json
{
    "status": "success",
    "count": 150,
    "employees": [ { "id": 1, ... }, { "id": 2, ... } ]
}
```

**Checks performed:**
- HTTP status is 200
- Response contains an `employees` array

---

### 5. GET /api/employees/{id} (Test 3 — Get by ID)

**Purpose:** Retrieve the specific employee just created in Test 1.

**Expected response:**
```json
{
    "status": "success",
    "employee": { "id": 42, "name": "K6 User ...", ... }
}
```

**Checks performed:**
- HTTP status is 200
- Response `employee.id` matches the ID from the create response

---

## Test Script Breakdown

### Imports (Lines 1-3)

```javascript
import http from 'k6/http';         // HTTP client for making requests
import { check, sleep } from 'k6';  // Assertions and delays
import { Counter, Rate, Trend } from 'k6/metrics';  // Custom metrics
```

### Custom Metrics (Lines 5-9)

```javascript
const employeeCreated = new Counter('employees_created');  // Total successful creates
const apiErrorRate = new Rate('api_errors');                // % of API-level errors
const createDuration = new Trend('create_employee_duration', true);  // Create timing
const listDuration = new Trend('list_employees_duration', true);     // List timing
```

| Metric Type | Name                       | What It Tracks                          |
|-------------|----------------------------|-----------------------------------------|
| `Counter`   | `employees_created`        | Total number of successfully created employees |
| `Rate`      | `api_errors`               | Percentage of iterations with API errors |
| `Trend`     | `create_employee_duration` | Response time distribution for POST /api/employees |
| `Trend`     | `list_employees_duration`  | Response time distribution for GET /api/employees |

### setup() — One-Time Authentication (Lines 37-74)

Runs **once** before any virtual user starts. It:
1. Registers a test user (tolerates 400 if user already exists)
2. Logs in to get a JWT token
3. Returns `{ token }` which is passed to every iteration of the default function

### randomEmployee() — Data Generation (Lines 79-87)

Generates unique employee data per iteration using `Date.now()` + random number to avoid duplicate email/name conflicts. The `contactNumber` is always exactly 10 digits to pass server-side validation.

### default function — Main Test Loop (Lines 90-164)

Runs **1200 times** across all virtual users. Each execution:
1. Creates an employee (POST)
2. Lists all employees (GET)
3. Fetches the created employee by ID (GET)
4. Sleeps 0.5 seconds to simulate realistic spacing

### teardown() — Cleanup (Lines 167-171)

Runs **once** after all iterations complete. Prints a completion message.

---

## How to Run the Test

### Basic Run

```bash
cd "SpringMvcHelloWorld  Week 3"
k6 run src/test/k6/employee-api-load-test.js
```

### Run with JSON Output (for analysis)

```bash
k6 run --out json=src/test/k6/results.json src/test/k6/employee-api-load-test.js
```

### Run with CSV Output

```bash
k6 run --out csv=src/test/k6/results.csv src/test/k6/employee-api-load-test.js
```

### Run with Custom Duration or Rate Override

```bash
# Shorter test for quick check
k6 run --duration 1m src/test/k6/employee-api-load-test.js

# Higher load
k6 run --env RATE=10 src/test/k6/employee-api-load-test.js
```

### Run with Web Dashboard (K6 built-in)

```bash
K6_WEB_DASHBOARD=true k6 run src/test/k6/employee-api-load-test.js
```

This opens a real-time browser dashboard at `http://localhost:5665` during the test.

---

## Monitoring System Resources

While the K6 test is running, open a **separate terminal** to monitor server resources.

### macOS

**CPU and Memory (real-time):**
```bash
top -l 0 -s 2 | grep -E "^(CPU|Phys|Load)"
```

**Detailed CPU/Memory per process:**
```bash
top -l 0 -s 2 -o cpu
```

**Memory pressure:**
```bash
vm_stat 1
```

**Network bandwidth:**
```bash
nettop -P -d
```

**Disk I/O:**
```bash
iostat -w 2
```

**All-in-one monitoring (if htop is installed):**
```bash
brew install htop
htop
```

### Linux

**CPU, Memory, and I/O:**
```bash
vmstat 2
```

**Per-process monitoring:**
```bash
top -d 2
```

**Network:**
```bash
iftop
# or
nethogs
```

**Comprehensive (if available):**
```bash
htop
# or
glances
```

### What to Watch For

| Resource           | Healthy Range       | Warning Sign                              |
|--------------------|---------------------|-------------------------------------------|
| **CPU Load**       | < 70% average       | Sustained 90%+ = CPU bottleneck           |
| **Memory Usage**   | < 80% of available  | Constant growth = possible memory leak    |
| **Network**        | Low latency, no drops| Packet loss, high retransmits             |
| **Disk I/O**       | Low wait times       | High iowait = database bottleneck         |
| **Java Heap**      | Sawtooth pattern     | Flat at max = GC pressure, OOM risk       |
| **DB Connections** | < pool max           | Pool exhaustion = connection timeout      |

### Monitoring the Java/Tomcat Process Specifically

```bash
# Find the Tomcat process ID
ps aux | grep tomcat | grep -v grep

# Monitor that specific PID (replace <PID>)
top -pid <PID> -l 0 -s 2

# JVM memory (if JDK tools available)
jstat -gc <PID> 2000
```

---

## Understanding the Results

After the test completes, K6 prints a detailed summary. Here's how to read it:

### Sample Output

```
          /\      |‾‾| /‾‾/   /‾‾/
     /\  /  \     |  |/  /   /  /
    /  \/    \    |     (   /   ‾‾\
   /          \   |  |\  \ |  (‾)  |
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: employee-api-load-test.js
     output: -

  scenarios: (100.00%) 1 scenario, 50 max VUs, 5m30s max duration
           * load_test: 4.00 iterations/s for 5m0s

     ✓ create status is 200
     ✓ create returns success
     ✓ list status is 200
     ✓ list returns array
     ✓ get-by-id status is 200
     ✓ get-by-id returns correct employee
     ✓ login status is 200
     ✓ login returns token

     checks.....................: 100.00% ✓ 7200  ✗ 0
     data_received..............: 15 MB   50 kB/s
     data_sent..................: 3.2 MB  11 kB/s
     http_req_blocked...........: avg=1.2ms  p(95)=3ms
     http_req_connecting........: avg=0.8ms  p(95)=2ms
   ✓ http_req_duration..........: avg=45ms   p(95)=120ms
   ✓ http_req_failed............: 0.00%  ✓ 0     ✗ 3600
     http_req_receiving.........: avg=0.5ms  p(95)=1ms
     http_req_sending...........: avg=0.2ms  p(95)=0.5ms
     http_req_waiting...........: avg=44ms   p(95)=118ms
     http_reqs..................: 3600   12/s
     iteration_duration.........: avg=680ms  p(95)=850ms
     iterations.................: 1200   4/s
```

### Key Metrics Explained

| Metric                 | What It Means                                                  |
|------------------------|----------------------------------------------------------------|
| `checks`               | Pass/fail rate of all `check()` assertions                     |
| `http_req_duration`    | Total time for each HTTP request (send + wait + receive)       |
| `http_req_failed`      | Percentage of requests that returned non-2xx status codes      |
| `http_reqs`            | Total number of HTTP requests made                             |
| `iterations`           | Total number of complete test iterations (default function calls) |
| `data_received`        | Total data downloaded from the server                          |
| `data_sent`            | Total data uploaded to the server                              |
| `http_req_blocked`     | Time spent waiting for a free TCP connection                   |
| `http_req_connecting`  | Time spent establishing TCP connection                         |
| `http_req_waiting`     | Time between sending the request and receiving the first byte (TTFB) |
| `vus`                  | Current number of active virtual users                         |

### Percentile Breakdown

- **avg** — Average response time (can be skewed by outliers)
- **p(90)** — 90% of requests completed within this time
- **p(95)** — 95% of requests completed within this time (used in thresholds)
- **max** — Slowest single request

**Rule of thumb:** Focus on **p(95)**, not average. If avg=50ms but p(95)=2000ms, many users are having a bad experience.

---

## Thresholds and Pass/Fail Criteria

```javascript
thresholds: {
    http_req_duration: ['p(95)<2000'],   // 95% of requests must complete under 2 seconds
    http_req_failed: ['rate<0.05'],       // Less than 5% HTTP-level failures
    api_errors: ['rate<0.10'],            // Less than 10% API-level errors
},
```

| Threshold            | Condition         | What Happens If Failed                        |
|----------------------|-------------------|-----------------------------------------------|
| `http_req_duration`  | p(95) < 2000ms    | K6 exits with non-zero code, test marked FAIL |
| `http_req_failed`    | rate < 5%         | K6 exits with non-zero code, test marked FAIL |
| `api_errors`         | rate < 10%        | K6 exits with non-zero code, test marked FAIL |

Failed thresholds are shown with `✗` in the output:
```
   ✗ http_req_duration..........: avg=2500ms  p(95)=4200ms
                                  { p(95)<2000 }
```

---

## Custom Metrics

These are application-specific metrics defined in the test, beyond K6's built-in HTTP metrics.

| Metric Name                | Type      | Description                                         |
|----------------------------|-----------|-----------------------------------------------------|
| `employees_created`        | Counter   | Total count of employees successfully created       |
| `api_errors`               | Rate      | Fraction of iterations where the API returned an error |
| `create_employee_duration` | Trend     | Response time distribution for `POST /api/employees` |
| `list_employees_duration`  | Trend     | Response time distribution for `GET /api/employees`  |

These appear in the K6 summary output alongside the built-in metrics.

---

## Troubleshooting

### "connection refused" errors

The application is not running. Start Tomcat:
```bash
./apache-tomcat-10.1.52/bin/startup.sh
```

### "Login failed" in setup

- Check that the `/api/auth/register` and `/api/auth/login` endpoints are deployed
- Verify the database is running and accessible
- Check Tomcat logs: `apache-tomcat-10.1.52/logs/catalina.out`

### All creates return 400

- The server-side validation is rejecting the employee data
- Check that `contactNumber` is exactly 10 digits
- Check that `email` format is valid
- Check Tomcat logs for validation error details

### High error rate / threshold failures

- **CPU saturated:** Reduce the rate (e.g., `rate: 2`) or increase server resources
- **Database slow:** Check connection pool size and database query performance
- **Memory pressure:** Monitor JVM heap with `jstat -gc <PID> 2000`
- **Network:** Check for packet loss with `ping localhost`

### "dropped iterations" warning

```
WARN[0010] Insufficient VUs, reached 50 active VUs and cannot initialize more
```

K6 can't keep up with the requested rate. Either:
- Increase `maxVUs` in the test config
- Reduce `rate`
- The server is too slow — this itself is a test finding

### Clean up test data after running

```sql
-- Connect to your database and remove K6 test data
DELETE FROM employees WHERE name LIKE 'K6 User%';
```
