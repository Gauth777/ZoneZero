package com.example.zonezero.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zonezero.service.NewsService;

/**
 * REST controller exposing the news endpoint.
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * GET /api/news
     *
     * @return a list of news articles
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getNews() {
        List<Map<String, String>> articles = newsService.getLatestNews();
        return ResponseEntity.ok(articles);
    }
}