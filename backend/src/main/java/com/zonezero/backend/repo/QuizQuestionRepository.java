package com.zonezero.backend.repo;

import com.zonezero.backend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
  List<QuizQuestion> findByCategoryIgnoreCase(String category);
}
