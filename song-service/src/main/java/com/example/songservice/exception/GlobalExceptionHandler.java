package com.example.songservice.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(SongNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(SongNotFoundException ex) {
    log.warn("Song not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("404")
        .build());
  }

  @ExceptionHandler(SongAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleConflict(SongAlreadyExistsException ex) {
    log.warn("Song already exists: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("409")
        .build());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String value = ex.getValue() != null ? ex.getValue().toString() : "null";
    String message = String.format("Invalid value '%s' for ID. Must be a positive integer", value);
    log.warn("Type mismatch for parameter '{}': {}", ex.getName(), message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage(message)
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(InvalidRequestException ex) {
    log.warn("Invalid request: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> details = new LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    log.warn("Validation errors: {}", details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage("Validation error")
        .errorCode("400")
        .details(details)
        .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    log.error("Unexpected internal error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ErrorResponse.builder()
        .errorMessage("An internal server error occurred")
        .errorCode("500")
        .build());
  }
}
