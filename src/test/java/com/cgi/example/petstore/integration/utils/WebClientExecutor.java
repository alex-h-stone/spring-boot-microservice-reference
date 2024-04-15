package com.cgi.example.petstore.integration.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebClientExecutor {

    private final WebClient webClient;

    public ResponseEntity<String> execute(RequestEntity<?> requestEntity) {
        log.info("Integration test RequestEntity: [{}]", requestEntity);
        WebClient.RequestBodySpec requestSpec = webClient.method(requestEntity.getMethod())
                .uri(requestEntity.getUrl())
                .headers(httpHeaders -> httpHeaders.addAll(requestEntity.getHeaders()));
        if (requestEntity.hasBody()) {
            requestSpec.bodyValue(requestEntity.getBody());
        }
        Mono<ResponseEntity<String>> entity = requestSpec
                .retrieve()
                .toEntity(String.class);

        ResponseEntity<String> response = entity.block();
        log.info("Integration test ResponseEntity: [{}]", response);
        return response;
    }
}
