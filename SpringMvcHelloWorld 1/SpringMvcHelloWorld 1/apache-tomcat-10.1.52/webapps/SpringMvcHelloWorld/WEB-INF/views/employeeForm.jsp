<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .error-message {
            color: #d32f2f;
            font-size: 0.85em;
            margin-top: 2px;
            margin-bottom: 8px;
            display: none;
        }
        .input-error {
            border-color: #d32f2f !important;
        }
        .success-banner {
            background-color: #e8f5e9;
            color: #2e7d32;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 16px;
            display: none;
        }
        .error-banner {
            background-color: #ffebee;
            color: #d32f2f;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 16px;
            display: none;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Employee Registration</h1>

        <div id="successBanner" class="success-banner"></div>
        <div id="errorBanner" class="error-banner"></div>

        <form id="registrationForm">
            <label for="name">Name</label>
            <input type="text" id="name" name="name">
            <div class="error-message" id="name-error"></div>

            <label for="email">Email</label>
            <input type="email" id="email" name="email">
            <div class="error-message" id="email-error"></div>

            <label for="contactNumber">Contact Number</label>
            <input type="tel" id="contactNumber" name="contactNumber">
            <div class="error-message" id="contactNumber-error"></div>

            <label for="position">Position</label>
            <input type="text" id="position" name="position">
            <div class="error-message" id="position-error"></div>

            <button type="submit">Submit</button>
        </form>
    </div>

    <script>
        document.getElementById('registrationForm').addEventListener('submit', function(e) {
            e.preventDefault();

            // Clear previous errors
            document.querySelectorAll('.error-message').forEach(el => {
                el.style.display = 'none';
                el.textContent = '';
            });
            document.querySelectorAll('.input-error').forEach(el => {
                el.classList.remove('input-error');
            });
            document.getElementById('successBanner').style.display = 'none';
            document.getElementById('errorBanner').style.display = 'none';

            // Build JSON from form fields
            const data = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                contactNumber: document.getElementById('contactNumber').value,
                position: document.getElementById('position').value
            };

            // Send JSON to the API endpoint
            fetch('${pageContext.request.contextPath}/api/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(result => {
                if (result.status === 'success') {
                    // Redirect to summary page with data in query params
                    window.location.href = '${pageContext.request.contextPath}/register/summary'
                        + '?name=' + encodeURIComponent(result.employee.name)
                        + '&email=' + encodeURIComponent(result.employee.email)
                        + '&contactNumber=' + encodeURIComponent(result.employee.contactNumber)
                        + '&position=' + encodeURIComponent(result.employee.position);
                } else if (result.status === 'error') {
                    // Show validation errors from the server
                    const errorBanner = document.getElementById('errorBanner');
                    errorBanner.textContent = 'Please fix the errors below.';
                    errorBanner.style.display = 'block';

                    for (const [field, message] of Object.entries(result.errors)) {
                        const errorDiv = document.getElementById(field + '-error');
                        const inputEl = document.getElementById(field);
                        if (errorDiv) {
                            errorDiv.textContent = message;
                            errorDiv.style.display = 'block';
                        }
                        if (inputEl) {
                            inputEl.classList.add('input-error');
                        }
                    }
                }
            })
            .catch(err => {
                const errorBanner = document.getElementById('errorBanner');
                errorBanner.textContent = 'An unexpected error occurred. Please try again.';
                errorBanner.style.display = 'block';
            });
        });
    </script>
</body>
</html>
