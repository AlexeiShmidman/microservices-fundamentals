package com.example.resourceservice.service;

import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.exception.InvalidMp3FileException;
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
import org.springframework.stereotype.Component;


@Component
public class Mp3MetadataExtractor {

  private static final Logger log = LoggerFactory.getLogger(Mp3MetadataExtractor.class);

  private static final String AUDIO_MPEG_TYPE = "audio/mpeg";

  public SongMetadataDto extractMetadata(byte[] audioData, Long resourceId) {
    try (InputStream inputStream = new ByteArrayInputStream(audioData)) {
      Parser parser = new AutoDetectParser();
      BodyContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();

      parser.parse(inputStream, handler, metadata, context);

      String contentType = metadata.get(Metadata.CONTENT_TYPE);
      if (contentType == null || !contentType.contains("audio/mpeg")) {
        throw new InvalidMp3FileException("The uploaded file is not a valid MP3 file");
      }

      String title = getMetadataValue(metadata, TikaCoreProperties.TITLE.getName(), "Unknown Title");
      String artist = getMetadataValue(metadata, "xmpDM:artist", "Unknown Artist");
      String album = getMetadataValue(metadata, "xmpDM:album", "Unknown Album");
      String year = extractYear(metadata);
      String duration = extractDuration(metadata);

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
      log.error("Failed to parse MP3 metadata: {}", ex.getMessage());
      throw new InvalidMp3FileException("The uploaded file is not a valid MP3 file or metadata cannot be read");
    }
  }

  public void validateMp3(byte[] data) {
    try (InputStream inputStream = new ByteArrayInputStream(data)) {
      Parser parser = new AutoDetectParser();
      BodyContentHandler handler = new BodyContentHandler(-1);
      Metadata metadata = new Metadata();
      ParseContext context = new ParseContext();

      parser.parse(inputStream, handler, metadata, context);

      String contentType = metadata.get(Metadata.CONTENT_TYPE);
      if (contentType == null || !contentType.contains(AUDIO_MPEG_TYPE)) {
        throw new InvalidMp3FileException("The uploaded file is not a valid MP3 file. Detected type: " + contentType);
      }
    } catch (InvalidMp3FileException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new InvalidMp3FileException("The uploaded file could not be validated as an MP3 file");
    }
  }

  private String getMetadataValue(Metadata metadata, String key, String defaultValue) {
    String value = metadata.get(key);
    if (value == null || value.isBlank()) {
      // Try common alternative Tika metadata key formats
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
    // Try various year-related metadata fields
    String[] yearKeys = {"xmpDM:releaseDate", "date", "creation-date", "Year", "YEAR"};
    for (String key : yearKeys) {
      String value = metadata.get(key);
      if (value != null && !value.isBlank()) {
        // Extract 4-digit year from potential date strings like "1977-01-01"
        if (value.length() >= 4) {
          String year = value.substring(0, 4);
          if (year.matches("\\d{4}")) {
            return year;
          }
        }
      }
    }
    return "2000"; // sensible default
  }

  private String extractDuration(Metadata metadata) {
    // Tika stores duration in seconds as a double string
    String[] durationKeys = {"xmpDM:duration", "duration"};
    for (String key : durationKeys) {
      String value = metadata.get(key);
      if (value != null && !value.isBlank()) {
        try {
          double seconds = Double.parseDouble(value);
          return formatDuration((long) seconds);
        } catch (NumberFormatException ignored) {
          // try next key
        }
      }
    }
    return "00:00";
  }

  /**
   * Converts duration in seconds to mm:ss format with leading zeros.
   */
  public String formatDuration(long totalSeconds) {
    long minutes = totalSeconds / 60;
    long seconds = totalSeconds % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }
}

