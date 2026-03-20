# Employee Registration Form - Spring MVC Implementation

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [How the Application Works (Request Flow)](#how-the-application-works-request-flow)
4. [POJO - Employee.java](#pojo---employeejava)
5. [Service - EmployeeService.java](#service---employeeservicejava)
6. [Controller - EmployeeController.java](#controller---employeecontrollerjava)
7. [Views (JSP Pages)](#views-jsp-pages)
   - [employeeForm.jsp](#employeeformjsp---registration-form)
   - [employeeSummary.jsp](#employeesummaryjsp---registration-summary)
8. [CSS - style.css](#css---stylecss)
9. [Configuration Files](#configuration-files)
   - [dispatcher-servlet.xml](#dispatcher-servletxml)
   - [web.xml](#webxml)

---

## Overview

This application implements an **Employee Registration Form** using the **Spring MVC (Model-View-Controller)** pattern. A user fills in a form with employee details (Name, Email, Contact Number, and Position), submits it, and the data is sent to the server. The server processes the data and displays it on a summary page.

The implementation uses three core components as required:

- **POJO (Plain Old Java Object)** - `Employee.java` to represent employee data
- **Service** - `EmployeeService.java` to handle the business logic
- **Controller** - `EmployeeController.java` to handle HTTP requests and responses

---

## Project Structure

```
src/main/
├── java/com/example/
│   ├── controller/
│   │   └── EmployeeController.java    <-- Handles HTTP requests
│   ├── model/
│   │   └── Employee.java              <-- POJO (data object)
│   └── service/
│       └── EmployeeService.java       <-- Business logic layer
└── webapp/
    ├── css/
    │   └── style.css                  <-- External stylesheet
    └── WEB-INF/
        ├── dispatcher-servlet.xml     <-- Spring MVC configuration
        ├── web.xml                    <-- Web application configuration
        └── views/
            ├── employeeForm.jsp       <-- Registration form page
            └── employeeSummary.jsp    <-- Summary display page
```

---

## How the Application Works (Request Flow)

The entire flow from the user opening the form to seeing the summary follows these steps:

### Step 1: User Opens the Registration Form

```
Browser  -->  GET /register  -->  Tomcat  -->  DispatcherServlet  -->  EmployeeController.showRegistrationForm()
                                                                              |
                                                                        returns "employeeForm"
                                                                              |
                                                                        ViewResolver resolves to
                                                                        /WEB-INF/views/employeeForm.jsp
                                                                              |
                                                                        JSP renders HTML form
                                                                              |
Browser  <--  HTML form page  <-----------------------------------------------+
```

1. The user navigates to `/register` in their browser.
2. Tomcat receives the request and passes it to the **DispatcherServlet** (Spring's front controller).
3. DispatcherServlet looks up which controller method handles `GET /register`.
4. It finds `EmployeeController.showRegistrationForm()` and calls it.
5. The method returns the view name `"employeeForm"`.
6. The **ViewResolver** maps `"employeeForm"` to `/WEB-INF/views/employeeForm.jsp`.
7. The JSP renders the HTML registration form and sends it back to the browser.

### Step 2: User Fills the Form and Clicks Submit

```
Browser  -->  POST /register (with form data)  -->  Tomcat  -->  DispatcherServlet
                                                                       |
                                                              EmployeeController.registerEmployee()
                                                                       |
                                                              Calls EmployeeService.registerEmployee()
                                                                       |
                                                              Service creates Employee POJO
                                                                       |
                                                              Controller adds Employee to Model
                                                                       |
                                                              returns "employeeSummary"
                                                                       |
                                                              ViewResolver resolves to
                                                              /WEB-INF/views/employeeSummary.jsp
                                                                       |
                                                              JSP renders summary using ${employee.name}, etc.
                                                                       |
Browser  <--  HTML summary page  <-------------------------------------+
```

1. The user fills in all fields (Name, Email, Contact Number, Position) and clicks **Submit**.
2. The browser sends a `POST` request to `/register` with the form data.
3. DispatcherServlet routes it to `EmployeeController.registerEmployee()`.
4. The controller extracts form parameters using `@RequestParam`.
5. The controller calls `EmployeeService.registerEmployee()` to create an `Employee` object.
6. The `Employee` object is added to the **Model** (a data container shared between controller and view).
7. The controller returns the view name `"employeeSummary"`.
8. The ViewResolver maps it to `/WEB-INF/views/employeeSummary.jsp`.
9. The JSP accesses the employee data from the Model using Expression Language (e.g., `${employee.name}`) and renders the summary page.

---

## POJO - Employee.java

**Location:** `src/main/java/com/example/model/Employee.java`

### What is a POJO?

A **POJO (Plain Old Java Object)** is a simple Java class that does not extend or implement any special framework classes or interfaces. It is used purely to represent data. In this application, the `Employee` POJO represents the data structure for an employee.

### What Does This Class Do?

The `Employee` class holds the four pieces of information collected from the registration form:

| Field           | Type   | Description                        |
|-----------------|--------|------------------------------------|
| `name`          | String | The employee's full name           |
| `email`         | String | The employee's email address       |
| `contactNumber` | String | The employee's phone number        |
| `position`      | String | The employee's job position/title  |

### Key Components of the POJO

```java
public class Employee {

    private String name;
    private String email;
    private String contactNumber;
    private String position;
```

- **Private fields** - The four data fields are declared as `private`, meaning they cannot be accessed directly from outside the class. This is called **encapsulation**, a core principle of object-oriented programming.

```java
    public Employee() {
    }
```

- **No-argument constructor** - A constructor with no parameters. This is needed by Spring and other frameworks that create objects using reflection. Without this, the framework would not be able to instantiate the class.

```java
    public Employee(String name, String email, String contactNumber, String position) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.position = position;
    }
```

- **Parameterized constructor** - A convenience constructor that lets us create an `Employee` object with all fields set in a single line. The `EmployeeService` uses this constructor.

```java
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ... same pattern for email, contactNumber, position
```

- **Getters and Setters** - Public methods that provide controlled read/write access to the private fields. The JSP view uses the **getters** when it accesses `${employee.name}` - behind the scenes, this calls `employee.getName()`.

### Why Use a POJO?

- **Separation of concerns** - Data representation is separate from business logic and presentation.
- **Reusability** - The same `Employee` object can be used across controllers, services, and views.
- **Framework compatibility** - Spring, JSP Expression Language, and other tools expect standard getter/setter conventions (known as the **JavaBean convention**).

---

## Service - EmployeeService.java

**Location:** `src/main/java/com/example/service/EmployeeService.java`

### What is a Service?

In the Spring MVC architecture, the **Service layer** contains the **business logic** of the application. It sits between the Controller (which handles HTTP) and the data/model layer. The controller should not contain business logic directly - instead, it delegates to the service.

### What Does This Class Do?

```java
@Service
public class EmployeeService {

    public Employee registerEmployee(String name, String email, String contactNumber, String position) {
        return new Employee(name, email, contactNumber, position);
    }
}
```

### Key Components

- **`@Service` annotation** - This annotation tells Spring that this class is a service component. When the application starts, Spring's **component scanning** (configured in `dispatcher-servlet.xml`) detects this annotation and automatically creates an instance of `EmployeeService` and registers it in the **Spring Application Context** (a container that manages all Spring-managed objects, called "beans").

- **`registerEmployee()` method** - This method takes the four form fields as parameters and creates a new `Employee` POJO using the parameterized constructor. It returns the fully populated `Employee` object.

### Why Use a Service Layer?

Even though our service is simple, the service layer pattern is important because:

1. **Separation of concerns** - The controller handles HTTP request/response concerns. The service handles business logic. If we later need to add validation, save to a database, or send a confirmation email, we add that logic here without touching the controller.
2. **Reusability** - Multiple controllers or other services can call the same service method.
3. **Testability** - Services can be unit tested independently without needing an HTTP context.

### How Spring Manages the Service (Dependency Injection)

The `EmployeeController` does not create the `EmployeeService` itself using `new EmployeeService()`. Instead, Spring **injects** it automatically:

```java
// In EmployeeController:
@Autowired
private EmployeeService employeeService;
```

This is called **Dependency Injection (DI)**. Spring sees the `@Autowired` annotation, looks up the `EmployeeService` bean in its Application Context, and automatically assigns it to this field. This means the controller does not need to know how to create the service - Spring handles it.

---

## Controller - EmployeeController.java

**Location:** `src/main/java/com/example/controller/EmployeeController.java`

### What is a Controller?

The **Controller** is the "C" in MVC. It is the component that receives HTTP requests from the user's browser, processes them (by calling services), prepares data for the view, and returns the name of the view to render.

### What Does This Class Do?

The `EmployeeController` handles two HTTP requests:

| HTTP Method | URL        | Method                   | Purpose                     |
|-------------|------------|--------------------------|-----------------------------|
| GET         | /register  | `showRegistrationForm()` | Display the empty form      |
| POST        | /register  | `registerEmployee()`     | Process form submission     |

### Key Components

#### Class-Level Annotations

```java
@Controller
public class EmployeeController {
```

- **`@Controller`** - Marks this class as a Spring MVC controller. Spring's component scanning detects it and registers it as a bean that can handle HTTP requests.

#### Dependency Injection

```java
    @Autowired
    private EmployeeService employeeService;
```

- **`@Autowired`** - Tells Spring to automatically inject an instance of `EmployeeService` into this field. The controller can then use `employeeService` to call business logic methods without manually creating the service object.

#### GET /register - Show the Form

```java
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "employeeForm";
    }
```

- **`@GetMapping("/register")`** - This method handles HTTP GET requests to the `/register` URL. When a user navigates to this URL in their browser, this method is called.
- The method simply returns the String `"employeeForm"`, which is the **view name**. The ViewResolver maps this to `/WEB-INF/views/employeeForm.jsp`.

#### POST /register - Process the Form

```java
    @PostMapping("/register")
    public String registerEmployee(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("position") String position,
            Model model) {

        Employee employee = employeeService.registerEmployee(name, email, contactNumber, position);
        model.addAttribute("employee", employee);
        return "employeeSummary";
    }
```

- **`@PostMapping("/register")`** - Handles HTTP POST requests to `/register`. The form's `method="post"` triggers this.

- **`@RequestParam("name") String name`** - Extracts the value of the form field named `"name"` from the POST request body and assigns it to the `name` parameter. The same is done for `email`, `contactNumber`, and `position`. Each `@RequestParam` corresponds to an `<input name="...">` in the JSP form.

- **`Model model`** - Spring automatically provides a `Model` object. The Model acts as a container (like a Map) that carries data from the controller to the view. Whatever you add to the Model becomes accessible in the JSP via Expression Language.

- **`employeeService.registerEmployee(...)`** - Delegates to the service layer to create the `Employee` POJO.

- **`model.addAttribute("employee", employee)`** - Adds the `Employee` object to the Model under the key `"employee"`. The JSP view can then access it using `${employee}`.

- **`return "employeeSummary"`** - Returns the view name. The ViewResolver maps this to `/WEB-INF/views/employeeSummary.jsp`.

---

## Views (JSP Pages)

The **Views** are the "V" in MVC. They are responsible only for **presentation** - displaying data to the user. They do not contain business logic.

### employeeForm.jsp - Registration Form

**Location:** `src/main/webapp/WEB-INF/views/employeeForm.jsp`

This JSP renders the HTML registration form that the user fills in.

#### Key Parts

```html
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
```

- Links to the external CSS file. `${pageContext.request.contextPath}` dynamically resolves to the application's base URL (e.g., `/SpringMvcHelloWorld`), ensuring the CSS path works regardless of deployment context.

```html
<form action="${pageContext.request.contextPath}/register" method="post">
```

- **`action`** - The URL the form data is sent to when submitted. Points to `/register`.
- **`method="post"`** - Sends the data as an HTTP POST request (which the controller's `@PostMapping` handles).

```html
<input type="text" id="name" name="name" required>
```

- **`name="name"`** - This is critical. The `name` attribute must match the `@RequestParam("name")` in the controller. This is how Spring knows which form field maps to which method parameter.
- **`required`** - HTML5 validation that prevents submission if the field is empty.

The form has four fields:

| Field          | Input Type | name Attribute    |
|----------------|------------|-------------------|
| Name           | text       | `name`            |
| Email          | email      | `email`           |
| Contact Number | tel        | `contactNumber`   |
| Position       | text       | `position`        |

### employeeSummary.jsp - Registration Summary

**Location:** `src/main/webapp/WEB-INF/views/employeeSummary.jsp`

This JSP displays the submitted employee data in a table format.

#### Key Parts

```html
<td>${employee.name}</td>
<td>${employee.email}</td>
<td>${employee.contactNumber}</td>
<td>${employee.position}</td>
```

- **`${employee.name}`** - This is **Expression Language (EL)**. It accesses the `employee` object that the controller added to the Model via `model.addAttribute("employee", employee)`. Behind the scenes, `${employee.name}` calls `employee.getName()`. This is why the POJO needs proper getter methods following the JavaBean naming convention.

```html
<a class="back-link" href="${pageContext.request.contextPath}/register">Register Another Employee</a>
```

- A link back to the registration form so the user can register another employee.

---

## CSS - style.css

**Location:** `src/main/webapp/css/style.css`

### Why a Separate CSS File?

Instead of writing `<style>` blocks inside each JSP, all styles are centralized in a single external CSS file. This provides:

1. **Reusability** - All pages share the same stylesheet, ensuring consistent styling.
2. **Maintainability** - Changing a style in one place updates it across all pages.
3. **Separation of concerns** - HTML handles structure, CSS handles presentation.
4. **Caching** - Browsers cache external CSS files, so they are downloaded only once instead of being embedded in every page response.

### How Static Resources Are Served

By default, Spring's DispatcherServlet intercepts all requests (URL pattern `/`), including requests for CSS files. Without special configuration, a request for `/css/style.css` would be routed to a controller (which doesn't exist for that URL), resulting in a 404 error.

To fix this, the following line was added to `dispatcher-servlet.xml`:

```xml
<mvc:resources mapping="/css/**" location="/css/"/>
```

This tells Spring: "For any URL starting with `/css/`, serve the file directly from the `/css/` directory in the webapp folder instead of routing it to a controller."

---

## Configuration Files

### dispatcher-servlet.xml

**Location:** `src/main/webapp/WEB-INF/dispatcher-servlet.xml`

This is the Spring MVC configuration file. It tells Spring how to find and configure the application's components.

```xml
<context:component-scan base-package="com.example"/>
```

- **Component Scanning** - Tells Spring to scan all classes under `com.example` and automatically register any class annotated with `@Controller`, `@Service`, `@Repository`, or `@Component` as a Spring bean. This is how Spring discovers `EmployeeController` and `EmployeeService`.

```xml
<mvc:annotation-driven/>
```

- **Annotation-Driven MVC** - Enables support for annotations like `@GetMapping`, `@PostMapping`, `@RequestParam`, etc. Without this, Spring would not know how to process these annotations.

```xml
<mvc:resources mapping="/css/**" location="/css/"/>
```

- **Static Resources** - Maps URL patterns starting with `/css/` to the physical `/css/` directory so static files (CSS, JS, images) are served directly.

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

- **View Resolver** - When a controller returns a view name like `"employeeForm"`, the ViewResolver adds the prefix and suffix to construct the full path: `/WEB-INF/views/` + `employeeForm` + `.jsp` = `/WEB-INF/views/employeeForm.jsp`.

### web.xml

**Location:** `src/main/webapp/WEB-INF/web.xml`

This is the standard Java web application deployment descriptor. It configures how Tomcat handles the application.

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/dispatcher-servlet.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

- Registers Spring's **DispatcherServlet** as the front controller. All incoming requests pass through it first. The `contextConfigLocation` parameter tells it where to find the Spring configuration file.

```xml
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

- Maps all URL patterns (`/`) to the DispatcherServlet, meaning every request to the application is handled by Spring MVC.

---

## How to Run

1. Build the project: `mvn clean package`
2. Deploy the generated WAR file (`target/SpringMvcHelloWorld.war`) to Tomcat, or use the included `run.sh` / `run.bat` script.
3. Open a browser and go to: `http://localhost:8080/SpringMvcHelloWorld/register`
4. Fill in the form and click Submit to see the summary page.
