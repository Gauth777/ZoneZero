package com.zonezero.backend.controller;

import com.zonezero.backend.dto.WeatherResponse;
import com.zonezero.backend.service.RegionService;
import com.zonezero.backend.service.WeatherService;
import com.zonezero.backend.util.WeatherData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
  private final WeatherService weatherService;
  private final RegionService regionService;

  public WeatherController(WeatherService weatherService, RegionService regionService) {
    this.weatherService = weatherService; this.regionService = regionService;
  }

  @GetMapping
  public WeatherResponse get(@RequestParam String region) {
    regionService.getByNameOrThrow(region);
    WeatherData wd = weatherService.getWeatherForRegion(region);
    WeatherResponse r = new WeatherResponse();
    r.region = wd.region; r.temp = wd.temp; r.humidity = wd.humidity; r.windSpeed = wd.windSpeed;
    r.rain1h = wd.rain1h; r.rain3h = wd.rain3h; r.pop = wd.pop; r.source = wd.source;
    r.fetchedAtEpochSec = wd.fetchedAtEpochSec;
    return r;
  }
}
