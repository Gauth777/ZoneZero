package com.zonezero.backend.service.impl;

import com.zonezero.backend.dto.RiskResponse;
import com.zonezero.backend.entity.Region;
import com.zonezero.backend.service.RegionService;
import com.zonezero.backend.service.RiskService;
import com.zonezero.backend.util.WeatherData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class RiskServiceImpl implements RiskService {

  private final RegionService regionService;
  private static final Set<String> CHENNAI_CLUSTER = new HashSet<>(Arrays.asList(
      "Chennai","Tiruvallur","Kancheepuram","Chengalpattu"));

  public RiskServiceImpl(RegionService regionService) { this.regionService = regionService; }

  @Override
  public RiskResponse computeRisk(String regionName, boolean forceRed, WeatherData w) {
    Region reg = regionService.getByNameOrThrow(regionName);

    String heat = computeHeat(w.temp, w.humidity);
    String flood = computeFlood(w.rain1h, w.rain3h, w.pop);
    String wind = computeWind(w.windSpeed);

    // Seasonal boost (Sep–Nov) for Chennai cluster
    LocalDate today = LocalDate.now();
    if (today.getMonthValue() >= Month.SEPTEMBER.getValue() &&
        today.getMonthValue() <= Month.NOVEMBER.getValue() &&
        CHENNAI_CLUSTER.contains(reg.getName())) {
      flood = escalateOne(flood);
    }

    RiskResponse r = new RiskResponse();
    r.region = regionName;
    r.heatLevel = heat;
    r.floodLevel = flood;
    r.windLevel = wind;
    r.basedOnWeatherAtEpochSec = w.fetchedAtEpochSec;

    r.messages.add("Source weather: " + w.source);
    r.messages.add(String.format("t=%.1f°C, hum=%d%%, wind=%.1f m/s, rain1h=%.1f, rain3h=%.1f, pop=%.2f",
        w.temp, w.humidity, w.windSpeed, w.rain1h, w.rain3h, w.pop));

    if (forceRed) {
      r.heatLevel = "red"; r.floodLevel = "red"; r.windLevel = "red";
      r.forcedRed = true;
      r.messages.add("Demo override: force=red applied.");
    }
    return r;
  }

  private String computeHeat(double temp, int humidity) {
    if (temp >= 38 && humidity >= 55) return "red";
    if (temp >= 35 && humidity >= 60) return "orange";
    if (temp >= 32 && humidity >= 50) return "yellow";
    return "green";
  }

  private String computeFlood(double rain1h, double rain3h, double pop) {
    if (rain3h >= 60) return "red";
    if (rain1h >= 20 || rain3h >= 35) return "orange";
    if (rain1h >= 10 || pop >= 0.7) return "yellow";
    return "green";
  }

  private String computeWind(double wind) {
    if (wind >= 25) return "red";
    if (wind >= 17) return "orange";
    if (wind >= 10) return "yellow";
    return "green";
  }

  private String escalateOne(String level) {
    switch (level) {
      case "yellow": return "orange";
      case "orange": return "red";
      default: return level; // green or red unchanged
    }
  }
}
