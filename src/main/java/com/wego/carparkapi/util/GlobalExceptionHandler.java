package com.wego.carparkapi.util;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, String>> handleMissingParams(
      MissingServletRequestParameterException ex) {
    log.warn("Missing required parameter: {}", ex.getParameterName());

    Map<String, String> error = new HashMap<>();
    error.put("error", "Missing required parameter");
    error.put("parameter", ex.getParameterName());
    error.put("message", String.format("Required parameter '%s' is missing", ex.getParameterName()));

    return ResponseEntity.badRequest().body(error);
  }
}
