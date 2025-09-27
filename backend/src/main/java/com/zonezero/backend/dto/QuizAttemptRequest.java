package com.zonezero.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuizAttemptRequest {
  @NotBlank @Email public String userEmail;
  @NotBlank public String scenario;
  @NotNull public Integer score;
}

