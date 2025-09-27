package com.zonezero.backend.service.impl;

import com.zonezero.backend.entity.User;
import com.zonezero.backend.repo.UserRepository;
import com.zonezero.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository repo;

  public UserServiceImpl(UserRepository repo) {
    this.repo = repo;
  }

  @Override
  public User upsert(String username, String email, String phone, String region) {
    return repo.findByEmail(email).map(u -> {
      u.setUsername(username);
      u.setPhone(phone);
      u.setRegion(region);
      return repo.save(u);
    }).orElseGet(() -> {
      User u = new User();
      u.setUsername(username);
      u.setEmail(email);
      u.setPhone(phone);
      u.setRegion(region);
      u.setCreatedAt(Instant.now());
      return repo.save(u);
    });
  }

  @Override
  public User getByEmail(String email) {
    return repo.findByEmail(email).orElse(null);
  }
}

