package com.zonezero.backend.service.impl;

import com.zonezero.backend.entity.Region;
import com.zonezero.backend.repo.RegionRepository;
import com.zonezero.backend.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {
  private final RegionRepository repo;
  public RegionServiceImpl(RegionRepository repo) { this.repo = repo; }

  @Override public List<Region> list() { return repo.findAll(); }

  @Override public Region getByNameOrThrow(String name) {
    return repo.findByName(name).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown region: " + name));
  }
}
