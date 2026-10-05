package com.example.resourceservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private String errorMessage;
  private String errorCode;
  private Map<String, String> details;

  public ErrorResponse() {
  }

  public ErrorResponse(String errorMessage, String errorCode, Map<String, String> details) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
    this.details = details;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public Map<String, String> getDetails() {
    return details;
  }

  public void setDetails(Map<String, String> details) {
    this.details = details;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String errorMessage;
    private String errorCode;
    private Map<String, String> details;

    public Builder errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public Builder errorCode(String errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public Builder details(Map<String, String> details) {
      this.details = details;
      return this;
    }

    public ErrorResponse build() {
      return new ErrorResponse(errorMessage, errorCode, details);
    }
  }
}
