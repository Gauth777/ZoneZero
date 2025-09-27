package com.zonezero.backend.dto;

public class LoginResponse {
  public String message;
  public String userId;
  public String region;

  public LoginResponse(String message, String userId, String region) {
    this.message = message;
    this.userId = userId;
    this.region = region;
  }
}
