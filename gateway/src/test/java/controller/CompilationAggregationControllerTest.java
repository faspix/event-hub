package controller;

import com.faspix.GatewayApplication;
import com.faspix.controller.CompilationAggregationController;
import com.faspix.dto.CompilationWithEventsDTO;
import com.faspix.dto.external.ResponseCompilationDTO;
import com.faspix.dto.external.ResponseEventShortDTO;
import com.faspix.exception.CompilationNotFoundException;
import confg.TestSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utility.CompilationFactory.makeCompilation;
import static utility.EventFactory.makeEventShort;
@WebFluxTest(controllers = CompilationAggregationController.class)
@ContextConfiguration(classes = GatewayApplication.class)
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN", "MICROSERVICE"})
class CompilationAggregationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private WebClient.Builder webClientBuilder;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockitoBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @MockitoBean
    private WebClient.RequestBodySpec requestBodySpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
    }

    @Test
    void getCompilationWithEvents_Success() {
        Long compId = 1L;
        Integer from = 0;
        Integer size = 20;

        ResponseCompilationDTO compilationDTO = makeCompilation();
        ResponseEventShortDTO eventShortDTO = makeEventShort();
        List<ResponseEventShortDTO> events = List.of(eventShortDTO);
        CompilationWithEventsDTO resultDTO = new CompilationWithEventsDTO(
                compilationDTO.id(),
                compilationDTO.title(),
                compilationDTO.pinned(),
                events
        );

        when(requestHeadersUriSpec.uri("lb://compilation-service/compilations/{compId}", compId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseCompilationDTO.class))
                .thenReturn(Mono.just(compilationDTO));

        when(requestBodyUriSpec.uri(any(Function.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(compilationDTO.events()))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseEventShortDTO.class))
                .thenReturn(Flux.fromIterable(events));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/compilations/compilation-with-events/{compId}")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build(compId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompilationWithEventsDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    void getCompilationWithEvents_NoQueryParams_Success() {
        Long compId = 1L;

        ResponseCompilationDTO compilationDTO = makeCompilation();
        List<ResponseEventShortDTO> events = Collections.emptyList();
        CompilationWithEventsDTO resultDTO = new CompilationWithEventsDTO(
                compilationDTO.id(),
                compilationDTO.title(),
                compilationDTO.pinned(),
                events
        );

        when(requestHeadersUriSpec.uri("lb://compilation-service/compilations/{compId}", compId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseCompilationDTO.class))
                .thenReturn(Mono.just(compilationDTO));

        when(requestBodyUriSpec.uri(any(Function.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseEventShortDTO.class))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/compilations/compilation-with-events/{compId}", compId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompilationWithEventsDTO.class)
                .isEqualTo(resultDTO);
    }

    @Test
    void getCompilationWithEvents_CompilationNotFound_ReturnsError() {
        Long compId = 1L;

        when(requestHeadersUriSpec.uri("lb://compilation-service/compilations/{compId}", compId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseCompilationDTO.class))
                .thenReturn(Mono.error(new CompilationNotFoundException("Compilation not found")));

         when(requestBodyUriSpec.uri(any(Function.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseEventShortDTO.class))
                .thenReturn(Flux.empty());


        webTestClient.get()
                .uri("/compilations/compilation-with-events/{compId}", compId)
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Test
    void getCompilationWithEvents_ReturnsRuntimeException() {
        Long compId = 1L;

        when(requestHeadersUriSpec.uri("lb://compilation-service/compilations/{compId}", compId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ResponseCompilationDTO.class))
                .thenReturn(Mono.error(new RuntimeException()));

        when(requestBodyUriSpec.uri(any(Function.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);
        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(ResponseEventShortDTO.class))
                .thenReturn(Flux.empty());


        webTestClient.get()
                .uri("/compilations/compilation-with-events/{compId}", compId)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
