package com.zonezero.backend.service;

import com.zonezero.backend.entity.User;

public interface UserService {
  User upsert(String username, String email, String phone, String region);
  User getByEmail(String email);
}
