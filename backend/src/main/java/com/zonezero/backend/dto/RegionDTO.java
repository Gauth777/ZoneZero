package com.zonezero.backend.dto;

public class RegionDTO {
  public String name;
  public double lat;
  public double lon;

  public RegionDTO(String name, double lat, double lon) {
    this.name = name; this.lat = lat; this.lon = lon;
  }
}
