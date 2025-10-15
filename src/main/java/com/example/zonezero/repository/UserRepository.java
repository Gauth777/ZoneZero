package com.example.zonezero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.zonezero.model.User;

/**
 * Spring Data repository for {@link User} entities. Provides basic CRUD
 * operations out of the box and can be extended for custom queries.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods can be declared here if needed
}