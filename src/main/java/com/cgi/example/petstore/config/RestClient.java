package com.cgi.example.petstore.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClient {

    @Bean
    public WebClient webClient() {
        // Include any config parameters (headers, base URL etc.) common to ALL
        // external APIs in this method
        return WebClient.builder().build();
    }
}
