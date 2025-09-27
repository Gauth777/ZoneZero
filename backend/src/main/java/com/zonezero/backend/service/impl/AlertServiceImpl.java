package com.zonezero.backend.service.impl;

import com.zonezero.backend.service.AlertService;
import com.zonezero.backend.util.MaskingUtil;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {
  @Override
  public String simulateSms(String phone, String message) {
    // No external integration in MVP
    return "SIMULATED -> " + MaskingUtil.maskPhone(phone) + " : " + message;
  }
}
