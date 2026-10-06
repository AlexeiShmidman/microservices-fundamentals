package com.example.resourceservice.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Configuration
public class S3Config {

  @Value("${aws.s3.endpoint}")
  private String endpoint;

  @Value("${aws.s3.region}")
  private String region;

  @Value("${aws.s3.access-key}")
  private String accessKey;

  @Value("${aws.s3.secret-key}")
  private String secretKey;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
      .endpointOverride(URI.create(endpoint))
      .region(Region.of(region))
      .credentialsProvider(StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)))
      .forcePathStyle(true)
      .build();
  }

  @Bean
  public ApplicationRunner createBucketIfNotExists(S3Client s3Client) {
    return args -> {
      try {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
      } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ignored) {
      }
    };
  }
}
