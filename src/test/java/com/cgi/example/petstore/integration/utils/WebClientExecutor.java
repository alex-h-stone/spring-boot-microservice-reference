package com.cgi.example.petstore.integration.utils;

import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientExecutor {

  private final WebClient webClient;

  public ResponseEntity<String> execute(RequestEntity<?> requestEntity) {
    log.info("Request entity: [{}]", requestEntity);
    WebClient.RequestBodySpec requestSpec =
        webClient
            .method(Objects.requireNonNull(requestEntity.getMethod()))
            .uri(requestEntity.getUrl())
            .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()));

    Object body = requestEntity.getBody();
    if (Objects.nonNull(body)) {
      requestSpec.bodyValue(body);
    }

    ResponseEntity<String> responseEntity =
        requestSpec
            .exchangeToMono(response -> response.toEntity(String.class))
            .onErrorResume(onError())
            .block();

    log.info("Response entity: [{}]", responseEntity);
    return responseEntity;
  }

  private Function<Throwable, Mono<? extends ResponseEntity<String>>> onError() {
    return throwable -> {
      if (throwable instanceof WebClientResponseException exception) {
        ResponseEntity<String> responseEntity =
            ResponseEntity.status(exception.getStatusCode())
                .headers(exception.getHeaders())
                .body(exception.getResponseBodyAsString());
        return Mono.just(responseEntity);
      }
      log.error("Unable to create HTTP ResponseEntity: {}", throwable.getMessage(), throwable);
      throw new IllegalStateException("Unable to create HTTP ResponseEntity", throwable);
    };
  }
}
