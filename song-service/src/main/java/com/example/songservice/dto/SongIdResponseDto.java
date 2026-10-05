package com.example.songservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SongIdResponseDto {

  @JsonProperty("id")
  private Long id;

  public SongIdResponseDto() {
  }

  public SongIdResponseDto(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
