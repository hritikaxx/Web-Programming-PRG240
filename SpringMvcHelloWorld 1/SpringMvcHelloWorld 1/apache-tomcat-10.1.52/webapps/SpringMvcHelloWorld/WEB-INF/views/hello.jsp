<%--
    hello.jsp - The View (the "V" in MVC)
    ======================================
    This JSP file is the VIEW that renders the HTML sent back to the browser.
    The ViewResolver maps the name "hello" to this file at /WEB-INF/views/hello.jsp.

    KEY CONCEPT: View
    =================
    The View is responsible ONLY for presentation (displaying data).
    It receives data from the Model (via the Controller) and renders it as HTML.
    The View does NOT contain business logic - it only displays what the Controller provides.

    The ${message.text} syntax is an Expression Language (EL) expression.
    It accesses the "message" attribute that the Controller added to the Model,
    and calls getMessage().getText() to get the text value.

    Flow: Controller adds "message" to Model --> JSP accesses it via ${message.text}
--%>
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
        h1 {
            color: #2e7d32;
            margin-bottom: 10px;
        }
        p.greeting {
            font-size: 1.4rem;
            color: #555;
            margin: 20px 0;
        }
        .info {
            font-size: 0.9rem;
            color: #888;
            margin-top: 20px;
            line-height: 1.6;
        }
        a {
            color: #2e7d32;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
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
