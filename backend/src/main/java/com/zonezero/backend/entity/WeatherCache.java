package com.zonezero.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "weather_cache")
public class WeatherCache {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String regionName;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String payloadJson;

  @Column(nullable = false)
  private Instant fetchedAt;

  public WeatherCache() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getRegionName() { return regionName; }
  public void setRegionName(String regionName) { this.regionName = regionName; }
  public String getPayloadJson() { return payloadJson; }
  public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
  public Instant getFetchedAt() { return fetchedAt; }
  public void setFetchedAt(Instant fetchedAt) { this.fetchedAt = fetchedAt; }
}

