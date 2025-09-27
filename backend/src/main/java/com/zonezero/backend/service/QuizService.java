package com.zonezero.backend.service;

import com.zonezero.backend.dto.QuizAttemptRequest;
import com.zonezero.backend.dto.QuizAttemptResponse;
import com.zonezero.backend.dto.QuizQuestionDTO;

import java.util.List;

public interface QuizService {
  List<QuizQuestionDTO> getQuestionsByCategory(String category);
  QuizAttemptResponse recordAttempt(QuizAttemptRequest req);
}
