package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * HelloController - handles HTTP requests and returns view names.
 *
 * KEY CONCEPT: @Controller
 * ========================
 * The @Controller annotation marks this class as a Spring MVC Controller.
 * Spring automatically detects it during component scanning (configured in dispatcher-servlet.xml).
 * A Controller's job is to:
 *   1. Receive the HTTP request
 *   2. Process it (call services, prepare data)
 *   3. Add data to the Model
 *   4. Return a View name
 *
 * KEY CONCEPT: Model
 * ==================
 * The Model is a container (like a Map) that carries data from the Controller to the View.
 * Controllers add attributes to the Model using model.addAttribute("key", value).
 * The View (JSP) can then access these attributes using ${key}.
 *
 * FULL REQUEST FLOW:
 * ==================
 * 1. Browser sends GET request to /hello
 * 2. Tomcat receives request, passes it to DispatcherServlet (configured in web.xml)
 * 3. DispatcherServlet consults HandlerMapping to find which Controller handles /hello
 * 4. DispatcherServlet calls HelloController.hello() method
 * 5. Controller adds data to Model, returns view name "hello"
 * 6. DispatcherServlet passes view name to ViewResolver
 * 7. ViewResolver resolves "hello" --> /WEB-INF/views/hello.jsp
 * 8. DispatcherServlet forwards Model data to the JSP view
 * 9. JSP renders HTML using the Model data and sends response back to browser
 */
@Controller
public class HelloController {

    /**
     * Handles GET requests to the root URL "/".
     * Redirects the user to the /hello page.
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    /**
     * Handles GET requests to "/hello".
     *
     * @param name  optional query parameter (e.g., /hello?name=Abiral)
     * @param model the Model object to pass data to the View
     * @return the view name "hello" (resolved to /WEB-INF/views/hello.jsp)
     */

    @GetMapping("/home")
    public String index() {
    return "index";
    }
}