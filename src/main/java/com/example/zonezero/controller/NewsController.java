package com.example.zonezero.controller;

import com.example.zonezero.service.NewsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<Map<String, Object>> getNews(@RequestParam(defaultValue = "disaster") String q) {
        return newsService.getDisasterNews(q);
    }
}
