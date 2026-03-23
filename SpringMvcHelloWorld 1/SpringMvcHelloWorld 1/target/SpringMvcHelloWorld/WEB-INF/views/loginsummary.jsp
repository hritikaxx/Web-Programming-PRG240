<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login Success – PAWS</title>

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

.summary-card{
    background:white;
    padding:30px;
    border-radius:12px;
    border:1px solid #eee;
    box-shadow:0 8px 20px rgba(0,0,0,0.05);
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

<a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">PAWS</a>

<ul class="navbar-nav ms-auto">
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
<li class="nav-item"><a class="nav-link" href="#">Adopt</a></li>
<li class="nav-item"><a class="nav-link" href="#">Contact</a></li>
</ul>

</div>
</nav>

<section class="py-5">
<div class="container">

<div class="text-center mb-4">
<h2>Login Successful!</h2>
<p>Welcome back, ${username}!</p>
</div>

<div class="row justify-content-center">
<div class="col-md-6">

<div class="summary-card">

<h5 class="mb-3">Your Details</h5>

<p><strong>Username:</strong> ${username}</p>

<div class="d-grid gap-2 mt-4">
<a href="${pageContext.request.contextPath}/" class="btn btn-adopt">Go to Home</a>
</div>

</div>

</div>
</div>

</div>
</section>

<footer>
<p><b>Opening Hours:</b> Sunday – Friday | 10:00 AM – 5:00 PM</p>
<p>© 2026 PAWS Pet Adoption</p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>