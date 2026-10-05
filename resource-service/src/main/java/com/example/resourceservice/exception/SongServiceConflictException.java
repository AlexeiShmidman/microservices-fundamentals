package com.example.resourceservice.exception;

/**
 * Thrown when the Song Service returns 409 Conflict — metadata for this resource ID already exists.
 */
public class SongServiceConflictException extends RuntimeException {

  public SongServiceConflictException(String message) {
    super(message);
  }
}
