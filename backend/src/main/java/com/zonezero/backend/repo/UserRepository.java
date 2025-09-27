package com.zonezero.backend.repo;

import com.zonezero.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);
}
