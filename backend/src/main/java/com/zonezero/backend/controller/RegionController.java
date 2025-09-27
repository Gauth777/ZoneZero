package com.zonezero.backend.controller;

import com.zonezero.backend.dto.RegionDTO;
import com.zonezero.backend.service.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
  private final RegionService regionService;

  public RegionController(RegionService regionService) { this.regionService = regionService; }

  @GetMapping
  public List<RegionDTO> list() {
    return regionService.list().stream()
        .map(r -> new RegionDTO(r.getName(), r.getLat(), r.getLon()))
        .toList();
  }
}
