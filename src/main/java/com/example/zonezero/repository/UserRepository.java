package com.example.zonezero.repository;

import com.example.zonezero.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for User entity.
 * Extends JpaRepository to provide CRUD operations automatically.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom finder method
    Optional<User> findByEmail(String email);
}
