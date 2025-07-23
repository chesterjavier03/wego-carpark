package com.wego.carparkapi.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Configuration
public class WebConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
        .build();
  }
}
