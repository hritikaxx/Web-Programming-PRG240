package com.example.dao;

import com.example.dto.PetDTO;

import java.util.List;

/**
 * DAO (Data Access Object) Interface for Pet database operations.
 */
public interface PetDAO {

    // INSERT a new pet into the database and return it with the generated ID
    PetDTO save(PetDTO pet);

    // SELECT all pets from the database
    List<PetDTO> findAll();

    // SELECT one pet by their ID (returns null if not found)
    PetDTO findById(Long id);

    // UPDATE a pet's information (returns null if not found)
    PetDTO update(Long id, PetDTO pet);

    // DELETE a pet by their ID (returns true if deleted, false if not found)
    boolean delete(Long id);
}