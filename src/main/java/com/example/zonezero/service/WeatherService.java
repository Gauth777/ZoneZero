package com.example.zonezero.service;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.zonezero.model.WeatherData;
import com.example.zonezero.model.Alert;
import com.example.zonezero.repository.AlertRepository;

@Service
public class WeatherService {

    private final AlertRepository alertRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random rand = new Random();

    // Load from application.properties
    @Value("${openweather.api.key}")
    private String apiKey;

    public WeatherService(AlertRepository alertRepo) {
        this.alertRepo = alertRepo;
    }

    public WeatherData getWeatherForRegion(String region) {
        try {
            String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                region, apiKey
            );

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) throw new RuntimeException("Null response from OpenWeather");

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            var weatherArr = (java.util.List<Map<String, Object>>) response.get("weather");
            String status = weatherArr != null && !weatherArr.isEmpty() ?
                    (String) weatherArr.get(0).get("main") : "Clear";

            int temp = ((Number) main.get("temp")).intValue();
            int humidity = ((Number) main.get("humidity")).intValue();
            int windSpeed = ((Number) wind.get("speed")).intValue();

            WeatherData data = new WeatherData(temp, humidity, windSpeed, status);

            // Trigger your existing alert logic
            String severity = getSeverity(data);
            if (!"GREEN".equals(severity)) {
                Alert a = new Alert();
                a.setRegion(region);
                a.setMessage("⚠️ " + severity + " ALERT in " + region + " — " + status +
                        " | Temp: " + temp + "°C | Humidity: " + humidity + "%");
                a.setSeverity(severity);
                a.setTimestamp(java.time.LocalDateTime.now());
                alertRepo.save(a);
            }

            return data;

        } catch (Exception e) {
            // fallback to random mock values if API fails
            System.out.println("⚠️ OpenWeather fetch failed, using mock data: " + e.getMessage());
            int temp = rand.nextInt(25) + 20;
            int humidity = rand.nextInt(70) + 30;
            int windSpeed = rand.nextInt(20) + 1;
            String[] statuses = {"Clear", "Clouds", "Rain", "Thunderstorm", "Haze"};
            String status = statuses[rand.nextInt(statuses.length)];
            return new WeatherData(temp, humidity, windSpeed, status);
        }
    }

    private String getSeverity(WeatherData w) {
        if (w.getWindSpeed() > 14 || ("Thunderstorm".equals(w.getStatus()) && w.getHumidity() > 80))
            return "RED";
        if (w.getTemp() > 34 && w.getHumidity() > 60)
            return "YELLOW";
        return "GREEN";
    }
}
