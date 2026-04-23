<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Pet</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">
    <style>
        body { background-color: #F6EFE7; }
        .card { max-width: 560px; margin: 36px auto; padding: 32px; border-radius: 20px; box-shadow: 0 20px 50px rgba(0,0,0,0.08); border: 1px solid rgba(140, 118, 92, 0.12); background-color: rgba(255,255,255,0.98); }
        .card h1 { text-align: center; color: #4a2c2a; margin-bottom: 24px; }
        label { display: block; margin-top: 16px; font-weight: 600; }
        input[type="text"], input[type="number"], input[type="file"] { width: 100%; padding: 12px 14px; border: 1px solid #d8d3ca; border-radius: 10px; margin-top: 8px; background-color: #fff; }
        button { width: 100%; background-color: #C98A5B; color: white; border: none; padding: 16px; border-radius: 14px; font-size: 1rem; cursor: pointer; margin-top: 24px; }
        button:hover { background-color: #a96e44; }
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

            <label for="type">Pet Type</label>
            <input type="text" id="type" name="type">
            <div class="error-message" id="type-error"></div>

            <label for="age">Age</label>
            <input type="number" id="age" name="age" min="0">
            <div class="error-message" id="age-error"></div>

            <label for="image">Pet Image</label>
            <input type="file" id="image" name="image" accept="image/*">
            <div class="error-message" id="image-error"></div>
            <div id="imagePreview" style="margin-bottom: 12px; display: none;">
                <img id="previewImg" src="" alt="Preview" style="max-width: 150px; max-height: 150px; border-radius: 8px; border: 1px solid #ddd;">
            </div>

            <button type="submit">Add Pet</button>
        </form>
    </div>

    <script>
        // Image preview when a file is selected
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

            // Build FormData to support file upload
            const formData = new FormData();
            formData.append('name', document.getElementById('name').value);
            formData.append('type', document.getElementById('type').value);
            formData.append('age', document.getElementById('age').value);

            const imageInput = document.getElementById('image');
            if (imageInput.files.length > 0) {
                formData.append('image', imageInput.files[0]);
            }

            // Send multipart form data to the API endpoint
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
                    // Redirect to homepage
                    window.location.href = '${pageContext.request.contextPath}/';
                } else if (result.status === 'error') {
                    const errorBanner = document.getElementById('errorBanner');
                    errorBanner.textContent = 'Please fix the errors below.';
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

                    if (result.message) {
                        const errorBanner = document.getElementById('errorBanner');
                        errorBanner.textContent = result.message;
                        errorBanner.style.display = 'block';
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