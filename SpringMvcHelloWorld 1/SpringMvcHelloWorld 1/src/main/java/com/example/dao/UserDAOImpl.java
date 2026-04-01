package com.example.dao;

import com.example.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<UserDTO> rowMapper = (rs, rowNum) -> new UserDTO(
            rs.getLong("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("password")
    );

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTable();
    }

    private void initTable() {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "full_name VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL, " +
                            "username VARCHAR(255) NOT NULL, " +
                            "password VARCHAR(255) NOT NULL" +
                            ")"
            );
            logger.info("User table initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize user table: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO save(UserDTO user) {
        String sql = "INSERT INTO users (full_name, email, username, password) VALUES (?, ?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getUsername());
                ps.setString(4, user.getPassword());
                return ps;
            }, keyHolder);

            Long generatedId = keyHolder.getKey().longValue();
            user.setId(generatedId);
            logger.info("User inserted successfully with id: {}", generatedId);
            return user;
        } catch (Exception e) {
            logger.error("Database error while saving user: {}", e.getMessage(), e);
            throw e;
        }
    }
 
    @Override
    public List<UserDTO> findAll() {
        try {
            return jdbcTemplate.query("SELECT * FROM users", rowMapper);
        } catch (Exception e) {
            logger.error("Database error while fetching all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO findById(Long id) {
        try {
            List<UserDTO> results = jdbcTemplate.query(
                    "SELECT * FROM users WHERE id = ?", rowMapper, id);

            if (results.isEmpty()) {
                return null;
            }
            return results.get(0);
        } catch (Exception e) {
            logger.error("Database error while fetching user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO update(Long id, UserDTO user) {
        try {
            int rowsChanged = jdbcTemplate.update(
                    "UPDATE users SET full_name = ?, email = ?, username = ?, password = ? WHERE id = ?",
                    user.getFullName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    id
            );

            if (rowsChanged == 0) {
                return null;
            }

            user.setId(id);
            logger.info("User updated successfully with id: {}", id);
            return user;
        } catch (Exception e) {
            logger.error("Database error while updating user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            int rowsDeleted = jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);

            if (rowsDeleted > 0) {
                logger.info("User deleted successfully with id: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Database error while deleting user with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}