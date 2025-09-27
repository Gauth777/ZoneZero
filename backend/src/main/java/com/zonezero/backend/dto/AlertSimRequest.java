package com.zonezero.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class AlertSimRequest {
  @NotBlank public String phone;
  @NotBlank public String message;
}
