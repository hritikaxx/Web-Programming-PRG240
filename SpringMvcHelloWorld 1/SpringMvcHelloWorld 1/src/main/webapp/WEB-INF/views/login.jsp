<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login – PAWS</title>

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

<a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">PAWS</a>

<ul class="navbar-nav ms-auto">
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/addPet">Add Pet</a></li>
<c:if test="${empty sessionScope.loggedInUser}">
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a></li>
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/userregister">Register</a></li>
</c:if>
<c:if test="${not empty sessionScope.loggedInUser}">
<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/logout">Logout</a></li>
</c:if>
</ul>

</div>
</nav>

<section class="py-5">
<div class="container">

<div class="text-center mb-4">
<h2>Login to Your Account</h2>
<p>Welcome back! Please enter your details.</p>
</div>

<div class="row justify-content-center">
<div class="col-md-6">

<div class="info-box">

<% if (request.getAttribute("errorMessage") != null) { %>
    <div class="alert alert-danger text-center">
        <%= request.getAttribute("errorMessage") %>
    </div>
<% } %>

<form action="${pageContext.request.contextPath}/loginUser" method="post">
<input type="hidden" name="redirectAfterLogin" value="${redirectAfterLogin}" />

<div class="mb-3">
<label class="form-label">Username</label>
<input type="text" class="form-control" name="username" required>
</div>

<div class="mb-3">
<label class="form-label">Password</label>
<input type="password" class="form-control" name="password" required>
</div>

<div class="d-grid">
<button type="submit" class="btn btn-adopt">Login</button>
</div>

</form>

<p class="text-center mt-3">
Don't have an account? <a href="userregister">Register here</a>
</p>

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