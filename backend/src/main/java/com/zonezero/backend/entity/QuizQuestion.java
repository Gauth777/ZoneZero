package com.zonezero.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String category;

  @Column(nullable = false, length = 2000)
  private String question;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String optionsJson;

  @Column(nullable = false)
  private Integer answerIndex;

  @Column(length = 2000)
  private String explanation;

  public QuizQuestion() {}

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getQuestion() { return question; }
  public void setQuestion(String question) { this.question = question; }
  public String getOptionsJson() { return optionsJson; }
  public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
  public Integer getAnswerIndex() { return answerIndex; }
  public void setAnswerIndex(Integer answerIndex) { this.answerIndex = answerIndex; }
  public String getExplanation() { return explanation; }
  public void setExplanation(String explanation) { this.explanation = explanation; }
}
