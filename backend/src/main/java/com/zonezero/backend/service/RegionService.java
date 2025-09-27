package com.zonezero.backend.service;

import com.zonezero.backend.entity.Region;

import java.util.List;

public interface RegionService {
  List<Region> list();
  Region getByNameOrThrow(String name);
}
