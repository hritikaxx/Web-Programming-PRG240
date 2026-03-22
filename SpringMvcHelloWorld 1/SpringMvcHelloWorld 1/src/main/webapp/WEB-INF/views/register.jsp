<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register – PAWS</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">

  <style>
    .info-box {
      background-color: white;
      border: 1px solid #e8d5c2;
      border-radius: 12px;
      padding: 24px;
    }

    .info-box h4 {
      color: #C98A5B;
    }

    .form-control:focus {
      border-color: #C98A5B;
      box-shadow: none;
    }

    .form-check-input:checked {
      background-color: #C98A5B;
      border-color: #C98A5B;
    }

    a.link-brown {
      color: #C98A5B;
      text-decoration: none;
    }
    a.link-brown:hover {
      color: #a96e44;
      text-decoration: underline;
    }
  </style>
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar navbar-expand-lg">
    <div class="container">
      <a class="navbar-brand fw-bold fs-4" href="#">PAWS</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link px-3" href="${pageContext.request.contextPath}/">Home</a>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link px-3 dropdown-toggle" href="#" data-bs-toggle="dropdown">Adopt</a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/#pets">Browse Pets</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/adoption">Adoption Form</a></li>
            </ul>
          </li>
          <li class="nav-item">
            <a class="nav-link px-3" href="${pageContext.request.contextPath}/contact">Contact</a>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link px-3 dropdown-toggle" href="#" data-bs-toggle="dropdown">Account</a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/login">Login</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/register">Register</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>


  <!-- REGISTER SECTION -->
  <section class="py-5">
    <div class="container">

      <div class="text-center mb-5">
        <h1>Create an Account</h1>
        <p>Join PAWS and find your perfect companion</p>
      </div>

      <div class="row justify-content-center">
        <div class="col-lg-5 col-md-8">

          <div class="info-box">
            <h4 class="mb-4">Register</h4>

            <form>
              <div class="mb-3">
                <label class="form-label fw-semibold">Email Address</label>
                <input type="email" class="form-control" placeholder="Enter your email" required>
              </div>

              <div class="mb-4">
                <label class="form-label fw-semibold">Password</label>
                <input type="password" class="form-control" placeholder="Create a password" minlength="8" required>
              </div>

              <div class="d-grid mb-3">
                <button type="submit" class="btn btn-adopt">Create Account</button>
              </div>
            </form>

            <p class="text-center small mb-0">
              Already have an account?
              <a href="${pageContext.request.contextPath}/login" class="link-brown fw-semibold">Log in here</a>
            </p>
          </div>

        </div>
      </div>
    </div>
  </section>


  <!-- FOOTER -->
  <footer class="mt-5">
    <p><b>Opening Hours:</b> Sunday - Friday | 10:00 AM - 5:00 PM</p>
    <p class="mb-0">© 2026 Paws Pet Adoption. All rights reserved.</p>
  </footer>

  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
