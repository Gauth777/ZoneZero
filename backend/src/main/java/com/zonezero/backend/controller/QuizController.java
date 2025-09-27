package com.zonezero.backend.controller;

import com.zonezero.backend.dto.QuizAttemptRequest;
import com.zonezero.backend.dto.QuizAttemptResponse;
import com.zonezero.backend.dto.QuizQuestionDTO;
import com.zonezero.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
  private final QuizService quizService;
  public QuizController(QuizService quizService) { this.quizService = quizService; }

  @GetMapping("/questions")
  public List<QuizQuestionDTO> questions(@RequestParam String category) {
    return quizService.getQuestionsByCategory(category);
  }

  @PostMapping("/attempts")
  public QuizAttemptResponse attempt(@Valid @RequestBody QuizAttemptRequest req) {
    return quizService.recordAttempt(req);
  }
}
