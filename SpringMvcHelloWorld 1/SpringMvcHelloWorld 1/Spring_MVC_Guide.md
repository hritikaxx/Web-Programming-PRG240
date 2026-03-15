# Spring MVC - Complete Java Guide

## What is Spring MVC?

Spring MVC is a web framework built on top of the Java Servlet API. It implements the
**Model-View-Controller (MVC)** design pattern to separate an application into three
interconnected components:

- **Model** - the data
- **View** - the presentation (what the user sees)
- **Controller** - the logic (handles user requests)

This separation makes the code organized, testable, and maintainable.

---

## The MVC Architecture - How a Request Flows

```
                         Spring MVC Request Flow
                         =======================

  Browser                                                      Server
  =======                                                      ======

  1. User visits              2. Tomcat receives request
     /hello?name=Abiral  -->     and passes it to
                                 DispatcherServlet
                                        |
                                        v
                              3. DispatcherServlet asks
                                 HandlerMapping:
                                 "Which Controller handles /hello?"
                                        |
                                        v
                              4. HandlerMapping says:
                                 "HelloController.hello()"
                                        |
                                        v
                              5. DispatcherServlet calls
                                 HelloController.hello()
                                        |
                                        v
                              6. Controller:
                                 - Builds greeting "Hello, Abiral!"
                                 - Adds it to Model
                                 - Returns view name "hello"
                                        |
                                        v
                              7. DispatcherServlet asks
                                 ViewResolver:
                                 "Where is the 'hello' view?"
                                        |
                                        v
                              8. ViewResolver says:
                                 "/WEB-INF/views/hello.jsp"
                                        |
                                        v
                              9. JSP renders HTML using
                                 data from the Model
                                        |
  10. Browser displays   <--            v
      the HTML page          Response sent back to browser
```

---

## 1. DispatcherServlet (The Front Controller)

### What is it?
The `DispatcherServlet` is the **single entry point** for all HTTP requests in a Spring MVC
application. It acts as a **Front Controller** - every request goes through it first.

### Why do we need it?
Without DispatcherServlet, you would need to create a separate Servlet for every URL in your
application. DispatcherServlet centralizes request handling and delegates to the right Controller.

### How is it configured?

