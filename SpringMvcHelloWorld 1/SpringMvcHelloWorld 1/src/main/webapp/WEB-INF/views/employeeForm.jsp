<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="card">
        <h1>Employee Registration</h1>
        <form action="${pageContext.request.contextPath}/register" method="post">
            <label for="name">Name</label>
            <input type="text" id="name" name="name" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>

            <label for="contactNumber">Contact Number</label>
            <input type="tel" id="contactNumber" name="contactNumber" required>

            <label for="position">Position</label>
            <input type="text" id="position" name="position" required>

            <button type="submit">Submit</button>
        </form>
    </div>
</body>
</html>
