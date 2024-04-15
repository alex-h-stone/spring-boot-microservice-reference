package com.cgi.example.petstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfiguration {

  @Bean
  public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
    return new ShallowEtagHeaderFilter();
  }

  @Bean
  public WebClient webClient() {
    return WebClient.builder().build();
  }
}
