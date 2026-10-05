package com.example.songservice.mapper;

import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

  public Song toEntity(SongRequestDto dto) {
    return Song.builder()
      .id(dto.getId())
      .name(dto.getName())
      .artist(dto.getArtist())
      .album(dto.getAlbum())
      .duration(dto.getDuration())
      .year(dto.getYear())
      .build();
  }

  public SongResponseDto toDto(Song entity) {
    return new SongResponseDto(
      entity.getId(),
      entity.getName(),
      entity.getArtist(),
      entity.getAlbum(),
      entity.getDuration(),
      entity.getYear()
    );
  }
}
