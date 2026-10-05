package com.example.resourceservice.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class SongServiceClientConfig {

  @Value("${song-service.base-url}")
  private String baseUrl;

  @Value("${song-service.connect-timeout-ms}")
  private int connectTimeoutMs;

  @Value("${song-service.read-timeout-ms}")
  private int readTimeoutMs;

  @Bean
  public RestClient songServiceRestClient() {
    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
      .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
      .build();

    HttpClient httpClient = HttpClients.custom()
      .setDefaultRequestConfig(requestConfig)
      .build();

    HttpComponentsClientHttpRequestFactory factory =
      new HttpComponentsClientHttpRequestFactory(httpClient);

    return RestClient.builder()
      .baseUrl(baseUrl)
      .requestFactory(factory)
      .build();
  }
}
