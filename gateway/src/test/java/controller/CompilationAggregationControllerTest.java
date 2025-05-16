package controller;

import com.faspix.GatewayApplication;
import com.faspix.controller.CompilationAggregationController;
import com.faspix.dto.CompilationWithEventsDTO;
import com.faspix.dto.external.ResponseCompilationDTO;
import com.faspix.dto.external.ResponseEventShortDTO;
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

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        reset(webClient, requestHeadersUriSpec, requestBodyUriSpec, requestHeadersSpec, requestBodySpec, responseSpec);

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
                .thenReturn(Mono.error(new RuntimeException("Compilation not found")));

        webTestClient.get()
                .uri("/compilations/compilation-with-events/{compId}", compId)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
