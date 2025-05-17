package com.faspix.controller;

import com.faspix.dto.CompilationWithEventsDTO;
import com.faspix.dto.external.ResponseCompilationDTO;
import com.faspix.dto.external.ResponseEventShortDTO;
import com.faspix.exception.CompilationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationAggregationController {

    private final WebClient.Builder webClientBuilder;

    @GetMapping("/compilation-with-events/{compId}")
    public Mono<CompilationWithEventsDTO> findCompilationWithEvents(
            @PathVariable Long compId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        WebClient webClient = webClientBuilder.build();

        Mono<ResponseCompilationDTO> compilationMono = webClient
                .get()
                .uri("lb://compilation-service/compilations/{compId}", compId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleWebClientError)
                .bodyToMono(ResponseCompilationDTO.class);

        Mono<List<ResponseEventShortDTO>> eventsMono = compilationMono
                .flatMap(compilation -> {
                    List<Long> eventIds = compilation.events();

                    if (eventIds == null || eventIds.isEmpty()) {
                        return Mono.just(Collections.emptyList());
                    }
                    return webClient
                            .post()
                            .uri(uriBuilder -> {
                                UriBuilder builder = uriBuilder
                                        .scheme("lb")
                                        .host("event-service")
                                        .path("/events/batch")
                                        .queryParamIfPresent("from", Optional.ofNullable(from))
                                        .queryParamIfPresent("size", Optional.ofNullable(size));
                                return builder.build();
                            })
                            .bodyValue(eventIds)
                            .retrieve()
                            .bodyToFlux(ResponseEventShortDTO.class)
                            .collectList();
                });

        return compilationMono
                .zipWith(eventsMono)
                .map(tuple -> {
                    ResponseCompilationDTO compilation = tuple.getT1();
                    List<ResponseEventShortDTO> events = tuple.getT2();
                    return new CompilationWithEventsDTO(
                            compilation.id(),
                            compilation.title(),
                            compilation.pinned(),
                            events
                    );
                });
    }


    private <T> Mono<T> handleWebClientError(ClientResponse response) {
        if (response.statusCode().is4xxClientError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(
                            new CompilationNotFoundException("Compilation not found. Error: " + body))
                    );
        } else if (response.statusCode().is5xxServerError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(
                            new RuntimeException("Server error: " + body))
                    );
        }
        return response.createException().flatMap(Mono::error);
    }

}
