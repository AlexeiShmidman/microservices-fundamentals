package com.example.resourceservice.exception;

/**
 * Thrown when the Song Service returns an unexpected error (5xx).
 */
public class SongServiceException extends RuntimeException {

  public SongServiceException(String message) {
    super(message);
  }

  public SongServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
