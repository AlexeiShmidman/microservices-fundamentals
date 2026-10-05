package com.example.resourceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceIdResponseDto {

  @JsonProperty("id")
  private Long id;

  public ResourceIdResponseDto() {
  }

  public ResourceIdResponseDto(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
