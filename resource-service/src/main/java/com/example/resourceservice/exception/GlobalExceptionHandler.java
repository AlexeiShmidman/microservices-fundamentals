package com.example.resourceservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("404")
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

  @ExceptionHandler(InvalidMp3FileException.class)
  public ResponseEntity<ErrorResponse> handleInvalidMp3(InvalidMp3FileException ex) {
    log.warn("Invalid MP3 file: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
    log.warn("Request body not readable: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage("Request body is missing or unreadable")
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
    String contentType = ex.getContentType() != null ? ex.getContentType().toString() : "unknown";
    log.warn("Unsupported media type: {}", contentType);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage("Invalid file format: " + contentType + ". Only MP3 files are allowed")
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String value = ex.getValue() != null ? ex.getValue().toString() : "unknown";
    log.warn("Type mismatch for parameter: value={}", value);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage("Invalid value '" + value + "' for ID. Must be a positive integer")
        .errorCode("400")
        .build());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex) {
    log.warn("File upload size exceeded: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
        .errorMessage("Uploaded file exceeds the maximum allowed size")
        .errorCode("400")
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

  @ExceptionHandler(SongServiceConflictException.class)
  public ResponseEntity<ErrorResponse> handleSongConflict(SongServiceConflictException ex) {
    log.warn("Song Service conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("409")
        .build());
  }

  @ExceptionHandler(SongServiceException.class)
  public ResponseEntity<ErrorResponse> handleSongServiceError(SongServiceException ex) {
    log.error("Song Service error: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body(ErrorResponse.builder()
        .errorMessage(ex.getMessage())
        .errorCode("503")
        .build());
  }
}
