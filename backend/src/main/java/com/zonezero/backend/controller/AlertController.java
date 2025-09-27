package com.zonezero.backend.controller;

import com.zonezero.backend.dto.AlertSimRequest;
import com.zonezero.backend.dto.AlertSimResponse;
import com.zonezero.backend.service.AlertService;
import com.zonezero.backend.util.MaskingUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
  private final AlertService alertService;
  public AlertController(AlertService alertService) { this.alertService = alertService; }

  @PostMapping("/simulate")
  public AlertSimResponse simulate(@Valid @RequestBody AlertSimRequest req) {
    String masked = MaskingUtil.maskPhone(req.phone);
    String status = alertService.simulateSms(req.phone, req.message).startsWith("SIMULATED") ? "sent" : "error";
    return new AlertSimResponse(status, masked, req.message);
  }
}
