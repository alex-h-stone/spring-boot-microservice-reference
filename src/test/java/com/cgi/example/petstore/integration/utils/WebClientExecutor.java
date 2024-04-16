package com.cgi.example.petstore.integration.utils;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientExecutor {

  private final WebClient webClient;

  public ResponseEntity<String> execute(RequestEntity<?> requestEntity) {
    log.info("Integration test RequestEntity: [{}]", requestEntity);
    WebClient.RequestBodySpec requestSpec =
        webClient
            .method(requestEntity.getMethod())
            .uri(requestEntity.getUrl())
            .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()));
    if (requestEntity.hasBody()) {
      requestSpec.bodyValue(requestEntity.getBody());
    }
    ResponseEntity<String> entity =
        requestSpec.exchangeToMono(exchange()).onErrorResume(onError()).block();

    log.info("Integration test ResponseEntity: [{}]", entity);
    return entity;
  }

  private static Function<ClientResponse, Mono<ResponseEntity<String>>> exchange() {
    return response -> {
      if (response.statusCode().isError()) {
        return response.createException().flatMap(Mono::error);
      } else {
        return response.toEntity(String.class);
      }
    };
  }

  private Function<Throwable, Mono<? extends ResponseEntity<String>>> onError() {
    return throwable -> {
      if (throwable instanceof WebClientResponseException) {
        WebClientResponseException ex = (WebClientResponseException) throwable;
        // Return the status code and body contained in the exception
        ResponseEntity<String> responseEntity =
            ResponseEntity.status(ex.getStatusCode())
                .headers(ex.getHeaders())
                .body(ex.getResponseBodyAsString());

        return Mono.just(responseEntity);
      }
      return Mono.just(ResponseEntity.noContent().build());
    };
  }
}
