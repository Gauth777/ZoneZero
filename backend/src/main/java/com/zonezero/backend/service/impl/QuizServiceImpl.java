package com.zonezero.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zonezero.backend.dto.QuizAttemptRequest;
import com.zonezero.backend.dto.QuizAttemptResponse;
import com.zonezero.backend.dto.QuizQuestionDTO;
import com.zonezero.backend.entity.QuizAttempt;
import com.zonezero.backend.entity.QuizQuestion;
import com.zonezero.backend.entity.User;
import com.zonezero.backend.repo.QuizAttemptRepository;
import com.zonezero.backend.repo.QuizQuestionRepository;
import com.zonezero.backend.service.QuizService;
import com.zonezero.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class QuizServiceImpl implements QuizService {
  private final QuizQuestionRepository qRepo;
  private final QuizAttemptRepository aRepo;
  private final UserService userService;
  private final ObjectMapper mapper;

  public QuizServiceImpl(QuizQuestionRepository qRepo, QuizAttemptRepository aRepo, UserService userService, ObjectMapper mapper) {
    this.qRepo = qRepo; this.aRepo = aRepo; this.userService = userService; this.mapper = mapper;
  }

  @Override
  public List<QuizQuestionDTO> getQuestionsByCategory(String category) {
    return qRepo.findByCategoryIgnoreCase(category).stream().map(q -> {
      List<String> options = readOptions(q.getOptionsJson());
      return new QuizQuestionDTO(q.getId(), q.getCategory(), q.getQuestion(), options, q.getExplanation());
    }).collect(Collectors.toList());
  }

  @Override
  public QuizAttemptResponse recordAttempt(QuizAttemptRequest req) {
    User u = userService.getByEmail(req.userEmail);
    if (u == null) throw new ResponseStatusException(BAD_REQUEST, "Unknown user email: " + req.userEmail);

    QuizAttempt a = new QuizAttempt();
    a.setUserId(u.getId());
    a.setScenario(req.scenario);
    a.setScore(req.score);
    a.setAttemptedAt(Instant.now());
    a = aRepo.save(a);

    return new QuizAttemptResponse(a.getId(), a.getUserId(), a.getScenario(), a.getScore(), a.getAttemptedAt().getEpochSecond());
  }

  private List<String> readOptions(String json) {
    try { return mapper.readValue(json, new TypeReference<List<String>>() {}); }
    catch (Exception e) { throw new ResponseStatusException(BAD_REQUEST, "Invalid options JSON"); }
  }
}
