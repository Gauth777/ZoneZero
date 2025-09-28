package com.zonezero.backend.controller;

import com.zonezero.backend.dto.RiskResponse;
import com.zonezero.backend.service.RiskService;
import com.zonezero.backend.service.WeatherService;
import com.zonezero.backend.util.WeatherData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RiskController {

    private final RiskService riskService;
    private final WeatherService weatherService;

    public RiskController(RiskService riskService, WeatherService weatherService) {
        this.riskService = riskService;
        this.weatherService = weatherService;
    }

    @GetMapping("/risk")
    public RiskResponse getRisk(
            @RequestParam("region") String region,
            @RequestParam(value = "force", required = false) String force
    ) {
        boolean forceRed = "red".equalsIgnoreCase(force);
        WeatherData w = weatherService.getWeatherForRegion(region);
        return riskService.computeRisk(region, forceRed, w);
    }
}