In `web.xml` (our project's actual code):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                             https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

    <!-- Register the DispatcherServlet (Spring's Front Controller) -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

        <!-- Tell Spring where to find its configuration file -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/dispatcher-servlet.xml</param-value>
        </init-param>

        <!-- Load this servlet when the server starts (not on first request) -->
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- Map ALL URL patterns to the DispatcherServlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>
```

### What does each part mean?

| Element                        | Purpose                                                        |
|--------------------------------|----------------------------------------------------------------|
| `<web-app version="6.0">`     | Jakarta EE 6.0 web app descriptor (required for Tomcat 10+)   |
| `<servlet-class>`             | The fully qualified class name of Spring's DispatcherServlet   |
| `contextConfigLocation`       | Path to the Spring configuration file                          |
| `<load-on-startup>1</load-on-startup>` | Load immediately when server starts (not on first request) |
| `<url-pattern>/</url-pattern>`| Intercept ALL incoming requests                                |

### What DispatcherServlet does internally:
1. Receives the HTTP request from Tomcat
2. Consults **HandlerMapping** to find the right Controller method
3. Calls the Controller method
4. Receives the view name and Model data from the Controller
5. Passes the view name to **ViewResolver** to find the actual JSP file
6. Forwards the Model data to the JSP for rendering
7. Returns the rendered HTML response to the browser

---

## 2. Model (The Data - "M" in MVC)

### What is it?
The Model represents the **data** of the application. In Spring MVC, the `Model` is an
interface that acts as a **container** (like a `Map<String, Object>`) to pass data from
the Controller to the View.

### How our project uses Model:

In our `HelloController.java`, we add a simple String directly to the Model:

```java
@GetMapping("/hello")
public String hello(
        @RequestParam(value = "name", defaultValue = "World") String name,
        Model model) {

    // Add the greeting directly as a String attribute to the Model
    model.addAttribute("greeting", "Hello, " + name + "!");

    // Return the view name
    return "hello";
}
```

The Controller calls `model.addAttribute("greeting", "Hello, Abiral!")`.
The JSP View accesses it via `${greeting}`.

**How data flows:**
```
Controller                     Model (container)              View (JSP)
==========                     ================               ==========
Builds greeting String  -->    Stores it as "greeting" -->    Accesses via ${greeting}
```

### Model with POJO classes (for larger applications)

In our Hello World project, we pass a simple String to the Model. But in real-world
applications, you would typically create **POJO (Plain Old Java Object)** classes to
hold structured data. For example:

```java
// Model class - a POJO that holds data
public class Message {
    private String text;

    public Message() {}

    public Message(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
```

Then in the Controller:
```java
Message message = new Message("Hello, Abiral!");
model.addAttribute("message", message);
```

And in the JSP View:
```jsp
<!-- EL calls message.getText() automatically -->
<p>${message.text}</p>
```

**Key points about Model POJOs:**
- A POJO is a simple Java class - no Spring annotations needed
- Has private fields, a constructor, and getter/setter methods
- The Model class does NOT know about Controllers or Views
- It simply holds data that flows between the Controller and View
- Use POJOs when you have structured data (e.g., Employee with name, email, position)
- For simple data like a greeting string, you can pass it directly without a POJO

---

## 3. View (The Presentation - "V" in MVC)

### What is it?
The View is responsible for **rendering the user interface**. In this project, we use
JSP (JavaServer Pages) as the view technology.

### Our hello.jsp (actual project code):
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring MVC Hello World</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            background-color: #f0f2f5;
            color: #333;
        }
        .card {
            background: #fff;
            padding: 40px 50px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 500px;
        }
        h1 { color: #2e7d32; margin-bottom: 10px; }
        p.greeting { font-size: 1.4rem; color: #555; margin: 20px 0; }
        .info { font-size: 0.9rem; color: #888; margin-top: 20px; line-height: 1.6; }
        a { color: #2e7d32; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Spring MVC Hello World</h1>

        <!-- Display the greeting from the Model -->
        <p class="greeting">${greeting}</p>

        <p class="info">
            Try: <a href="${pageContext.request.contextPath}/hello?name=Abiral">/hello?name=Abiral</a>
        </p>
    </div>
</body>
</html>
```

### How does the View get data?
1. The Controller adds `greeting` to the Model: `model.addAttribute("greeting", "Hello, Abiral!")`
2. The ViewResolver finds `/WEB-INF/views/hello.jsp`
3. The JSP accesses it using **Expression Language (EL)**: `${greeting}`
4. `${greeting}` reads the `"greeting"` attribute from the Model and outputs its value

### Expression Language (EL) - `${}` syntax
EL is used in JSP to access data from the Model:
- `${greeting}` - accesses a simple String attribute named "greeting"
- `${message.text}` - accesses a POJO; calls `message.getText()` automatically
- `${pageContext.request.contextPath}` - gets the application's base URL path

### ViewResolver Configuration

In `dispatcher-servlet.xml`:

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

**How it works:**
```
Controller returns: "hello"
ViewResolver adds:  prefix + "hello" + suffix
Result:             /WEB-INF/views/hello.jsp
```

### Why are JSPs inside /WEB-INF/?
Files inside `/WEB-INF/` **cannot** be accessed directly by the browser. This is a security
feature - users must go through the Controller, which ensures proper request handling.

---

## 4. Controller (The Logic - "C" in MVC)

### What is it?
The Controller is the **brain** of the MVC pattern. It receives HTTP requests, processes them,
prepares data, and decides which View to show.

### Our HelloController.java (actual project code):
```java
package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "redirect:/hello";
    }

    @GetMapping("/hello")
    public String hello(
            @RequestParam(value = "name", defaultValue = "World") String name,
            Model model) {

        // Add the greeting directly as a String attribute to the Model
        model.addAttribute("greeting", "Hello, " + name + "!");

        // Return the view name - ViewResolver will resolve this to /WEB-INF/views/hello.jsp
        return "hello";
    }
}
```

### Annotations Explained:

| Annotation                    | Purpose                                                            |
|-------------------------------|--------------------------------------------------------------------|
| `@Controller`                 | Marks this class as a Spring MVC Controller (detected by component scan) |
| `@GetMapping("/")`           | Maps HTTP GET requests to `/` to the `home()` method               |
| `@GetMapping("/hello")`      | Maps HTTP GET requests to `/hello` to the `hello()` method         |
| `@RequestParam`              | Extracts query parameters from the URL (e.g., `?name=Abiral`)     |

### What the Controller does step by step:
1. `@GetMapping("/")` - if user visits root `/`, redirect them to `/hello`
2. `@GetMapping("/hello")` - Spring routes GET `/hello` requests to the `hello()` method
3. `@RequestParam(value = "name", defaultValue = "World")` - reads `?name=...` from URL; if no name parameter is provided, uses "World"
4. `model.addAttribute("greeting", "Hello, " + name + "!")` - puts data into the Model container
5. `return "hello"` - tells DispatcherServlet to use the "hello" view

### Other common annotations (for reference):

| Annotation          | Purpose                                              |
|---------------------|------------------------------------------------------|
| `@PostMapping`      | Maps HTTP POST requests (form submissions)           |
| `@RequestMapping`   | Maps any HTTP method to a URL (parent of Get/Post)   |
| `@ModelAttribute`   | Binds form data to a POJO object automatically       |
| `@PathVariable`     | Extracts values from the URL path (e.g., `/user/{id}`) |

---

## 5. Spring MVC Configuration (dispatcher-servlet.xml)

This file is the **heart of Spring MVC configuration**. It is loaded by the DispatcherServlet
when the server starts.

### Our dispatcher-servlet.xml (actual project code):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd
            http://www.springframework.org/schema/mvc
            http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- Scan the 'com.example' package for Spring components (@Controller, @Service, etc.) -->
    <context:component-scan base-package="com.example"/>

    <!-- Enable Spring MVC annotations like @Controller, @RequestMapping, @GetMapping -->
    <mvc:annotation-driven/>

    <!-- ViewResolver: maps view names returned by Controllers to JSP files -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
```

### What each line does:

| Configuration                                    | Purpose                                                    |
|--------------------------------------------------|------------------------------------------------------------|
| `<context:component-scan base-package="com.example"/>` | Scans `com.example` package and sub-packages for classes annotated with `@Controller`, `@Service`, etc. and registers them as Spring beans |
| `<mvc:annotation-driven/>`                       | Enables annotation-based MVC configuration (`@GetMapping`, `@RequestParam`, etc.) |
| `InternalResourceViewResolver`                   | Resolves view names to JSP files by adding prefix and suffix |

---

## 6. Maven Configuration (pom.xml)

Maven manages the project's **dependencies** (libraries) and **build process**.

### Our pom.xml (actual project code):

```xml
<project ...>
    <groupId>com.example</groupId>
    <artifactId>SpringMvcHelloWorld</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Web MVC - the core framework -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>6.2.10</version>
        </dependency>

        <!-- Servlet API - provided by Tomcat, not packaged in WAR -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- JSTL - JSP Standard Tag Library -->
        <dependency>
            <groupId>jakarta.servlet.jsp.jstl</groupId>
            <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
            <version>3.0.0</version>
        </dependency>
    </dependencies>
</project>
```

### Dependencies Explained:

| Dependency            | Why it's needed                                              |
|-----------------------|--------------------------------------------------------------|
| `spring-webmvc`      | Provides DispatcherServlet, @Controller, Model, ViewResolver - the entire Spring MVC framework |
| `jakarta.servlet-api` | The Servlet API that Spring MVC builds on. Scope `provided` means Tomcat already has it, so don't include it in the WAR |
| `jakarta.servlet.jsp.jstl` | JSTL library for using Expression Language `${}` and tags in JSP files |

### Key Build Settings:

| Setting               | Purpose                                                     |
|-----------------------|-------------------------------------------------------------|
| `<packaging>war</packaging>` | Build a WAR (Web Application Archive) file for deployment to Tomcat |
| `<finalName>SpringMvcHelloWorld</finalName>` | The WAR file will be named `SpringMvcHelloWorld.war` |
| `maven-compiler-plugin` | Compiles Java source code using Java 17                   |
| `maven-war-plugin`   | Packages the project as a WAR file                           |

---

## 7. Putting It All Together - Complete Request Example

**User visits:** `http://localhost:8080/SpringMvcHelloWorld/hello?name=Abiral`

```
Step 1: Tomcat receives the HTTP GET request for /hello?name=Abiral

Step 2: Tomcat checks web.xml, finds that "/" is mapped to DispatcherServlet
        --> Passes the request to DispatcherServlet

Step 3: DispatcherServlet reads dispatcher-servlet.xml on startup, which told it to:
        - Scan com.example package for @Controller classes
        - Use InternalResourceViewResolver for views

Step 4: DispatcherServlet finds that @GetMapping("/hello") in HelloController
        matches the request URL /hello

Step 5: DispatcherServlet calls HelloController.hello("Abiral", model)
        - @RequestParam extracts "Abiral" from ?name=Abiral
        - Controller builds greeting: "Hello, Abiral!"
        - Controller adds greeting to Model: model.addAttribute("greeting", "Hello, Abiral!")
        - Controller returns view name "hello"

Step 6: DispatcherServlet passes "hello" to ViewResolver
        - ViewResolver resolves: /WEB-INF/views/ + "hello" + .jsp
        - Result: /WEB-INF/views/hello.jsp

Step 7: DispatcherServlet forwards Model data to hello.jsp
        - JSP accesses ${greeting} which reads the "greeting" attribute from Model
        - JSP renders: "Hello, Abiral!"

Step 8: Rendered HTML is sent back to the browser
        - User sees: "Hello, Abiral!" on the web page
```

---

## 8. Project File Structure

```
SpringMvcHelloWorld/
├── pom.xml                                    <-- Maven config (dependencies & build)
├── run.sh                                     <-- macOS: build, deploy & start Tomcat
├── run.bat                                    <-- Windows: build, deploy & start Tomcat
├── Spring_MVC_Guide.md                        <-- This guide
├── SETUP_GUIDE.md                             <-- Setup instructions for Windows & macOS
└── src/main/
    ├── java/com/example/
    │   └── controller/
    │       └── HelloController.java           <-- CONTROLLER: handles requests, adds data to Model
    └── webapp/WEB-INF/
        ├── web.xml                            <-- Registers DispatcherServlet with Tomcat
        ├── dispatcher-servlet.xml             <-- Spring MVC config (component scan, ViewResolver)
        └── views/
            └── hello.jsp                      <-- VIEW: renders HTML using ${greeting} from Model
```

---

## 9. Key Takeaways

1. **DispatcherServlet** is the Front Controller - the single entry point for all requests
2. **Model** is a data container (`Map<String, Object>`) that carries data from Controller to View. You can add simple Strings or complex POJO objects to it
3. **View** (JSP) only handles presentation - it displays data from the Model using `${}`
4. **Controller** handles the request, prepares data, adds it to the Model, and chooses which View to render
5. **ViewResolver** maps logical view names (like "hello") to actual files (`/WEB-INF/views/hello.jsp`)
6. **web.xml** tells Tomcat about the DispatcherServlet and uses Jakarta EE 6.0 namespace for Tomcat 10+
7. **dispatcher-servlet.xml** tells Spring where to find Controllers and how to resolve Views
8. **pom.xml** manages dependencies - `spring-webmvc` is the core, `jakarta.servlet-api` is provided by Tomcat
9. The MVC pattern keeps **data**, **logic**, and **presentation** separate and organized
