package com.faspix.controller;

import com.faspix.dto.EventsWithCommentsDTO;
import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseEventDTO;
import com.faspix.enums.CommentSortType;
import com.faspix.exception.EventNotFoundException;
import com.faspix.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventAggregationController {

    private final WebClient.Builder webClientBuilder;

    private final EventMapper eventMapper;

    @GetMapping("/event-with-comments/{eventId}")
    public Mono<EventsWithCommentsDTO> getEventWithComments(
            @PathVariable Long eventId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) CommentSortType sortType,
            ServerHttpRequest request
    ) {
        WebClient webClient = webClientBuilder.build();
        HttpHeaders headers = request.getHeaders();

        Mono<ResponseEventDTO> eventMono = webClient
                .get()
                .uri("lb://event-service/events/{eventId}", eventId)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleWebClientError)
                .bodyToMono(ResponseEventDTO.class);

        Mono<List<ResponseCommentDTO>> commentsMono = webClient
                .get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder
                            .scheme("lb")
                            .host("event-service")
                            .path("/events/comments/{eventId}")
                            .queryParamIfPresent("from", Optional.ofNullable(from))
                            .queryParamIfPresent("size", Optional.ofNullable(size))
                            .queryParamIfPresent("sortType", Optional.ofNullable(sortType));
                    return builder.build(eventId);
                })
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .bodyToFlux(ResponseCommentDTO.class)
                .collectList();

        return Mono.zip(eventMono, commentsMono)
                .map(tuple -> eventMapper.toEventsWithCommentsDTO(
                        tuple.getT1(), tuple.getT2()));
    }

    private <T> Mono<T> handleWebClientError(ClientResponse response) {
        if (response.statusCode().is4xxClientError()) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(
                            new EventNotFoundException("Event not found. Error: " + body))
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
