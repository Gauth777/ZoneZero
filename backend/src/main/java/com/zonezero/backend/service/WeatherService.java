package com.zonezero.backend.service;

import com.zonezero.backend.util.WeatherData;

public interface WeatherService {
  WeatherData getWeatherForRegion(String regionName);
}
