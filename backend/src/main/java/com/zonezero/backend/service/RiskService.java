package com.zonezero.backend.service;

import com.zonezero.backend.dto.RiskResponse;
import com.zonezero.backend.util.WeatherData;

public interface RiskService {
  RiskResponse computeRisk(String regionName, boolean forceRed, WeatherData weather);
}
