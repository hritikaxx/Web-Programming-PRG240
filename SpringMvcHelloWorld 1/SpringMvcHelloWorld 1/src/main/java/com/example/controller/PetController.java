package com.example.controller;

import com.example.model.Pet;
import com.example.service.PetService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Pet operations.
 */
@Controller
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    @Autowired
    private PetService petService;

    // ==================== JSP VIEW ENDPOINTS ====================

    @GetMapping("/addPet")
    public String showAddPetPage(HttpSession session) {
        // Check if user is logged in
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "addPet";
    }

    // ==================== REST API ENDPOINTS ====================

    @PostMapping("/api/addPet")
    @ResponseBody
    public ResponseEntity<?> addPetApi(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("age") String ageStr,
            @RequestParam(value = "personality", required = false) String personality,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        // Check if user is logged in
        if (session.getAttribute("loggedInUser") == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "User not logged in");
            return ResponseEntity.status(401).body(response);
        }

        Map<String, Object> response = new HashMap<>();

        // Validate inputs
        Map<String, String> errors = new HashMap<>();
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name is required");
        }
        if (type == null || type.trim().isEmpty()) {
            errors.put("type", "Type is required");
        }
        int age = 0;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0) {
                errors.put("age", "Age must be positive");
            }
        } catch (NumberFormatException e) {
            errors.put("age", "Age must be a valid number");
        }

        if (!errors.isEmpty()) {
            response.put("status", "error");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        // Handle image upload
        byte[] imageData = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageData = image.getBytes();
            } catch (IOException e) {
                logger.error("Failed to read image data", e);
                response.put("status", "error");
                response.put("message", "Failed to read image data");
                return ResponseEntity.status(500).body(response);
            }
        }

        try {
            // Add pet
            Pet pet = petService.addPet(name.trim(), type.trim(), age, personality != null ? personality.trim() : null, imageData);

            response.put("status", "success");
            response.put("pet", pet);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding pet", e);
            response.put("status", "error");
            response.put("message", "Failed to add pet");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/api/pets")
    @ResponseBody
    public ResponseEntity<?> getAllPetsApi() {
        try {
            List<Pet> pets = petService.getAllPets();
            List<Map<String, Object>> petMaps = new ArrayList<>();
            for (Pet pet : pets) {
                Map<String, Object> petMap = new HashMap<>();
                petMap.put("id", pet.getId());
                petMap.put("name", pet.getName());
                petMap.put("type", pet.getType());
                petMap.put("age", pet.getAge());
                petMap.put("personality", pet.getPersonality());
                if (pet.getImage() != null) {
                    petMap.put("imageUrl", "/api/pets/" + pet.getId() + "/image");
                } else {
                    petMap.put("imageUrl", null);
                }
                petMaps.add(petMap);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("pets", petMaps);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving pets", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to retrieve pets");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/api/pets/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> getPetImage(@PathVariable("id") Long id) {
        Pet pet = petService.getPetById(id);
        if (pet == null || pet.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        // Determine content type based on image data (simple check)
        String contentType = "image/jpeg"; // default
        if (pet.getImage().length > 0) {
            if (pet.getImage()[0] == (byte) 0x89 && pet.getImage()[1] == (byte) 0x50) {
                contentType = "image/png";
            } else if (pet.getImage()[0] == (byte) 0xFF && pet.getImage()[1] == (byte) 0xD8) {
                contentType = "image/jpeg";
            } else if (pet.getImage()[0] == (byte) 0x47 && pet.getImage()[1] == (byte) 0x49) {
                contentType = "image/gif";
            }
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .body(pet.getImage());
    }

    @DeleteMapping("/api/pets/{id}")
    @ResponseBody
    public ResponseEntity<?> deletePetApi(@PathVariable("id") Long id, HttpSession session) {
        Pet pet = petService.getPetById(id);
        if (pet == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Pet not found");
            return ResponseEntity.status(404).body(response);
        }

        boolean deleted = petService.deletePet(id);
        if (deleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Pet deleted successfully");
            return ResponseEntity.ok(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Failed to delete pet");
        return ResponseEntity.status(500).body(response);
    }
}