package com.zonezero.backend.util;

public class MaskingUtil {
  public static String maskPhone(String phone) {
    if (phone == null || phone.length() < 4) return "****";
    String last4 = phone.substring(phone.length()-4);
    return "****-****-" + last4;
  }
}
