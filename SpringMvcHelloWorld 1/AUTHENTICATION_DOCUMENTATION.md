# User Authentication & Session Management — Documentation

## Table of Contents
1. [Overview — What Was Changed and Why](#overview)
2. [The Problem (Before)](#the-problem-before)
3. [The Solution (After)](#the-solution-after)
4. [Complete User Flow](#complete-user-flow)
5. [New Files Created](#new-files-created)
   - [UserDTO.java](#1-userdtojava)
   - [UserDAO.java (Interface)](#2-userdaojava-interface)
   - [UserDAOImpl.java](#3-userdaoimpljava)
   - [UserService.java](#4-userservicejava)
   - [UserController.java](#5-usercontrollerjava)
   - [SessionAuthInterceptor.java](#6-sessionauthinterceptorjava)
   - [login.jsp](#7-loginjsp)
   - [signup.jsp](#8-signupjsp)
6. [Modified Files](#modified-files)
   - [dispatcher-servlet.xml](#1-dispatcher-servletxml)
   - [AuthController.java](#2-authcontrollerjava)
   - [HelloController.java](#3-hellocontrollerjava)
   - [employeeForm.jsp](#4-employeeformjsp)
   - [employeeSummary.jsp](#5-employeesummaryjsp)
7. [Two Authentication Systems Explained](#two-authentication-systems-explained)
8. [Password Hashing Explained](#password-hashing-explained)
9. [Session vs JWT — Why Both?](#session-vs-jwt--why-both)
10. [Database Changes](#database-changes)
11. [Architecture Diagram](#architecture-diagram)

---

## Overview

User authentication was added to the application so that **only registered and logged-in users can access the employee management features**. Previously, anyone could visit `/register` and add employees. Now the flow is:

**Sign Up → Log In → Add Employees → Log Out**

---

## The Problem (Before)

| Issue | Details |
|-------|---------|
| **No user registration** | There was no way for users to create accounts |
| **Hardcoded credentials** | `AuthController` only accepted `admin` / `password123` |
| **Unprotected UI pages** | Anyone could visit `/register` and add employees without logging in |
| **API-only auth** | JWT authentication only protected `/api/*` endpoints, not the JSP pages |
| **Single user** | Only one person (admin) could ever use the system |

### Before — What Happened When You Visited `/register`:
```
Browser → GET /register → EmployeeController → employeeForm.jsp (NO CHECK!)
```
Anyone could access the employee form. No login required.

---

## The Solution (After)

| Feature | Implementation |
|---------|---------------|
| **User registration** | `/signup` page → stores users in H2 database |
| **User login** | `/login` page → validates credentials against database |
| **Password security** | Passwords hashed with SHA-256 before storage |
| **Session-based UI protection** | `SessionAuthInterceptor` blocks unauthenticated access to `/register` pages |
| **Logout** | `/logout` destroys the session and redirects to login |
| **Database-backed API auth** | `AuthController` now authenticates against the database (not hardcoded) |

### After — What Happens When You Visit `/register`:
```
Browser → GET /register
  → SessionAuthInterceptor checks session
  → No "loggedInUser" in session?
  → REDIRECT to /login
  → User logs in → session created → redirect to /register
  → SessionAuthInterceptor checks session → "loggedInUser" found → ALLOW
  → EmployeeController → employeeForm.jsp
```

---

## Complete User Flow

### Step 1: First Visit
```
User visits http://localhost:8080/
  → HelloController.home() redirects to /login    [CHANGED: was /hello before]
  → login.jsp is displayed
```

### Step 2: Sign Up (New User)
```
User clicks "Sign up" link on login page
  → GET /signup → UserController.showSignupPage() → signup.jsp
  → User fills in: username, email, password
  → POST /signup → UserController.signup()
    → Validates: fields not blank, password >= 6 chars
    → Calls UserService.registerUser()
      → Checks if username already exists (UserDAO.findByUsername)
      → Hashes password with SHA-256
      → Saves to database (UserDAO.save → INSERT INTO users)
    → Redirects to /login?registered=true
  → login.jsp shows "Account created successfully!"
```

### Step 3: Log In
```
User enters username and password
  → POST /login → UserController.login()
    → Calls UserService.authenticate()
      → Finds user by username (UserDAO.findByUsername)
      → Hashes entered password with SHA-256
      → Compares hash with stored hash
    → If match: session.setAttribute("loggedInUser", username)
    → Redirects to /register
```

### Step 4: Access Employee Form (Protected)
```
GET /register
  → SessionAuthInterceptor.preHandle() runs BEFORE the controller
  → Checks: session exists AND session has "loggedInUser"?
  → YES → request continues to EmployeeController.showRegistrationForm()
  → employeeForm.jsp shows:
    - "Logged in as <username>" header
    - "Logout" link
    - Employee registration form
```

### Step 5: Add Employee
```
User fills in employee details and submits
  → JavaScript fetch() sends POST to /api/register
  → (This is still JWT-protected for API clients, but the form works via session)
  → EmployeeController creates the employee
  → Redirects to /register/summary (also protected by SessionAuthInterceptor)
```

### Step 6: Logout
```
User clicks "Logout"
  → GET /logout → UserController.logout()
  → session.invalidate() — destroys the session
  → Redirects to /login?loggedOut=true
  → login.jsp shows "You have been logged out."
```

---

## New Files Created

### 1. UserDTO.java
**Path:** `src/main/java/com/example/dto/UserDTO.java`

**Why:** To have a Java object representing a user record in the database, following the same DTO pattern as `EmployeeDTO`.

**What it does:** Simple data container with fields that map to the `users` database table columns.

| Field | Type | Purpose |
|-------|------|---------|
| `id` | Long | Auto-generated primary key from database |
| `username` | String | Unique login name |
| `password` | String | SHA-256 hashed password (NEVER plain text) |
| `email` | String | User's email address |

**Constructors:**
- `UserDTO()` — No-arg, required by Spring/JDBC
- `UserDTO(username, password, email)` — For creating NEW users (no ID yet)
- `UserDTO(id, username, password, email)` — For users loaded FROM database (has ID)

---

### 2. UserDAO.java (Interface)
**Path:** `src/main/java/com/example/dao/UserDAO.java`

**Why:** Defines the contract for user database operations, following the same interface pattern as `EmployeeDAO`. The Service layer depends on this interface (not the implementation), which is a core principle of Spring's dependency injection.

**Methods:**

| Method | Purpose |
|--------|---------|
| `save(UserDTO)` | Insert a new user into the database |
| `findByUsername(String)` | Look up a user by username (used for login AND registration duplicate check) |

**Why only 2 methods (vs EmployeeDAO's 5)?** We only need registration and login for now. No need for `findAll()`, `update()`, or `delete()` until we build a user management admin panel.

---

### 3. UserDAOImpl.java
**Path:** `src/main/java/com/example/dao/UserDAOImpl.java`

**Why:** The actual implementation that runs SQL queries against the H2 database. Follows the exact same pattern as `EmployeeDAOImpl`.

**What it does on startup:**
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,   -- UNIQUE prevents duplicate usernames
    password VARCHAR(255) NOT NULL,           -- stores SHA-256 hash
    email VARCHAR(255) NOT NULL
)
```

**Key implementation details:**
- Uses `JdbcTemplate` for safe SQL execution (same as `EmployeeDAOImpl`)
- Uses `?` placeholders in SQL to prevent SQL injection attacks
- Uses `RowMapper<UserDTO>` to convert database rows into Java objects
- Uses `GeneratedKeyHolder` to capture the auto-generated ID after INSERT
- `UNIQUE` constraint on `username` column provides database-level duplicate prevention
- `@Repository` annotation tells Spring this is a database-access component

---

### 4. UserService.java
**Path:** `src/main/java/com/example/service/UserService.java`

**Why:** Contains the business logic for user registration and authentication. Sits between Controller and DAO, following the same pattern as `EmployeeService`.

**Methods:**

#### `registerUser(username, password, email)` → returns `boolean`
1. Calls `userDAO.findByUsername()` to check if username already exists
2. If exists → returns `false` (registration fails)
3. Hashes password with SHA-256 using `hashPassword()`
4. Creates `UserDTO` with username, hashed password, email
5. Calls `userDAO.save()` to insert into database
6. Returns `true` (registration succeeded)

#### `authenticate(username, password)` → returns `boolean`
1. Calls `userDAO.findByUsername()` to find the user
2. If user not found → returns `false`
3. Hashes the entered password with SHA-256
4. Compares with the stored hash from the database
5. If hashes match → returns `true` (correct password)
6. If hashes don't match → returns `false` (wrong password)

#### `hashPassword(password)` → returns `String` (private helper)
- Uses Java's built-in `MessageDigest` with SHA-256 algorithm
- Converts password string to bytes → hashes → converts hash to hex string
- Example: `"password123"` → `"ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"`

---

### 5. UserController.java
**Path:** `src/main/java/com/example/controller/UserController.java`

**Why:** Handles the login, signup, and logout web pages. This is a `@Controller` (returns JSP views), not a `@RestController` (which returns JSON).

**Endpoints:**

| HTTP Method | URL | What it does |
|-------------|-----|-------------|
| `GET /login` | Shows the login page (`login.jsp`) |
| `POST /login` | Processes login form submission. On success: stores username in session, redirects to `/register`. On failure: shows login page again with error message. |
| `GET /signup` | Shows the signup page (`signup.jsp`) |
| `POST /signup` | Processes signup form. Validates fields, calls `UserService.registerUser()`. On success: redirects to `/login?registered=true`. On failure: shows error. |
| `GET /logout` | Calls `session.invalidate()` to destroy the session, redirects to `/login?loggedOut=true` |

**How the session works:**
- `session.setAttribute("loggedInUser", username)` — stores the username in the HTTP session after successful login
- The session is a server-side storage tied to a cookie (`JSESSIONID`) in the browser
- `session.invalidate()` — destroys the session on logout (removes all stored data)
- `SessionAuthInterceptor` checks for this `"loggedInUser"` attribute on protected pages

---

### 6. SessionAuthInterceptor.java
**Path:** `src/main/java/com/example/security/SessionAuthInterceptor.java`

**Why:** The existing `JwtAuthInterceptor` only protects API endpoints (`/api/*`). The JSP pages (`/register`, `/register/summary`) had NO protection. This interceptor blocks unauthenticated users from accessing the employee management UI.

**How it works:**
```
Every request to /register or /register/**
  → preHandle() is called BEFORE the controller
  → Gets the HTTP session (without creating a new one)
  → Checks if "loggedInUser" attribute exists in session
  → If YES: return true (allow request to continue)
  → If NO: redirect to /login, return false (block request)
```

**Why `getSession(false)` instead of `getSession()`?**
- `getSession(false)` = get existing session if one exists, but DON'T create a new one
- `getSession()` or `getSession(true)` = get existing session OR create a new empty one
- We use `false` because we don't want to waste server memory creating empty sessions for every unauthenticated visitor

**Configured in `dispatcher-servlet.xml`:**
```xml
<mvc:interceptor>
    <mvc:mapping path="/register"/>       <!-- employee form page -->
    <mvc:mapping path="/register/**"/>    <!-- employee summary page -->
    <bean class="com.example.security.SessionAuthInterceptor"/>
</mvc:interceptor>
```

---

### 7. login.jsp
**Path:** `src/main/webapp/WEB-INF/views/login.jsp`

**Why:** Users need a login page to enter their credentials before accessing employee features.

**Features:**
- Username and password input fields with `required` attribute
- **Success banner** (green): shown after signup (`?registered=true`) or logout (`?loggedOut=true`)
- **Error banner** (red): shown when login fails (invalid credentials)
- **Sign up link**: directs to `/signup` for users who don't have an account
- Uses the same card layout and styling as the rest of the app

**How the banners work:**
- `request.getParameter("registered")` — checks for `?registered=true` in the URL (set by signup redirect)
- `request.getParameter("loggedOut")` — checks for `?loggedOut=true` in the URL (set by logout redirect)
- `request.getAttribute("error")` — checks for error message set by `UserController.login()` via `model.addAttribute("error", ...)`

---

### 8. signup.jsp
**Path:** `src/main/webapp/WEB-INF/views/signup.jsp`

**Why:** Users need a registration page to create an account before they can log in.

**Features:**
- Username, email, and password input fields
- Password field has `minlength="6"` for client-side validation
- **Error banner** (red): shown when registration fails (e.g., username already exists)
- **Login link**: directs to `/login` for users who already have an account
- Uses the same card layout and styling as the rest of the app

---

## Modified Files

### 1. dispatcher-servlet.xml
**Path:** `src/main/webapp/WEB-INF/dispatcher-servlet.xml`

**What changed:** Added a second interceptor inside `<mvc:interceptors>` for session-based authentication.

**Before:**
```xml
<mvc:interceptors>
    <!-- Only JWT interceptor for API endpoints -->
    <mvc:interceptor>
        <mvc:mapping path="/api/employees"/>
        <mvc:mapping path="/api/employees/**"/>
        <mvc:mapping path="/api/register"/>
        <mvc:exclude-mapping path="/api/auth/**"/>
        <bean class="com.example.security.JwtAuthInterceptor"/>
    </mvc:interceptor>
</mvc:interceptors>
```

**After:**
```xml
<mvc:interceptors>
    <!-- JWT interceptor for API endpoints (unchanged) -->
    <mvc:interceptor>
        <mvc:mapping path="/api/employees"/>
        <mvc:mapping path="/api/employees/**"/>
        <mvc:mapping path="/api/register"/>
        <mvc:exclude-mapping path="/api/auth/**"/>
        <bean class="com.example.security.JwtAuthInterceptor"/>
    </mvc:interceptor>

    <!-- NEW: Session interceptor for UI pages -->
    <mvc:interceptor>
        <mvc:mapping path="/register"/>
        <mvc:mapping path="/register/**"/>
        <bean class="com.example.security.SessionAuthInterceptor"/>
    </mvc:interceptor>
</mvc:interceptors>
```

**Why:** The employee registration form (`/register`) and summary page (`/register/summary`) were accessible to anyone. Now they require a logged-in session.

---

### 2. AuthController.java
**Path:** `src/main/java/com/example/controller/AuthController.java`

**What changed:** Replaced hardcoded credentials with database authentication via `UserService`.

**Before:**
```java
private static final String DEMO_USERNAME = "admin";
private static final String DEMO_PASSWORD = "password123";

// ...

if (DEMO_USERNAME.equals(username) && DEMO_PASSWORD.equals(password)) {
    // generate JWT
}
```

**After:**
```java
@Autowired
private UserService userService;

// ...

if (userService.authenticate(username, password)) {
    // generate JWT
}
```

**Why:** With real user accounts in the database, the API login endpoint (`POST /api/auth/login`) should validate against the same user store as the UI login. Any registered user can now get a JWT token via the API.

---

### 3. HelloController.java
**Path:** `src/main/java/com/example/controller/HelloController.java`

**What changed:** Root URL `/` now redirects to `/login` instead of `/hello`.

**Before:** `return "redirect:/hello";`
**After:** `return "redirect:/login";`

**Why:** When a user first visits the application, they should be directed to the login page — not the hello demo page. This makes login the entry point of the application.

---

### 4. employeeForm.jsp
**Path:** `src/main/webapp/WEB-INF/views/employeeForm.jsp`

**What changed:** Added a header bar showing the logged-in username and a logout link.

**Added at the top of the card:**
```html
<div style="display: flex; justify-content: space-between; align-items: center;">
    <span>Logged in as <strong>${sessionScope.loggedInUser}</strong></span>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</div>
```

**Why:**
- Users should see WHO they are logged in as (feedback that authentication worked)
- Users need a way to log out (the "Logout" link calls `/logout` which destroys the session)
- `${sessionScope.loggedInUser}` reads the username stored in the HTTP session by `UserController.login()`

---

### 5. employeeSummary.jsp
**Path:** `src/main/webapp/WEB-INF/views/employeeSummary.jsp`

**What changed:** Same as employeeForm.jsp — added logged-in user display and logout link.

**Why:** Consistent user experience — the user should see their login status and have access to logout on every protected page.

---

## Two Authentication Systems Explained

The application now has **two separate authentication systems** for two different purposes:

| | Session Auth (UI) | JWT Auth (API) |
|---|---|---|
| **Protects** | JSP pages (`/register`, `/register/summary`) | REST endpoints (`/api/employees`, `/api/register`) |
| **Used by** | Browser users (human clicking through pages) | API clients (Postman, JavaScript fetch, mobile apps) |
| **How it works** | Cookie-based session (`JSESSIONID`) | Token in `Authorization: Bearer <token>` header |
| **Login endpoint** | `POST /login` (form submission → session) | `POST /api/auth/login` (JSON → JWT token) |
| **Interceptor** | `SessionAuthInterceptor` | `JwtAuthInterceptor` |
| **Storage** | Server-side session memory | Client holds the token |
| **Expiration** | When session times out or user logs out | 5 minutes (configured in `JwtUtil`) |

---

## Password Hashing Explained

Passwords are **never stored as plain text**. They are hashed using SHA-256 before being saved to the database.

### What is hashing?
A hash function takes any input and produces a fixed-size output (a "hash"). It is **one-way** — you cannot reverse a hash back to the original input.

### Example:
```
Input:  "mypassword"
SHA-256: "89e01536ac207279409d4de1e5253e01f4a1769e696db0d6062ca9b8f56767c8"

Input:  "mypassword1"  (just one character different)
SHA-256: "b4b8e435b7f40e650e13df2ef5ff8104bbc83e6e3d0387a3f5b4d4df77b8e773"
```

### How login works with hashing:
1. **Registration:** User enters `"mypassword"` → hash it → store `"89e01536ac..."` in database
2. **Login:** User enters `"mypassword"` → hash it → get `"89e01536ac..."` → compare with database
3. **Match!** → Login succeeds (same input always produces the same hash)

### Why not store plain text?
If someone gains access to the database, they would see hashes like `"89e01536ac..."` instead of `"mypassword"`. They cannot reverse the hash to find the original password.

---

## Session vs JWT — Why Both?

### Why sessions for UI pages?
- Browsers automatically handle session cookies — no JavaScript needed
- Built into the Servlet API (`HttpSession`)
- Simple: set attribute on login, check attribute on each request, invalidate on logout
- Works naturally with form-based login (POST form → redirect)

### Why JWT for API endpoints?
- API clients (Postman, mobile apps) don't have browser cookies
- JWT tokens are stateless — the server doesn't need to store anything
- Tokens can be passed in the `Authorization` header
- Works well with JavaScript `fetch()` calls

### Why not use one system for both?
- Sessions require a browser cookie mechanism → API clients can't easily use them
- JWTs require JavaScript to manage tokens → annoying for page-to-page navigation
- Using both gives the best experience for both browser users and API clients

---

## Database Changes

### New Table: `users`
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);
```

The table is created automatically when the application starts (in `UserDAOImpl` constructor), the same way the `employees` table is created in `EmployeeDAOImpl`.

### Existing Table: `employees` (unchanged)
```sql
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    contact_number VARCHAR(10) NOT NULL,
    position VARCHAR(255) NOT NULL
);
```

Both tables live in the same H2 database file: `./data/employeedb`

---

## Architecture Diagram

### Layer Architecture (same 3-tier pattern, now with User components)

```
┌─────────────────────────────────────────────────────────┐
│                    BROWSER (UI Layer)                     │
│  login.jsp │ signup.jsp │ employeeForm.jsp │ summary.jsp │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP Requests
                       ▼
┌─────────────────────────────────────────────────────────┐
│              INTERCEPTORS (Security Layer)                │
│  SessionAuthInterceptor ──→ /register, /register/**      │
│  JwtAuthInterceptor     ──→ /api/employees, /api/register│
└──────────────────────┬──────────────────────────────────┘
                       │ (allowed through)
                       ▼
┌─────────────────────────────────────────────────────────┐
│                CONTROLLERS (Web Layer)                    │
│  UserController      ──→ /login, /signup, /logout        │
│  EmployeeController  ──→ /register, /api/employees       │
│  AuthController      ──→ /api/auth/login                 │
│  HelloController     ──→ /, /hello                       │
└──────────────────────┬──────────────────────────────────┘
                       │ calls
                       ▼
┌─────────────────────────────────────────────────────────┐
│                 SERVICES (Business Logic)                 │
│  UserService         ──→ registerUser(), authenticate()  │
│  EmployeeService     ──→ registerEmployee(), getAll()... │
└──────────────────────┬──────────────────────────────────┘
                       │ calls
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    DAOs (Data Access)                     │
│  UserDAOImpl         ──→ save(), findByUsername()         │
│  EmployeeDAOImpl     ──→ save(), findAll(), update()...  │
└──────────────────────┬──────────────────────────────────┘
                       │ SQL queries
                       ▼
┌─────────────────────────────────────────────────────────┐
│                   H2 DATABASE                            │
│  Table: users      (id, username, password, email)       │
│  Table: employees  (id, name, email, contact_number,     │
│                     position)                            │
└─────────────────────────────────────────────────────────┘
```

#### File Structure (new files marked with ★)

```
src/main/java/com/example/
├── controller/
│   ├── AuthController.java          ← MODIFIED (uses UserService now)
│   ├── EmployeeController.java
│   ├── HelloController.java         ← MODIFIED (/ redirects to /login)
│   └── UserController.java          ★ NEW (login, signup, logout pages)
├── dao/
│   ├── EmployeeDAO.java
│   ├── EmployeeDAOImpl.java
│   ├── UserDAO.java                 ★ NEW (interface for user DB operations)
│   └── UserDAOImpl.java             ★ NEW (SQL queries for users table)
├── dto/
│   ├── EmployeeDTO.java
│   └── UserDTO.java                 ★ NEW (user data container for DB)
├── model/
│   └── Employee.java
├── security/
│   ├── JwtAuthInterceptor.java
│   ├── JwtUtil.java
│   └── SessionAuthInterceptor.java  ★ NEW (blocks unauthenticated UI access)
└── service/
    ├── EmployeeService.java
    └── UserService.java             ★ NEW (registration + authentication logic)

src/main/webapp/
├── WEB-INF/
│   ├── dispatcher-servlet.xml       ← MODIFIED (added session interceptor)
│   ├── web.xml
│   └── views/
│       ├── employeeForm.jsp         ← MODIFIED (added logged-in user + logout)
│       ├── employeeSummary.jsp      ← MODIFIED (added logged-in user + logout)
│       ├── hello.jsp
│       ├── login.jsp                ★ NEW (login form page)
│       └── signup.jsp               ★ NEW (user registration page)
└── css/
    └── style.css
```
