package com.wego.carparkapi.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String,String>> handleConstraintViolation(ConstraintViolationException ex) {
    log.warn("Constraint violation: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    errors.put("error", "Validation failed");

    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      errors.put(propertyPath, message);
    }

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.warn("Method argument validation failed: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    errors.put("error", "Validation failed");

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());

    Map<String, String> error = new HashMap<>();
    error.put("error", "Invalid argument");
    error.put("message", ex.getMessage());

    return ResponseEntity.badRequest().body(error);
  }
}
