package com.example.songservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SongRequestDto {

  @NotNull(message = "ID must be a positive number")
  @Positive(message = "ID must be a positive number")
  @JsonProperty("id")
  private Long id;

  @NotNull(message = "Song name is required")
  @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 characters")
  @JsonProperty("name")
  private String name;

  @NotNull(message = "Artist name is required")
  @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
  @JsonProperty("artist")
  private String artist;

  @NotNull(message = "Album name is required")
  @Size(min = 1, max = 100, message = "Album name must be between 1 and 100 characters")
  @JsonProperty("album")
  private String album;

  @NotNull(message = "Duration is required")
  @Pattern(regexp = "^[0-5]\\d:[0-5]\\d$",
    message = "Duration must be in mm:ss format with leading zeros")
  @JsonProperty("duration")
  private String duration;

  @NotNull(message = "Year is required")
  @Pattern(regexp = "^(19|20)\\d{2}$",
    message = "Year must be between 1900 and 2099")
  @JsonProperty("year")
  private String year;

  public SongRequestDto() {
  }

  public SongRequestDto(Long id, String name, String artist,
    String album, String duration, String year) {
    this.id = id;
    this.name = name;
    this.artist = artist;
    this.album = album;
    this.duration = duration;
    this.year = year;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }
}
