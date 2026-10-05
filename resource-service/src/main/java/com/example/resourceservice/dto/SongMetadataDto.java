package com.example.resourceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SongMetadataDto {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("artist")
  private String artist;

  @JsonProperty("album")
  private String album;

  @JsonProperty("duration")
  private String duration;

  @JsonProperty("year")
  private String year;

  public SongMetadataDto() {
  }

  public SongMetadataDto(Long id, String name, String artist,
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder artist(String artist) {
      this.artist = artist;
      return this;
    }

    public Builder album(String album) {
      this.album = album;
      return this;
    }

    public Builder duration(String duration) {
      this.duration = duration;
      return this;
    }

    public Builder year(String year) {
      this.year = year;
      return this;
    }

    public SongMetadataDto build() {
      return new SongMetadataDto(id, name, artist, album, duration, year);
    }
  }
}
