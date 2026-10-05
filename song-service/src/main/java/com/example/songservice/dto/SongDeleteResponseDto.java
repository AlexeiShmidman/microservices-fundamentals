package com.example.songservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SongDeleteResponseDto {

  @JsonProperty("ids")
  private List<Long> ids;

  public SongDeleteResponseDto() {
  }

  public SongDeleteResponseDto(List<Long> ids) {
    this.ids = ids;
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }
}
