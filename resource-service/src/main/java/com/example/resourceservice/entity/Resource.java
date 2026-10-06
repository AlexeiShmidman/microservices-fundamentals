package com.example.resourceservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
public class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "storage_location", nullable = false)
  private String storageLocation;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column
  private Long size;

  public Resource() {
  }

  public Resource(Long id, String name, String storageLocation, String contentType, Long size) {
    this.id = id;
    this.name = name;
    this.storageLocation = storageLocation;
    this.contentType = contentType;
    this.size = size;
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

  public String getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String name;
    private String storageLocation;
    private String contentType;
    private Long size;

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder storageLocation(String storageLocation) {
      this.storageLocation = storageLocation;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder size(Long size) {
      this.size = size;
      return this;
    }

    public Resource build() {
      return new Resource(id, name, storageLocation, contentType, size);
    }
  }
}
