package com.zonezero.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
  @NotBlank public String username;
  @NotBlank @Email public String email;
  public String phone;
  @NotBlank public String region;
}
