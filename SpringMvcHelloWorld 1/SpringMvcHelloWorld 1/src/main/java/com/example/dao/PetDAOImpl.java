package com.example.dao;

import com.example.dto.PetDTO;
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

/**
 * This class handles all database operations for pets.
 * It uses Spring's JdbcTemplate to run SQL queries against the H2 database.
 */
@Repository
public class PetDAOImpl implements PetDAO {

    private static final Logger logger = LoggerFactory.getLogger(PetDAOImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PetDTO> rowMapper = (rs, rowNum) -> new PetDTO(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getInt("age"),
            rs.getString("personality"),
            rs.getBytes("image"),
            rs.getLong("user_id")
    );

    public PetDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTable();
    }

    private void initTable() {
        try {
            // Create table if it doesn't exist (don't drop to preserve data)
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS pets (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "type VARCHAR(255) NOT NULL, " +
                            "age INT NOT NULL, " +
                            "personality VARCHAR(255), " +
                            "image BLOB, " +
                            "user_id BIGINT NOT NULL, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id)" +
                            ")"
            );
            logger.info("Pet table initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize pet table: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public PetDTO save(PetDTO pet) {
        String sql = "INSERT INTO pets (name, type, age, personality, image, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pet.getName());
            ps.setString(2, pet.getType());
            ps.setInt(3, pet.getAge());
            ps.setString(4, pet.getPersonality());
            ps.setBytes(5, pet.getImage());
            ps.setLong(6, pet.getUserId());
            return ps;
        }, keyHolder);

        // Get the auto-generated ID
        Number key = keyHolder.getKey();
        if (key != null) {
            pet.setId(key.longValue());
        }

        logger.info("Pet saved with id: {}", pet.getId());
        return pet;
    }

    @Override
    public List<PetDTO> findAll() {
        String sql = "SELECT id, name, type, age, personality, image, user_id FROM pets";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public PetDTO findById(Long id) {
        String sql = "SELECT id, name, type, age, personality, image, user_id FROM pets WHERE id = ?";
        List<PetDTO> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public PetDTO update(Long id, PetDTO pet) {
        String sql = "UPDATE pets SET name = ?, type = ?, age = ?, personality = ?, image = ?, user_id = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, pet.getName(), pet.getType(), pet.getAge(), pet.getPersonality(), pet.getImage(), pet.getUserId(), id);

        if (rowsAffected == 0) {
            logger.info("No pet found with id: {}", id);
            return null;
        }

        pet.setId(id);
        logger.info("Pet updated with id: {}", id);
        return pet;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM pets WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        boolean deleted = rowsAffected > 0;
        if (deleted) {
            logger.info("Pet deleted with id: {}", id);
        } else {
            logger.info("No pet found with id: {}", id);
        }
        return deleted;
    }
}