package com.zonezero.backend.dto;

public class AlertSimResponse {
  public String status;
  public String to;
  public String message;

  public AlertSimResponse(String status, String to, String message) {
    this.status = status; this.to = to; this.message = message;
  }
}
