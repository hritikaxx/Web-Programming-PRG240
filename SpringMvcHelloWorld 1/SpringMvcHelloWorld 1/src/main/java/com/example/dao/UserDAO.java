package com.example.dao;

import com.example.dto.UserDTO;

import java.util.List;

public interface UserDAO {

    // INSERT a new user into the database and return it with the generated ID
    UserDTO save(UserDTO user);

    // SELECT all users from the database
    List<UserDTO> findAll();

    // SELECT one user by their ID (returns null if not found)
    UserDTO findById(Long id);

    // UPDATE a user's information (returns null if not found)
    UserDTO update(Long id, UserDTO user);

    // DELETE a user by their ID (returns true if deleted, false if not found)
    boolean delete(Long id);
}

