<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Summary</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="card">
        <h1>Registration Summary</h1>
        <table>
            <tr>
                <th>Name</th>
                <td>${employee.name}</td>
            </tr>
            <tr>
                <th>Email</th>
                <td>${employee.email}</td>
            </tr>
            <tr>
                <th>Contact Number</th>
                <td>${employee.contactNumber}</td>
            </tr>
            <tr>
                <th>Position</th>
                <td>${employee.position}</td>
            </tr>
        </table>
        <a class="back-link" href="${pageContext.request.contextPath}/register">Register Another Employee</a>
    </div>
</body>
</html>
