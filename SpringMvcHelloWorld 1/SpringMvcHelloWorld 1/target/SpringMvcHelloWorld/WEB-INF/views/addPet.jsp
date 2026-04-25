<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Pet</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #F6EFE7; }
        .card { max-width: 560px; margin: 36px auto; padding: 32px; border-radius: 20px; box-shadow: 0 20px 50px rgba(0,0,0,0.08); border: 1px solid rgba(140, 118, 92, 0.12); background-color: rgba(255,255,255,0.98); }
        .card h1 { text-align: center; color: #4a2c2a; margin-bottom: 24px; }
        label { display: block; margin-top: 16px; font-weight: 600; }
        input[type="text"], input[type="number"], input[type="file"], select { width: 100%; padding: 12px 14px; border: 1px solid #d8d3ca; border-radius: 10px; margin-top: 8px; background-color: #fff; box-sizing: border-box; }
        .age-row { display: flex; gap: 10px; margin-top: 8px; }
        .age-row input[type="number"] { margin-top: 0; flex: 1; }
        .age-row select { margin-top: 0; flex: 1; }
        button { width: 100%; background-color: #C98A5B; color: white; border: none; padding: 16px; border-radius: 14px; font-size: 1rem; cursor: pointer; margin-top: 24px; }
        button:hover { background-color: #a96e44; }
        .error-message { color: #d32f2f; font-size: 0.85em; margin-top: 2px; margin-bottom: 8px; display: none; }
        .input-error { border-color: #d32f2f !important; }
        .success-banner { background-color: #e8f5e9; color: #2e7d32; padding: 12px; border-radius: 6px; margin-bottom: 16px; display: none; }
        .error-banner { background-color: #ffebee; color: #d32f2f; padding: 12px; border-radius: 6px; margin-bottom: 16px; display: none; }
    </style>
</head>
<body>
    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand fw-bold fs-4" href="${pageContext.request.contextPath}/">PAWS</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/addPet">Add Pet</a>
                    </li>
                    <c:if test="${empty sessionScope.loggedInUser}">
                        <li class="nav-item">
                            <a class="nav-link px-3" href="${pageContext.request.contextPath}/login">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link px-3" href="${pageContext.request.contextPath}/userregister">Register</a>
                        </li>
                    </c:if>
                    <c:if test="${not empty sessionScope.loggedInUser}">
                        <li class="nav-item">
                            <a class="nav-link px-3" href="${pageContext.request.contextPath}/logout">Logout</a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>

    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
            <span style="font-size: 0.9rem; color: #555;">Logged in as <strong>${sessionScope.loggedInUser}</strong></span>
            <a href="${pageContext.request.contextPath}/logout" style="font-size: 0.9rem; color: #d32f2f;">Logout</a>
        </div>

        <h1>Add New Pet</h1>

        <div id="successBanner" class="success-banner"></div>
        <div id="errorBanner" class="error-banner"></div>

        <form id="addPetForm" enctype="multipart/form-data">
            <label for="name">Pet Name</label>
            <input type="text" id="name" name="name">
            <div class="error-message" id="name-error"></div>

            <label>Age</label>
            <div class="age-row">
                <input type="number" id="ageValue" name="ageValue" min="0" placeholder="e.g. 3">
                <select id="ageUnit" name="ageUnit">
                    <option value="years">Years</option>
                    <option value="months">Months</option>
                </select>
            </div>
            <div class="error-message" id="age-error"></div>

            <label for="breed">Breed</label>
            <input type="text" id="breed" name="breed">
            <div class="error-message" id="breed-error"></div>

            <label for="personality">Personality</label>
            <input type="text" id="personality" name="personality">
            <div class="error-message" id="personality-error"></div>

            <label for="image">Pet Image</label>
            <input type="file" id="image" name="image" accept="image/*">
            <div class="error-message" id="image-error"></div>
            <div id="imagePreview" style="margin-bottom: 12px; display: none;">
                <img id="previewImg" src="" alt="Preview" style="max-width: 150px; max-height: 150px; border-radius: 8px; border: 1px solid #ddd;">
            </div>

            <button type="submit">Add Pet</button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('image').addEventListener('change', function(e) {
            const file = e.target.files[0];
            const preview = document.getElementById('imagePreview');
            const previewImg = document.getElementById('previewImg');
            if (file) {
                const reader = new FileReader();
                reader.onload = function(ev) {
                    previewImg.src = ev.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                preview.style.display = 'none';
            }
        });

        document.getElementById('addPetForm').addEventListener('submit', function(e) {
            e.preventDefault();

            document.querySelectorAll('.error-message').forEach(el => {
                el.style.display = 'none';
                el.textContent = '';
            });
            document.querySelectorAll('.input-error').forEach(el => {
                el.classList.remove('input-error');
            });
            document.getElementById('successBanner').style.display = 'none';
            document.getElementById('errorBanner').style.display = 'none';

            const ageValue = parseInt(document.getElementById('ageValue').value);
            const ageUnit = document.getElementById('ageUnit').value;

            if (isNaN(ageValue) || ageValue < 0) {
                const errorDiv = document.getElementById('age-error');
                errorDiv.textContent = 'Please enter a valid age.';
                errorDiv.style.display = 'block';
                document.getElementById('ageValue').classList.add('input-error');
                return;
            }

            const ageInMonths = ageUnit === 'years' ? ageValue * 12 : ageValue;

            const formData = new FormData();
            formData.append('name', document.getElementById('name').value);
            formData.append('type', document.getElementById('breed').value);
            formData.append('age', ageInMonths);
            formData.append('personality', document.getElementById('personality').value);

            const imageInput = document.getElementById('image');
            if (imageInput.files.length > 0) {
                formData.append('image', imageInput.files[0]);
            }

            fetch('${pageContext.request.contextPath}/api/addPet', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    return response.json().catch(() => {
                        throw new Error('Server error: ' + response.status);
                    });
                }
                return response.json();
            })
            .then(result => {
                if (result.status === 'success') {
                    window.location.href = '${pageContext.request.contextPath}/';
                } else if (result.status === 'error') {
                    const errorBanner = document.getElementById('errorBanner');
                    errorBanner.textContent = result.message || 'Please fix the errors below.';
                    errorBanner.style.display = 'block';

                    for (const [field, message] of Object.entries(result.errors || {})) {
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
                console.error(err);
            });
        });
    </script>
</body>
</html>