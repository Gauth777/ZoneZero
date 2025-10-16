package com.example.zonezero.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    private static final String API_KEY = "5368b161217fcf3bb593e4d5b64d01aa";
    private static final String BASE_URL = "https://gnews.io/api/v4/search";

    public List<Map<String, Object>> getDisasterNews(String keyword) {
        try {
            String url = String.format("%s?q=%s&lang=en&country=in&max=5&token=%s",
                    BASE_URL, keyword, API_KEY);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            return (List<Map<String, Object>>) response.get("articles");
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
