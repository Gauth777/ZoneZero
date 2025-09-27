package com.zonezero.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 36)
  private String userId;

  @Column(nullable = false)
  private String scenario;

  @Column(nullable = false)
  private Integer score;

  @Column(nullable = false)
  private Instant attemptedAt;

  public QuizAttempt() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
  public String getScenario() { return scenario; }
  public void setScenario(String scenario) { this.scenario = scenario; }
  public Integer getScore() { return score; }
  public void setScore(Integer score) { this.score = score; }
  public Instant getAttemptedAt() { return attemptedAt; }
  public void setAttemptedAt(Instant attemptedAt) { this.attemptedAt = attemptedAt; }
}
