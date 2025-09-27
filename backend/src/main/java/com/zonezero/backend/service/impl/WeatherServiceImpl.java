package com.zonezero.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zonezero.backend.entity.Region;
import com.zonezero.backend.entity.WeatherCache;
import com.zonezero.backend.repo.WeatherCacheRepository;
import com.zonezero.backend.service.RegionService;
import com.zonezero.backend.service.WeatherService;
import com.zonezero.backend.util.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class WeatherServiceImpl implements WeatherService {

  private final RegionService regionService;
  private final WeatherCacheRepository cacheRepo;
  private final ObjectMapper mapper;
  private final RestTemplate http = new RestTemplate();

  @Value("${weather.mode:MOCK}")
  private String mode;

  @Value("${openweather.apiKey:}")
  private String apiKey;

  public WeatherServiceImpl(RegionService regionService, WeatherCacheRepository cacheRepo, ObjectMapper mapper) {
    this.regionService = regionService;
    this.cacheRepo = cacheRepo;
    this.mapper = mapper;
  }

  @Override
  public WeatherData getWeatherForRegion(String regionName) {
    // Use cache if <= 10 minutes old
    var cachedOpt = cacheRepo.findTopByRegionNameOrderByFetchedAtDesc(regionName);
    if (cachedOpt.isPresent()) {
      WeatherCache c = cachedOpt.get();
      if (Duration.between(c.getFetchedAt(), Instant.now()).toMinutes() <= 10) {
        return fromJson(c.getPayloadJson());
      }
    }

    WeatherData wd = "LIVE".equalsIgnoreCase(mode) ? fetchLive(regionName) : fetchMock(regionName);
    // persist cache
    try {
      WeatherCache wc = new WeatherCache();
      wc.setRegionName(regionName);
      wc.setPayloadJson(mapper.writeValueAsString(wd));
      wc.setFetchedAt(Instant.now());
      cacheRepo.save(wc);
    } catch (Exception ignore) {}
    return wd;
  }

  private WeatherData fetchMock(String regionName) {
    Random r = new Random(regionName.hashCode() + Instant.now().getEpochSecond()/600); // stable-ish per 10 min
    double temp = 28 + r.nextDouble() * 12; // 28..40
    int humidity = 45 + r.nextInt(55); // 45..99
    double wind = 3 + r.nextDouble() * 28; // 3..31 m/s
    double rain1h = r.nextDouble() < 0.4 ? r.nextDouble() * 30 : 0.0; // bursts
    double rain3h = rain1h * (1.0 + r.nextDouble()*2);
    double pop = r.nextDouble() < 0.5 ? r.nextDouble() : 0.0;

    WeatherData wd = new WeatherData();
    wd.region = regionName;
    wd.temp = round1(temp);
    wd.humidity = humidity;
    wd.windSpeed = round1(wind);
    wd.rain1h = round1(rain1h);
    wd.rain3h = round1(rain3h);
    wd.pop = round2(pop);
    wd.source = "MOCK";
    wd.fetchedAtEpochSec = Instant.now().getEpochSecond();
    return wd;
  }

  private WeatherData fetchLive(String regionName) {
    Region region = regionService.getByNameOrThrow(regionName);
    String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + region.getLat()
        + "&lon=" + region.getLon() + "&units=metric&appid=" + apiKey;

    long start = System.nanoTime();
    ResponseEntity<JsonNode> resp = http.getForEntity(url, JsonNode.class);
    long end = System.nanoTime();
    // Basic logging of external API latency
    System.out.println("[OpenWeather] latency ms: " + ((end - start)/1_000_000));

    JsonNode root = resp.getBody();
    double temp = val(root, "main.temp", 0.0);
    int humidity = (int) val(root, "main.humidity", 0.0);
    double wind = val(root, "wind.speed", 0.0);
    double rain1h = val(root, "rain.1h", 0.0);
    double rain3h = val(root, "rain.3h", 0.0);

    WeatherData wd = new WeatherData();
    wd.region = regionName;
    wd.temp = round1(temp);
    wd.humidity = humidity;
    wd.windSpeed = round1(wind);
    wd.rain1h = round1(rain1h);
    wd.rain3h = round1(rain3h);
    wd.pop = 0.0; // current API lacks 'pop'
    wd.source = "LIVE";
    wd.fetchedAtEpochSec = Instant.now().getEpochSecond();
    return wd;
  }

  private double val(JsonNode node, String path, double def) {
    String[] parts = path.split("\\.");
    JsonNode cur = node;
    for (String p : parts) {
      if (cur == null) return def;
      cur = cur.get(p);
    }
    return (cur != null && cur.isNumber()) ? cur.asDouble() : def;
  }

  private WeatherData fromJson(String json) {
    try { return mapper.readValue(json, WeatherData.class); }
    catch (Exception e) { return fetchMock("UNKNOWN"); }
  }

  private double round1(double d) { return Math.round(d*10.0)/10.0; }
  private double round2(double d) { return Math.round(d*100.0)/100.0; }
}
