import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Custom metrics
const employeeCreated = new Counter('employees_created');
const apiErrorRate = new Rate('api_errors');
const createDuration = new Trend('create_employee_duration', true);
const listDuration = new Trend('list_employees_duration', true);

// ─── Test configuration ───────────────────────────────────────────────
// 1000 API calls spread over 5 minutes
// Using a constant arrival rate so we hit exactly 1000 requests in 300s
// That's ~3.33 requests/second
export const options = {
    scenarios: {
        load_test: {
            executor: 'constant-arrival-rate',
            rate: 4,              // 4 iterations per second
            timeUnit: '1s',
            duration: '5m',       // run for 5 minutes (4 × 300 = 1200 iterations ≈ 1000+)
            preAllocatedVUs: 10,  // pre-allocate 10 virtual users
            maxVUs: 50,           // allow up to 50 if needed
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],   // 95% of requests under 2s
        http_req_failed: ['rate<0.05'],       // less than 5% failure rate
        api_errors: ['rate<0.10'],            // custom: less than 10% API-level errors
    },
};

const BASE_URL = 'http://localhost:8080/SpringMvcHelloWorld';
const HEADERS = { 'Content-Type': 'application/json' };

// ─── Setup: register + login once to get JWT token ────────────────────
export function setup() {
    // Step 1: Sign up a test user (ignore 400 if already exists)
    const signupPayload = JSON.stringify({
        username: 'k6testuser',
        password: 'k6testpass123',
        email: 'k6test@example.com',
    });

    const signupRes = http.post(`${BASE_URL}/api/auth/register`, signupPayload, {
        headers: HEADERS,
    });

    console.log(`Signup status: ${signupRes.status}`);

    // Step 2: Login to get JWT token
    const loginPayload = JSON.stringify({
        username: 'k6testuser',
        password: 'k6testpass123',
    });

    const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, {
        headers: HEADERS,
    });

    check(loginRes, {
        'login status is 200': (r) => r.status === 200,
        'login returns token': (r) => JSON.parse(r.body).token !== undefined,
    });

    if (loginRes.status !== 200) {
        console.error(`Login failed: ${loginRes.body}`);
        return { token: null };
    }

    const token = JSON.parse(loginRes.body).token;
    console.log('Login successful, JWT token obtained.');
    return { token };
}

// ─── Employee data pool ───────────────────────────────────────────────
const POSITIONS = ['Developer', 'Manager', 'Designer', 'Tester', 'DevOps', 'Analyst', 'Architect', 'Lead', 'Support', 'Intern'];

function randomEmployee() {
    const id = `${Date.now()}${Math.floor(Math.random() * 10000)}`;
    return {
        name: `K6 User ${id}`,
        email: `k6user${id}@example.com`,
        contactNumber: String(Math.floor(1000000000 + Math.random() * 9000000000)),
        position: POSITIONS[Math.floor(Math.random() * POSITIONS.length)],
    };
}

// ─── Main test function (runs per iteration) ──────────────────────────
export default function (data) {
    if (!data.token) {
        console.error('No JWT token available — skipping iteration');
        apiErrorRate.add(1);
        return;
    }

    const authHeaders = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${data.token}`,
    };

    // ── Test 1: Create a new employee (POST /api/employees) ──
    const employee = randomEmployee();
    const createRes = http.post(
        `${BASE_URL}/api/employees`,
        JSON.stringify(employee),
        { headers: authHeaders, tags: { name: 'CreateEmployee' } }
    );

    const createOk = check(createRes, {
        'create status is 200': (r) => r.status === 200,
        'create returns success': (r) => {
            try { return JSON.parse(r.body).status === 'success'; } catch (e) { return false; }
        },
    });

    createDuration.add(createRes.timings.duration);

    if (createOk) {
        employeeCreated.add(1);
        apiErrorRate.add(0);
    } else {
        apiErrorRate.add(1);
        console.warn(`Create failed [${createRes.status}]: ${createRes.body}`);
    }

    // ── Test 2: List all employees (GET /api/employees) ──
    const listRes = http.get(`${BASE_URL}/api/employees`, {
        headers: authHeaders,
        tags: { name: 'ListEmployees' },
    });

    check(listRes, {
        'list status is 200': (r) => r.status === 200,
        'list returns array': (r) => {
            try { return Array.isArray(JSON.parse(r.body).employees); } catch (e) { return false; }
        },
    });

    listDuration.add(listRes.timings.duration);

    // ── Test 3: Get employee by ID (if create succeeded) ──
    if (createOk) {
        try {
            const createdId = JSON.parse(createRes.body).employee.id;
            const getRes = http.get(`${BASE_URL}/api/employees/${createdId}`, {
                headers: authHeaders,
                tags: { name: 'GetEmployee' },
            });

            check(getRes, {
                'get-by-id status is 200': (r) => r.status === 200,
                'get-by-id returns correct employee': (r) => {
                    try { return JSON.parse(r.body).employee.id === createdId; } catch (e) { return false; }
                },
            });
        } catch (e) {
            // employee ID not in response — skip
        }
    }

    // Small pause between iterations to simulate realistic traffic
    sleep(0.5);
}

// ─── Teardown: print summary ──────────────────────────────────────────
export function teardown(data) {
    console.log('========================================');
    console.log('  K6 Load Test Complete');
    console.log('========================================');
}
