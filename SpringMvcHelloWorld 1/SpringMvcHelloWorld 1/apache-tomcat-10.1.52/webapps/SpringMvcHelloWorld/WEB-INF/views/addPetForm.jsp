<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>PAWS - Add New Pet</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/paws.css">
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
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <!-- PAGE CONTENT -->
  <section class="py-5">
    <div class="container">

      <div class="row justify-content-center">
        <div class="col-12 col-md-8 col-lg-6">

          <!-- Back link -->
          <a href="${pageContext.request.contextPath}/" class="text-decoration-none mb-3 d-inline-block"
             style="color: #C98A5B; font-weight: 600;">
            ← Back to Home
          </a>

          <div class="bg-white rounded-3 p-4 p-md-5 shadow-sm">

            <h2 class="text-center mb-1">Add New Pet</h2>
            <p class="text-center text-muted mb-4" style="font-size: 14px;">
              Fill in the details to list a pet for adoption
            </p>

            <form:form action="${pageContext.request.contextPath}/addPet"
                       method="post" modelAttribute="pet">

              <!-- Name & Age -->
              <div class="row g-3 mb-3">
                <div class="col-12 col-sm-6">
                  <label class="form-label fw-semibold">Pet Name *</label>
                  <form:input path="name" cssClass="form-control" placeholder="e.g. Buddy" required="true"/>
                </div>
                <div class="col-12 col-sm-6">
                  <label class="form-label fw-semibold">Age *</label>
                  <form:input path="age" cssClass="form-control" placeholder="e.g. 2 Years" required="true"/>
                </div>
              </div>

              <!-- Breed & Gender -->
              <div class="row g-3 mb-3">
                <div class="col-12 col-sm-6">
                  <label class="form-label fw-semibold">Breed *</label>
                  <form:input path="breed" cssClass="form-control" placeholder="e.g. Labrador" required="true"/>
                </div>
                <div class="col-12 col-sm-6">
                  <label class="form-label fw-semibold">Gender</label>
                  <form:select path="gender" cssClass="form-select">
                    <form:option value="Male">Male</form:option>
                    <form:option value="Female">Female</form:option>
                  </form:select>
                </div>
              </div>

              <!-- Personality -->
              <div class="mb-3">
                <label class="form-label fw-semibold">Personality *</label>
                <form:input path="personality" cssClass="form-control"
                            placeholder="e.g. Playful, friendly, active" required="true"/>
              </div>

              <!-- Price -->
              <div class="mb-3">
                <label class="form-label fw-semibold">Price</label>
                <form:input path="price" cssClass="form-control" placeholder="e.g. Rs. 20,000"/>
              </div>

              <!-- Image URL -->
              <div class="mb-3">
                <label class="form-label fw-semibold">Pet Photo URL</label>
                <form:input path="imageUrl" cssClass="form-control"
                            placeholder="Paste an image link (https://...)"/>
                <div class="form-text">Paste a direct link to the pet's photo.</div>
              </div>

              <!-- Description -->
              <div class="mb-3">
                <label class="form-label fw-semibold">Description</label>
                <form:textarea path="description" cssClass="form-control" rows="3"
                               placeholder="Tell us about this pet's story and habits…"/>
              </div>

              <!-- Available checkbox -->
              <div class="mb-4 form-check">
                <form:checkbox path="available" cssClass="form-check-input" id="available"/>
                <label class="form-check-label" for="available">Available for adoption</label>
              </div>

              <!-- Submit -->
              <div class="d-grid">
                <button type="submit" class="btn btn-adopt" style="font-size: 16px; padding: 12px;">
                  🐾 Add Pet to PAWS
                </button>
              </div>

            </form:form>
          </div>

        </div>
      </div>

    </div>
  </section>

  <!-- FOOTER -->
  <footer>
    <p><b>Opening Hours:</b> Sunday - Friday | 10:00 AM - 5:00 PM</p>
    <p>© 2026 Paws Pet Adoption. All rights reserved.</p>
  </footer>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>