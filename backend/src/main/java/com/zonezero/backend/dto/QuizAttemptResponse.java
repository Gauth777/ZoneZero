package com.zonezero.backend.dto;

public class QuizAttemptResponse {
  public Long id;
  public String userId;
  public String scenario;
  public Integer score;
  public long attemptedAtEpochSec;

  public QuizAttemptResponse(Long id, String userId, String scenario, Integer score, long attemptedAtEpochSec) {
    this.id = id; this.userId = userId; this.scenario = scenario; this.score = score; this.attemptedAtEpochSec = attemptedAtEpochSec;
  }
}
