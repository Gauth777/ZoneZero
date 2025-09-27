package com.zonezero.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zonezero.backend.entity.QuizQuestion;
import com.zonezero.backend.entity.Region;
import com.zonezero.backend.repo.QuizQuestionRepository;
import com.zonezero.backend.repo.RegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class SeedConfig {

  @Bean
  CommandLineRunner seedRegions(RegionRepository regionRepository) {
    return args -> {
      List<Region> seeds = Arrays.asList(
          new Region(null,"Chennai",13.0827,80.2707),
          new Region(null,"Chengalpattu",12.6919,79.9766),
          new Region(null,"Kancheepuram",12.8352,79.7000),
          new Region(null,"Tiruvallur",13.1391,79.9094),
          new Region(null,"Doha",25.2854,51.5310),
          new Region(null,"Wollongong",-34.4278,150.8931)
      );
      for (Region r : seeds) {
        Optional<Region> existing = regionRepository.findByName(r.getName());
        if (existing.isEmpty()) regionRepository.save(r);
      }
    };
  }

  @Bean
  CommandLineRunner seedQuiz(QuizQuestionRepository repo, ObjectMapper mapper) {
    return args -> {
      if (repo.count() > 0) return;
      try (InputStream is = getClass().getClassLoader().getResourceAsStream("seed/quiz.json")) {
        if (is == null) return;
        List<QuizQuestion> qs = mapper.readValue(is, new TypeReference<List<QuizQuestion>>() {});
        repo.saveAll(qs);
      }
    };
  }
}
