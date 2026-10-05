package com.example.resourceservice.controller;

import com.example.resourceservice.dto.ResourceDeleteResponseDto;
import com.example.resourceservice.dto.ResourceIdResponseDto;
import com.example.resourceservice.service.ResourceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @PostMapping(consumes = "audio/mpeg")
  public ResponseEntity<ResourceIdResponseDto> upload(@RequestBody byte[] file,
    @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType) {

    return ResponseEntity.ok(resourceService.uploadResource(file, contentType));
  }

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
    byte[] data = resourceService.getResourceData(id);
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType("audio/mpeg"))
      .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"resource-" + id + ".mp3\"")
      .body(data);
  }

  @DeleteMapping
  public ResponseEntity<ResourceDeleteResponseDto> delete(@RequestParam("id") String id) {
    return ResponseEntity.ok(resourceService.deleteResources(id));
  }
}
