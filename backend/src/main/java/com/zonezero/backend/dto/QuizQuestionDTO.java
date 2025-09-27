package com.zonezero.backend.dto;

import java.util.List;

public class QuizQuestionDTO {
  public Long id;
  public String category;
  public String question;
  public List<String> options;
  public String explanation; // optional

  public QuizQuestionDTO() {}

  public QuizQuestionDTO(Long id, String category, String question, List<String> options, String explanation) {
    this.id = id; this.category = category; this.question = question; this.options = options; this.explanation = explanation;
  }
}
