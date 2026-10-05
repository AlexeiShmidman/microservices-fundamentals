package com.example.songservice.repository;

import com.example.songservice.entity.Song;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

  List<Song> findAllByIdIn(List<Long> ids);
}

