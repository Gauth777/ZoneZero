package com.zonezero.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,Object> onValidation(MethodArgumentNotValidException ex) {
    Map<String,Object> body = new HashMap<>();
    body.put("error","VALIDATION");
    body.put("message", ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField()+": "+e.getDefaultMessage()).toList());
    return body;
  }

  @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
  public Map<String,Object> onRSE(org.springframework.web.server.ResponseStatusException ex) {
    Map<String,Object> body = new HashMap<>();
    body.put("error", ex.getStatusCode().toString());
    body.put("message", ex.getReason());
    return body;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String,Object> onOther(Exception ex) {
    Map<String,Object> body = new HashMap<>();
    body.put("error","INTERNAL");
    body.put("message", ex.getMessage());
    return body;
  }
}
