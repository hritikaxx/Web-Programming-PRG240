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

        <div style="display:flex; justify-content:space-between; align-items:center;">
            <h1>Employee Registration</h1>
            <a href="${pageContext.request.contextPath}/logout-home">Logout</a>
        </div>

        <div id="errorBanner" style="color:red; display:none;"></div>

        <form id="registrationForm">
            <label>Name</label>
            <input type="text" id="name">

            <label>Email</label>
            <input type="email" id="email">

            <label>Contact Number</label>
            <input type="tel" id="contactNumber">

            <label>Position</label>
            <input type="text" id="position">

            <button type="submit">Submit</button>
        </form>
    </div>

    <script>
        document.getElementById('registrationForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const data = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                contactNumber: document.getElementById('contactNumber').value,
                position: document.getElementById('position').value
            };

            fetch('${pageContext.request.contextPath}/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(result => {
                if (result.status === 'success') {
                    window.location.href = '${pageContext.request.contextPath}/register/summary'
                        + '?name=' + encodeURIComponent(result.employee.name)
                        + '&email=' + encodeURIComponent(result.employee.email)
                        + '&contactNumber=' + encodeURIComponent(result.employee.contactNumber)
                        + '&position=' + encodeURIComponent(result.employee.position);
                } else {
                    document.getElementById('errorBanner').textContent = 'Error: Please check your inputs.';
                    document.getElementById('errorBanner').style.display = 'block';
                }
            })
            .catch(() => {
                document.getElementById('errorBanner').textContent = 'Server error. Please try again.';
                document.getElementById('errorBanner').style.display = 'block';
            });
        });
    </script>
</body>
</html>