<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Registration Summary – PAWS</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

<style>
body{
    font-family: Arial, sans-serif;
    background:#fafafa;
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
</style>
</head>

<body>

<div class="container py-5">

<div class="text-center mb-4">
<h2>Registration Successful!</h2>
<p>Welcome to PAWS</p>
</div>

<div class="row justify-content-center">
<div class="col-md-6">

<div class="summary-card">

<h5 class="mb-3">Your Details</h5>

<table class="table">
<tr>
<th>Full Name</th>
<td>${user.fullName}</td>
</tr>

<tr>
<th>Email</th>
<td>${user.email}</td>
</tr>

<tr>
<th>Phone</th>
<td>${user.phone}</td>
</tr>
</table>

<div class="d-grid gap-2 mt-4">
<a href="login" class="btn btn-adopt">Go to Login</a>

<a href="userregister" class="btn btn-outline-secondary">Register Another User</a>
</div>

</div>

</div>
</div>

</div>

</body>
</html>