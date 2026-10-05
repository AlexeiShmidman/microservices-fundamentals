package com.example.resourceservice.client;

import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.exception.SongServiceConflictException;
import com.example.resourceservice.exception.SongServiceException;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class SongServiceClient {

  private static final Logger log = LoggerFactory.getLogger(SongServiceClient.class);

  private final RestClient restClient;
  private final io.github.resilience4j.retry.Retry deleteRetry;

  public SongServiceClient(RestClient songServiceRestClient, RetryRegistry retryRegistry) {
    this.restClient = songServiceRestClient;
    this.deleteRetry = retryRegistry.retry("songServiceDelete");

    retryRegistry.retry("songService").getEventPublisher()
      .onRetry(e -> log.warn("saveSongMetadata retry attempt #{}: {}",
        e.getNumberOfRetryAttempts(), e.getLastThrowable().getMessage()));
    this.deleteRetry.getEventPublisher()
      .onRetry(e -> log.warn("deleteSongMetadata retry attempt #{}: {}",
        e.getNumberOfRetryAttempts(), e.getLastThrowable().getMessage()));
  }

  @Retry(name = "songService")
  public void saveSongMetadata(SongMetadataDto dto) {
    log.debug("Sending song metadata to Song Service for resource ID={}", dto.getId());
    try {
      restClient.post()
        .uri("/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(dto)
        .retrieve()
        .onStatus(
          status -> status == HttpStatus.CONFLICT,
          (request, response) -> {
            throw new SongServiceConflictException(
              "Song metadata for resource ID=" + dto.getId() +
                " already exists in Song Service");
          }
        )
        .onStatus(
          status -> status.is4xxClientError() || status.is5xxServerError(),
          (request, response) -> {
            throw new SongServiceException(
              "Song Service responded with error. Status: " +
                response.getStatusCode());
          }
        )
        .toBodilessEntity();

      log.info("Song metadata successfully saved for resource ID={}", dto.getId());

    } catch (SongServiceConflictException | SongServiceException ex) {
      throw ex;
    } catch (ResourceAccessException ex) {
      log.error("Song Service is unreachable when saving metadata for resource ID={}: {}",
        dto.getId(), ex.getMessage());
      throw new SongServiceException(
        "Song Service is unreachable. Please try again later.", ex);
    } catch (Exception ex) {
      log.error("Unexpected error while saving song metadata for resource ID={}: {}",
        dto.getId(), ex.getMessage(), ex);
      throw new SongServiceException(
        "Unexpected error communicating with Song Service: " + ex.getMessage(), ex);
    }
  }

  public void deleteSongMetadata(String ids) {
    log.debug("Requesting Song Service to delete metadata for IDs={}", ids);
    try {
      deleteRetry.executeRunnable(() -> doDeleteSongMetadata(ids));
      log.info("Song metadata deletion requested successfully for IDs={}", ids);
    } catch (Exception ex) {
      log.error("All retry attempts exhausted for deleting song metadata IDs={}: {}",
        ids, ex.getMessage());
    }
  }

  private void doDeleteSongMetadata(String ids) {
    try {
      restClient.delete()
        .uri(u -> u.path("/songs").queryParam("id", ids).build())
        .retrieve()
        .onStatus(
          status -> status.is4xxClientError() || status.is5xxServerError(),
          (request, response) -> {
            throw new SongServiceException(
              "Song Service returned error status=" +
                response.getStatusCode() + " when deleting IDs=" + ids);
          }
        )
        .toBodilessEntity();
    } catch (SongServiceException ex) {
      throw ex;
    } catch (ResourceAccessException ex) {
      throw new SongServiceException(
        "Song Service unreachable when deleting IDs=" + ids, ex);
    }
  }
}
