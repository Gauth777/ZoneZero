package com.zonezero.backend.util;

public class WeatherData {
  public String region;
  public double temp;
  public int humidity;
  public double windSpeed; // m/s
  public double rain1h;
  public double rain3h;
  public double pop; // 0..1
  public String source; // MOCK or LIVE
  public long fetchedAtEpochSec;
}
