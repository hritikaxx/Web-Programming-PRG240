# Spring MVC Hello World

A minimal Spring Web MVC project demonstrating the Model-View-Controller pattern.

## Project Structure

```
src/main/
├── java/com/example/
│   ├── controller/
│   │   └── HelloController.java    <-- Controller (handles requests)
│   └── model/
│       └── Message.java            <-- Model (holds data)
└── webapp/
    └── WEB-INF/
        ├── views/
        │   └── hello.jsp           <-- View (renders HTML)
        ├── dispatcher-servlet.xml  <-- Spring MVC config
        └── web.xml                 <-- Deployment descriptor
```

## How to Run

1. Build: `mvn clean package`
2. Deploy `target/SpringMvcHelloWorld.war` to Tomcat 10+
3. Visit: `http://localhost:8080/SpringMvcHelloWorld/hello`
4. Try: `http://localhost:8080/SpringMvcHelloWorld/hello?name=Abiral`

## Tech Stack

- Spring MVC 6.2.10
- Java 17
- JSP + JSTL
- Maven
