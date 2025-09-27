package com.zonezero.backend.controller;

import com.zonezero.backend.dto.RiskResponse;
import com.zonezero.backend.service.RegionService;
import com.zonezero.backend.service.RiskService;
import com.zonezero.backend.service.WeatherService;
import com.zonezero.backend.util.WeatherData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
  private final RegionService regionService;
  private final WeatherService weatherService;
  private final RiskService riskService;

  public RiskController(RegionService regionService, WeatherService weatherService, RiskService riskService) {
    this.regionService = regionService; this.weatherService = weatherService; this.riskService = riskService;
  }

  @GetMapping
  public RiskResponse get(@RequestParam String region, @RequestParam(required = false) String force) {
    regionService.getByNameOrThrow(region);
    WeatherData w = weatherService.getWeatherForRegion(region);
    boolean forceRed = "red".equalsIgnoreCase(force);
    return riskService.computeRisk(region, forceRed, w);
  }
}
