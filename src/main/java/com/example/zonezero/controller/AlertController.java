package com.example.zonezero.controller;

import com.example.zonezero.model.Alert;
import com.example.zonezero.repository.AlertRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepo;

    public AlertController(AlertRepository alertRepo) {
        this.alertRepo = alertRepo;
    }

    @GetMapping
    public List<Alert> getAlertsByRegion(@RequestParam String region) {
        return alertRepo.findByRegionIgnoreCase(region);
    }
}
