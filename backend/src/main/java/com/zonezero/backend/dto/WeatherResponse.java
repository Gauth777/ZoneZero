package com.zonezero.backend.dto;

public class WeatherResponse {
  public String region;
  public double temp;
  public int humidity;
  public double windSpeed;
  public double rain1h;
  public double rain3h;
  public double pop; // probability of precipitation (0..1)
  public String source; // MOCK or LIVE
  public long fetchedAtEpochSec;

  public WeatherResponse() {}
}
