package com.example.zonezero.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * Service for generating mock weather data. In a real implementation you
 * would inject a web client here to call a weather API such as
 * OpenWeatherMap or WeatherAPI.
 */
@Service
public class WeatherService {

    /**
     * Generate pseudo‑random weather data seeded by the region name.
     *
     * @param region a string like "Chennai,IN"
     * @return a map with keys matching OpenWeatherMap structure
     */
    public Map<String, Object> getWeatherForRegion(String region) {
        long seed = region == null ? 0 : region.chars().sum();
        Random rand = new Random(seed);
        int temp = 22 + rand.nextInt(36 - 22 + 1);
        int humidity = 40 + rand.nextInt(95 - 40 + 1);
        int wind = 1 + rand.nextInt(20 - 1 + 1);
        String[] statuses = { "Clear", "Clouds", "Rain", "Thunderstorm", "Drizzle", "Haze" };
        String status = statuses[rand.nextInt(statuses.length)];
        Map<String, Integer> main = new HashMap<>();
        main.put("temp", temp);
        main.put("humidity", humidity);
        Map<String, Integer> windMap = new HashMap<>();
        windMap.put("speed", wind);
        Map<String, String> weather = new HashMap<>();
        weather.put("main", status);
        Map<String, Object> result = new HashMap<>();
        result.put("main", main);
        result.put("wind", windMap);
        result.put("weather", java.util.Collections.singletonList(weather));
        return result;
    }
}