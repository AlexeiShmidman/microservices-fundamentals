package com.example.resourceservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService {

  private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

  private final S3Client s3Client;
  private final String bucketName;

  public S3StorageService(S3Client s3Client,
    @Value("${aws.s3.bucket-name}") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  public String upload(String key, byte[] data, String contentType) {
    s3Client.putObject(
      PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(contentType)
        .contentLength((long) data.length)
        .build(),
      RequestBody.fromBytes(data)
    );
    log.info("Uploaded file to S3: bucket={}, key={}", bucketName, key);
    return key;
  }

  public byte[] download(String key) {
    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
      GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build()
    );
    log.info("Downloaded file from S3: bucket={}, key={}", bucketName, key);
    return objectBytes.asByteArray();
  }

  public void delete(String key) {
    s3Client.deleteObject(
      DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build()
    );
    log.info("Deleted file from S3: bucket={}, key={}", bucketName, key);
  }
}
