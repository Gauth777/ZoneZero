package com.example.zonezero.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zonezero.model.WeatherData;
import com.example.zonezero.service.WeatherService;

/**
 * REST controller exposing the weather endpoint.
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * GET /api/weather?q=City,CountryCode
     *
     * @param q region string (e.g. "Chennai,IN")
     * @return JSON containing weather data
     */
    @GetMapping
public ResponseEntity<WeatherData> getWeather(@RequestParam(name = "q") String q) {
    WeatherData weather = weatherService.getWeatherForRegion(q);
    return ResponseEntity.ok(weather);
}

}