package com.zonezero.backend.repo;

import com.zonezero.backend.entity.WeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, Long> {
  Optional<WeatherCache> findTopByRegionNameOrderByFetchedAtDesc(String regionName);
}
