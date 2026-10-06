package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.SongMetadataDto;
import com.example.resourceprocessor.exception.InvalidMp3FileException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Mp3MetadataService {

  private static final Logger log = LoggerFactory.getLogger(Mp3MetadataService.class);
  private static final String AUDIO_MPEG_TYPE = "audio/mpeg";

  public SongMetadataDto extractMetadata(byte[] audioData, Long resourceId) {
    try (InputStream inputStream = new ByteArrayInputStream(audioData)) {
      Parser parser = new AutoDetectParser();
      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();

      parser.parse(inputStream, handler, metadata, context);

      String contentType = metadata.get(Metadata.CONTENT_TYPE);
      if (contentType == null || !contentType.contains(AUDIO_MPEG_TYPE)) {
        throw new InvalidMp3FileException(
            "File is not a valid MP3. Detected type: " + contentType);
      }

      String title = getMetadataValue(metadata, TikaCoreProperties.TITLE.getName(), "Unknown Title");
      String artist = getMetadataValue(metadata, "xmpDM:artist", "Unknown Artist");
      String album = getMetadataValue(metadata, "xmpDM:album", "Unknown Album");
      String year = extractYear(metadata);
      String duration = extractDuration(metadata);

      log.debug("Extracted metadata for resourceId={}: title={}, artist={}", resourceId, title, artist);

      return SongMetadataDto.builder()
          .id(resourceId)
          .name(title)
          .artist(artist)
          .album(album)
          .duration(duration)
          .year(year)
          .build();

    } catch (InvalidMp3FileException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("Failed to parse MP3 metadata for resourceId={}: {}", resourceId, ex.getMessage());
      throw new InvalidMp3FileException("Could not read MP3 metadata");
    }
  }

  private String getMetadataValue(Metadata metadata, String key, String defaultValue) {
    String value = metadata.get(key);
    if (value == null || value.isBlank()) {
      for (String name : metadata.names()) {
        if (name.equalsIgnoreCase(key)) {
          value = metadata.get(name);
          break;
        }
      }
    }
    return (value != null && !value.isBlank()) ? value.trim() : defaultValue;
  }

  private String extractYear(Metadata metadata) {
    String[] yearKeys = {"xmpDM:releaseDate", "date", "creation-date", "Year", "YEAR"};
    for (String key : yearKeys) {
      String value = metadata.get(key);
      if (value != null && !value.isBlank() && value.length() >= 4) {
        String year = value.substring(0, 4);
        if (year.matches("\\d{4}")) {
          return year;
        }
      }
    }
    return "2000";
  }

  private String extractDuration(Metadata metadata) {
    String[] durationKeys = {"xmpDM:duration", "duration"};
    for (String key : durationKeys) {
      String value = metadata.get(key);
      if (value != null && !value.isBlank()) {
        try {
          double seconds = Double.parseDouble(value);
          long total = (long) seconds;
          return String.format("%02d:%02d", total / 60, total % 60);
        } catch (NumberFormatException ignored) {
        }
      }
    }
    return "00:00";
  }
}
