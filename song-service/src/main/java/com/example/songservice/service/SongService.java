package com.example.songservice.service;

import com.example.songservice.dto.SongDeleteResponseDto;
import com.example.songservice.dto.SongIdResponseDto;
import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.entity.Song;
import com.example.songservice.exception.InvalidRequestException;
import com.example.songservice.exception.SongAlreadyExistsException;
import com.example.songservice.exception.SongNotFoundException;
import com.example.songservice.mapper.SongMapper;
import com.example.songservice.repository.SongRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SongService {

  private static final Logger log = LoggerFactory.getLogger(SongService.class);
  private static final int MAX_CSV_LENGTH = 200;

  private final SongRepository songRepository;
  private final SongMapper songMapper;

  public SongService(SongRepository songRepository, SongMapper songMapper) {
    this.songRepository = songRepository;
    this.songMapper = songMapper;
  }

  @Transactional
  public SongIdResponseDto createSong(SongRequestDto dto) {
    if (songRepository.existsById(dto.getId())) {
      throw new SongAlreadyExistsException(
        "Metadata for resource ID=" + dto.getId() + " already exists");
    }
    Song saved = songRepository.save(songMapper.toEntity(dto));
    log.info("Metadata created for ID={}", saved.getId());
    return new SongIdResponseDto(saved.getId());
  }

  @Transactional(readOnly = true)
  public SongResponseDto getSong(Long id) {
    validatePositiveId(id);
    Song song = songRepository.findById(id)
      .orElseThrow(() -> new SongNotFoundException(
        "Song metadata for ID=" + id + " not found"));
    return songMapper.toDto(song);
  }

  @Transactional
  public SongDeleteResponseDto deleteSongs(String csvIds) {
    validateCsvIds(csvIds);
    List<Long> ids = parseCsvIds(csvIds);

    List<Song> found = songRepository.findAllByIdIn(ids);
    List<Long> foundIds = found.stream()
      .map(Song::getId)
      .collect(Collectors.toList());

    if (!foundIds.isEmpty()) {
      songRepository.deleteAllById(foundIds);
      log.info("Deleted song metadata IDs={}", foundIds);
    }
    return new SongDeleteResponseDto(foundIds);
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
