package com.zonezero.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class RiskResponse {
  public String region;
  public String heatLevel;   // green/yellow/orange/red
  public String floodLevel;  // green/yellow/orange/red
  public String windLevel;   // green/yellow/orange/red
  public List<String> messages = new ArrayList<>();
  public boolean forcedRed = false;

  public long basedOnWeatherAtEpochSec;
}
