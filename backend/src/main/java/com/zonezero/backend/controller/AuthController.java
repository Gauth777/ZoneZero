package com.zonezero.backend.controller;

import com.zonezero.backend.dto.LoginRequest;
import com.zonezero.backend.dto.LoginResponse;
import com.zonezero.backend.entity.Region;
import com.zonezero.backend.service.RegionService;
import com.zonezero.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final UserService userService;
  private final RegionService regionService;

  public AuthController(UserService userService, RegionService regionService) {
    this.userService = userService; this.regionService = regionService;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest req) {
    // Validate region exists
    Region r = regionService.getByNameOrThrow(req.region);
    var u = userService.upsert(req.username, req.email, req.phone, r.getName());
    return new LoginResponse("Welcome, " + u.getUsername() + "!", u.getId(), u.getRegion());
  }
}
