<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring MVC Hello World</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="card">
        <h1>Spring MVC Hello World</h1>
        <p class="greeting">${greeting}</p>
        <p class="info">
            Try: <a href="${pageContext.request.contextPath}/hello?name=Abiral">/hello?name=Abiral</a>
        </p>
    </div>
</body>
</html>
