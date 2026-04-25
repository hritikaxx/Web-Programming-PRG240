package com.example.service;

import com.example.dao.PetDAO;
import com.example.dto.PetDTO;
import com.example.model.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * The Service layer for Pet operations.
 */
@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetDAO petDAO;

    public PetService(PetDAO petDAO) {
        this.petDAO = petDAO;
    }

    /**
     * Add a new pet.
     */
    public Pet addPet(String name, String type, int age, String personality, byte[] image, Long userId) {
        logger.info("Adding new pet: name={}, type={}, age={}, userId={}", name, type, age, userId);

        PetDTO dto = new PetDTO(name, type, age, personality, image, userId);
        PetDTO saved = petDAO.save(dto);

        logger.info("Pet added successfully with id: {}", saved.getId());
        return toPet(saved);
    }

    /**
     * Get all pets.
     */
    public List<Pet> getAllPets() {
        List<PetDTO> dtos = petDAO.findAll();
        logger.info("Retrieved {} pets from database", dtos.size());

        List<Pet> pets = new ArrayList<>();
        for (PetDTO dto : dtos) {
            pets.add(toPet(dto));
        }
        return pets;
    }

    /**
     * Get one pet by ID.
     */
    public Pet getPetById(Long id) {
        PetDTO dto = petDAO.findById(id);
        if (dto == null) {
            logger.info("Pet not found with id: {}", id);
            return null;
        }
        return toPet(dto);
    }

    /**
     * Update a pet.
     */
    public Pet updatePet(Long id, Pet updated) {
        PetDTO dto = new PetDTO(
                updated.getName(),
                updated.getType(),
                updated.getAge(),
                updated.getPersonality(),
                updated.getImage(),
                updated.getUserId()
        );

        PetDTO result = petDAO.update(id, dto);
        if (result == null) {
            logger.info("Cannot update - pet not found with id: {}", id);
            return null;
        }
        return toPet(result);
    }

    /**
     * Delete a pet.
     */
    public boolean deletePet(Long id) {
        boolean deleted = petDAO.delete(id);
        if (!deleted) {
            logger.info("Cannot delete - pet not found with id: {}", id);
        }
        return deleted;
    }

    /**
     * Helper method: Convert DTO to Pet model.
     */
    private Pet toPet(PetDTO dto) {
        Pet pet = new Pet(dto.getName(), dto.getType(), dto.getAge(), dto.getPersonality(), dto.getImage(), dto.getUserId());
        pet.setId(dto.getId());
        return pet;
    }
}