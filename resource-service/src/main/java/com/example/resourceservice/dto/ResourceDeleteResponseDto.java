package com.example.resourceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ResourceDeleteResponseDto {

  @JsonProperty("ids")
  private List<Long> ids;

  public ResourceDeleteResponseDto() {
  }

  public ResourceDeleteResponseDto(List<Long> ids) {
    this.ids = ids;
  }

  public List<Long> getIds() {
    return ids;
  }

  public void setIds(List<Long> ids) {
    this.ids = ids;
  }
}
