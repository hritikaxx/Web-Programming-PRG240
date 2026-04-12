package com.example.dao;

import com.example.dto.UserDTO;
import java.util.List;

public interface UserDAO {

    UserDTO save(UserDTO user);

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    UserDTO update(Long id, UserDTO user);

    boolean delete(Long id);

    UserDTO findByUsername(String username);
}