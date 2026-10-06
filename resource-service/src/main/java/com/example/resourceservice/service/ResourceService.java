package com.example.resourceservice.service;

import com.example.resourceservice.client.SongServiceClient;
import com.example.resourceservice.dto.ResourceDeleteResponseDto;
import com.example.resourceservice.dto.ResourceIdResponseDto;
import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.exception.InvalidRequestException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.repository.ResourceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceService {

  private static final Logger log = LoggerFactory.getLogger(ResourceService.class);
  private static final int MAX_CSV_LENGTH = 200;

  private final ResourceRepository resourceRepository;
  private final Mp3MetadataExtractor metadataExtractor;
  private final S3StorageService storageService;
  private final SongServiceClient songServiceClient;

  public ResourceService(ResourceRepository resourceRepository,
    Mp3MetadataExtractor metadataExtractor,
    S3StorageService storageService,
    SongServiceClient songServiceClient) {
    this.resourceRepository = resourceRepository;
    this.metadataExtractor = metadataExtractor;
    this.storageService = storageService;
    this.songServiceClient = songServiceClient;
  }

  @Transactional
  public ResourceIdResponseDto uploadResource(byte[] data, String contentType) {
    if (contentType == null || !contentType.contains("audio/mpeg")) {
      throw new InvalidMp3FileException(
        "Invalid Content-Type. Expected audio/mpeg, received: " + contentType);
    }
    metadataExtractor.validateMp3(data);

    String key = "resources/resource-" + System.currentTimeMillis() + ".mp3";
    storageService.upload(key, data, "audio/mpeg");

    Resource resource = Resource.builder()
      .name(key)
      .storageLocation(key)
      .contentType("audio/mpeg")
      .size((long) data.length)
      .build();

    Resource saved = resourceRepository.save(resource);
    log.info("Resource stored with ID={}", saved.getId());

    SongMetadataDto dto = metadataExtractor.extractMetadata(data, saved.getId());
    songServiceClient.saveSongMetadata(dto);

    return new ResourceIdResponseDto(saved.getId());
  }

  @Transactional(readOnly = true)
  public byte[] getResourceData(Long id) {
    validatePositiveId(id);
    Resource resource = resourceRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException(
        "Resource with ID=" + id + " not found"));
    return storageService.download(resource.getStorageLocation());
  }

  @Transactional
  public ResourceDeleteResponseDto deleteResources(String csvIds) {
    validateCsvIds(csvIds);
    List<Long> ids = parseCsvIds(csvIds);

    List<Resource> found = resourceRepository.findAllByIdIn(ids);
    List<Long> foundIds = found.stream()
      .map(Resource::getId)
      .collect(Collectors.toList());

    if (!foundIds.isEmpty()) {
      List<String> storageKeys = found.stream()
        .map(Resource::getStorageLocation)
        .collect(Collectors.toList());

      resourceRepository.deleteAllById(foundIds);
      String idsStr = foundIds.stream()
        .map(String::valueOf)
        .collect(Collectors.joining(","));
      songServiceClient.deleteSongMetadata(idsStr);

      // S3 deletion runs last: if DB or song-service fails the transaction rolls back
      // and S3 is untouched. An orphaned S3 object is recoverable; a ghost DB record
      // pointing to a missing S3 key causes every subsequent GET to fail.
      storageKeys.forEach(storageService::delete);
      log.info("Deleted resources: {}", foundIds);
    }

    return new ResourceDeleteResponseDto(foundIds);
  }

  private void validatePositiveId(Long id) {
    if (id == null || id <= 0) {
      throw new InvalidRequestException(
        "Invalid value '" + id + "' for ID. Must be a positive integer");
    }
  }

  private void validateCsvIds(String csv) {
    if (csv == null || csv.isBlank()) {
      throw new InvalidRequestException("ID parameter must not be empty");
    }
    if (csv.length() > MAX_CSV_LENGTH) {
      throw new InvalidRequestException(
        "CSV string is too long: received " + csv.length() + " characters, maximum allowed is " + MAX_CSV_LENGTH);
    }
  }

  private List<Long> parseCsvIds(String csv) {
    List<Long> result = new ArrayList<>();
    for (String part : csv.split(",")) {
      String trimmed = part.trim();
        if (trimmed.isEmpty()) {
            continue;
        }
      try {
        long value = Long.parseLong(trimmed);
        if (value <= 0) {
          throw new InvalidRequestException(
            "Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
        }
        result.add(value);
      } catch (NumberFormatException ex) {
        throw new InvalidRequestException(
          "Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
      }
    }
    return result;
  }
}
