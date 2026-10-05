package com.example.songservice.controller;

import com.example.songservice.dto.SongDeleteResponseDto;
import com.example.songservice.dto.SongIdResponseDto;
import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

  private final SongService songService;

  public SongController(SongService songService) {
    this.songService = songService;
  }

  @PostMapping
  public ResponseEntity<SongIdResponseDto> createSong(@Valid @RequestBody SongRequestDto dto) {
    return ResponseEntity.ok(songService.createSong(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<SongResponseDto> getSong(@PathVariable Long id) {
    return ResponseEntity.ok(songService.getSong(id));
  }

  @DeleteMapping
  public ResponseEntity<SongDeleteResponseDto> deleteSongs(@RequestParam("id") String id) {
    return ResponseEntity.ok(songService.deleteSongs(id));
  }
}
