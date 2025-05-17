package controller;

import com.faspix.GatewayApplication;
import com.faspix.controller.EventAggregationController;
import com.faspix.dto.EventsWithCommentsDTO;
import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseEventDTO;
import com.faspix.enums.CommentSortType;
import com.faspix.exception.EventNotFoundException;
import com.faspix.mapper.EventMapper;
import confg.TestSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static utility.CommentFactory.makeComment;
import static utility.EventFactory.makeEventWithComments;
import static utility.EventFactory.makeResponseEvent;

@WebFluxTest(controllers = EventAggregationController.class)
@ContextConfiguration(classes = GatewayApplication.class)
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN", "MICROSERVICE"})
class EventAggregationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private WebClient.Builder webClientBuilder;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockitoBean
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {

        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void getEventWithComments_Success() {
        Long eventId = 1L;
        Integer from = 0;
        Integer size = 10;
        CommentSortType sortType = CommentSortType.ASC;

        ResponseEventDTO eventDTO = makeResponseEvent();
        ResponseCommentDTO commentDTO = makeComment();
        List<ResponseCommentDTO> comments = List.of(commentDTO);
        EventsWithCommentsDTO resultDTO = makeEventWithComments();

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("lb://event-service/events/{eventId}", eventId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseEventDTO.class))
                .thenReturn(Mono.just(eventDTO));
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseCommentDTO.class))
                .thenReturn(Flux.fromIterable(comments));

        when(eventMapper.toEventsWithCommentsDTO(eventDTO, comments))
                .thenReturn(resultDTO);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/event-with-comments/{eventId}")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .queryParam("sortType", sortType)
                        .build(eventId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventsWithCommentsDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    void getEventWithComments_NoQueryParams_Success() {
        Long eventId = 1L;

        ResponseEventDTO eventDTO = makeResponseEvent();
        List<ResponseCommentDTO> comments = List.of();
        EventsWithCommentsDTO resultDTO = makeEventWithComments();

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("lb://event-service/events/{eventId}", eventId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseEventDTO.class))
                .thenReturn(Mono.just(eventDTO));
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseCommentDTO.class))
                .thenReturn(Flux.fromIterable(comments));

        when(eventMapper.toEventsWithCommentsDTO(eventDTO, comments))
                .thenReturn(resultDTO);

        webTestClient.get()
                .uri("/events/event-with-comments/{eventId}", eventId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EventsWithCommentsDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    void getEventWithComments_EventNotFound_ReturnsError() {
        Long eventId = 1L;
        ResponseCommentDTO commentDTO = makeComment();
        List<ResponseCommentDTO> comments = List.of(commentDTO);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("lb://event-service/events/{eventId}", eventId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseEventDTO.class))
                .thenReturn(Mono.error(new EventNotFoundException("Event not found")));

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseCommentDTO.class))
                .thenReturn(Flux.fromIterable(comments));

        webTestClient.get()
                .uri("/events/event-with-comments/{eventId}", eventId)
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Test
    void getEventWithComments_ReturnsRuntimeException() {
        Long eventId = 1L;
        ResponseCommentDTO commentDTO = makeComment();
        List<ResponseCommentDTO> comments = List.of(commentDTO);

        when(webClient.get())
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("lb://event-service/events/{eventId}", eventId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseEventDTO.class))
                .thenReturn(Mono.error(new RuntimeException()));

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseCommentDTO.class))
                .thenReturn(Flux.fromIterable(comments));

        webTestClient.get()
                .uri("/events/event-with-comments/{eventId}", eventId)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
