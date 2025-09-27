package com.zonezero.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "regions", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Region {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private Double lat;

  @Column(nullable = false)
  private Double lon;

  public Region() {}
  public Region(Long id, String name, Double lat, Double lon) {
    this.id = id; this.name = name; this.lat = lat; this.lon = lon;
  }

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }
  public Double getLon() { return lon; }
  public void setLon(Double lon) { this.lon = lon; }
}
