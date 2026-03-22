<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register – PAWS</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

<style>
body{
    font-family: Arial, sans-serif;
    background:#fafafa;
}

.navbar{
    background:#fff;
    border-bottom:1px solid #eee;
}

.info-box{
    background:white;
    padding:25px;
    border-radius:10px;
    border:1px solid #eee;
}

.btn-adopt{
    background:#C98A5B;
    color:white;
    border:none;
}

.btn-adopt:hover{
    background:#a96e44;
}

footer{
    text-align:center;
    padding:20px;
    margin-top:40px;
    background:#f8f8f8;
}
</style>

</head>

<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg">
<div class="container">

<a class="navbar-brand fw-bold" href="index">PAWS</a>

<ul class="navbar-nav ms-auto">
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
<li class="nav-item"><a class="nav-link" href="#">Adopt</a></li>
<li class="nav-item"><a class="nav-link" href="#">Contact</a></li>
<li class="nav-item"><a class="nav-link" href="login">Login</a></li>
</ul>

</div>
</nav>

<!-- REGISTER FORM -->
<section class="py-5">
<div class="container">

<div class="text-center mb-4">
<h2>Create an Account</h2>
<p>Join PAWS and find your perfect companion</p>
</div>

<div class="row justify-content-center">
<div class="col-md-6">

<div class="info-box">

<form action="registerUser" method="post">

<div class="mb-3">
<label class="form-label">Full Name</label>
<input type="text" class="form-control" name="fullName" required>
</div>

<div class="mb-3">
<label class="form-label">Email</label>
<input type="email" class="form-control" name="email" required>
</div>

<div class="mb-3">
<label class="form-label">Phone</label>
<input type="text" class="form-control" name="phone" required>
</div>

<div class="mb-3">
<label class="form-label">Password</label>
<input type="password" class="form-control" name="password" required>
</div>

<div class="mb-3">
<label class="form-label">Confirm Password</label>
<input type="password" class="form-control" name="confirmPassword" required>
</div>

<div class="d-grid">
<button type="submit" class="btn btn-adopt">Create Account</button>
</div>

</form>

<p class="text-center mt-3">
Already have an account? <a href="login">Login</a>
</p>

</div>
</div>
</div>

</div>
</section>

<!-- FOOTER -->
<footer>
<p><b>Opening Hours:</b> Sunday – Friday | 10:00 AM – 5:00 PM</p>
<p>© 2026 PAWS Pet Adoption</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>