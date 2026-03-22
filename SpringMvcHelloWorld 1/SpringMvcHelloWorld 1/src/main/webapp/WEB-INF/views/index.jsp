<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Paws - Pet Adoption</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">

  <style>
    body { font-family: 'Poppins', sans-serif; background-color: #f8f9fa; }
    .navbar { background-color: #C98A5B !important; }
    .navbar-brand, .navbar .nav-link { color: white !important; }
    .card { transition: transform 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .card:hover { transform: translateY(-5px); }
    .btn-adopt { background-color: #C98A5B; color: white; border: none; padding: 10px 30px; }
    .btn-adopt:hover { background-color: #a87249; color: white; }
    footer { background-color: #875e3e; color: white; text-align: center; padding: 30px 0; margin-top: 50px; }
  </style>
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar navbar-expand-lg shadow-sm">
    <div class="container">
      <a class="navbar-brand fw-bold fs-4" href="#">PAWS</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item"><a class="nav-link px-3" href="index.html">Home</a></li>
          <li class="nav-item dropdown">
            <a class="nav-link px-3 dropdown-toggle" href="#" data-bs-toggle="dropdown">Adopt</a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="index.html#pets">Browse Pets</a></li>
              <li><a class="dropdown-item" href="contactpage.html">Adoption Form</a></li>
            </ul>
          </li>
          <li class="nav-item"><a class="nav-link px-3" href="contactpage.html">Contact</a></li>
          <li class="nav-item dropdown">
            <a class="nav-link px-3 dropdown-toggle" href="#" data-bs-toggle="dropdown">Account</a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="login.html">Login</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/userregister">Register</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>


  <!-- HERO SECTION -->
  <section id="home" style="background-image: url('https://i.pinimg.com/1200x/d1/d1/81/d1d18156545ed373db49a08fa9896488.jpg'); background-size: cover; background-position: bottom; padding: 100px 0; text-align: center;">
    <div style="background-color: rgba(0,0,0,0.4); padding: 40px 20px;">
      <h1 style="color: white;">Paws Pet Adoption Center</h1>
      <p style="color: #f0e6dc; max-width: 600px; margin: 0 auto;">
        Welcome to <b>Paws</b>, a non-profit center dedicated to rescuing and rehoming abandoned animals.
        <br>Each pet has a unique personality and story.
        <br><br>Click on any pet to learn more about their age, breed, and temperament.
      </p>
    </div>
  </section>


  <!-- PETS SECTION -->
  <section class="py-5" id="pets">
    <div class="container">
      <h2 class="text-center mb-5">Pets Available for Adoption</h2>

      <div class="row g-4 justify-content-center">

        <!-- Max -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="max.html">
              <img src="https://i.pinimg.com/736x/8d/9e/79/8d9e79283188acabd27436f8ca640d30.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Max the Labrador">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Max</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 2 Years</li>
                <li><b>Breed:</b> Labrador</li>
                <li><b>Personality:</b> Playful, friendly, active</li>
              </ul>
              <a href="max.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

        <!-- Moon -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="moon.html">
              <img src="https://i.pinimg.com/1200x/c4/10/3e/c4103e72b3052ce73f2ce02c5793131f.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Moon the Pug">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Moon</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 5 Years</li>
                <li><b>Breed:</b> Pug</li>
                <li><b>Personality:</b> Friendly, shy, gentle</li>
              </ul>
              <a href="moon.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

        <!-- Tommy -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="tommy.html">
              <img src="https://i.pinimg.com/736x/29/f7/00/29f7007bdf91df2385e0394b60ed0568.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Tommy the dog">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Tommy</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 4 Years</li>
                <li><b>Breed:</b> Mixed</li>
                <li><b>Personality:</b> Friendly, loving</li>
              </ul>
              <a href="tommy.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

        <!-- Luna -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="luna.html">
              <img src="https://i.pinimg.com/736x/3d/08/f3/3d08f312d3572139716883cb4c4ba94f.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Luna the cat">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Luna</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 1 Year</li>
                <li><b>Breed:</b> Domestic Shorthair</li>
                <li><b>Personality:</b> Calm, affectionate</li>
              </ul>
              <a href="luna.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

        <!-- Bella -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="bella.html">
              <img src="https://i.pinimg.com/736x/49/4e/93/494e93198c2a1959772b05777fb2ae8f.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Bella the cat">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Bella</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 3 Years</li>
                <li><b>Breed:</b> Domestic Shorthair</li>
                <li><b>Personality:</b> Friendly, playful</li>
              </ul>
              <a href="bella.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

        <!-- Cookie -->
        <div class="col-12 col-md-6 col-lg-4 d-flex justify-content-center">
          <div class="card h-100 text-center border-0 rounded-3 overflow-hidden" style="max-width: 300px; width: 100%;">
            <a href="cookie.html">
              <img src="https://i.pinimg.com/1200x/b4/e1/3d/b4e13d421892a0e0023a7dde6dab3f31.jpg" class="card-img-top" style="width:100%; height:300px; object-fit:cover; object-position:center top; border-bottom: 3px solid #e8d5c2;" alt="Cookie the cat">
            </a>
            <div class="card-body d-flex flex-column p-3">
              <h5 class="card-title fw-semibold mb-1">Cookie</h5>
              <ul class="list-unstyled mb-2 small">
                <li><b>Age:</b> 1 Year</li>
                <li><b>Breed:</b> Domestic Shorthair</li>
                <li><b>Personality:</b> Playful, shy</li>
              </ul>
              <a href="cookie.html" class="btn btn-adopt mt-auto">View Details</a>
            </div>
          </div>
        </div>

      </div>
    </div>
  </section>


  <!-- INFO SECTION -->
  <section class="py-5">
    <div class="container">
      <div class="bg-white rounded-3 p-4 p-md-5 shadow-sm">
        <div class="row g-4">
          <div class="col-12 col-md-4">
            <h2>Our Services</h2>
            <ul>
              <li>Pet rescue and rehabilitation</li>
              <li>Vaccination and health checkups</li>
              <li>Adoption counseling</li>
              <li>Basic training guidance</li>
            </ul>
          </div>
          <div class="col-12 col-md-4">
            <h2>Adoption Process</h2>
            <p>Interested adopters can visit our center, meet the pets, and complete a short adoption form. Our team ensures a good match between pet and owner.</p>
          </div>
          <div class="col-12 col-md-4">
            <h2>Why Adopt From Paws?</h2>
            <ul>
              <li>Fully vaccinated pets</li>
              <li>Medical history provided</li>
              <li>Safe and caring environment</li>
              <li>Post-adoption support</li>
            </ul>
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