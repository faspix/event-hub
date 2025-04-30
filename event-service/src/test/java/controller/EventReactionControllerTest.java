package controller;

import com.faspix.EventApplication;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.entity.Event;
import com.faspix.entity.EventDislike;
import com.faspix.entity.EventLike;
import com.faspix.repository.EventDislikeRepository;
import com.faspix.repository.EventLikeRepository;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import confg.TestSecurityConfiguration;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.makeEventTest;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class EventReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventLikeRepository eventLikeRepository;

    @Autowired
    private EventDislikeRepository eventDislikeRepository;

    @MockitoBean
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @MockitoBean
    private StatisticsServiceClient statisticsServiceClient;

    @MockitoBean
    private EventSearchRepository eventSearchRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
    }

    @Test
    void likeEventTest_Success() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        mockMvc.perform(post("/events/{eventId}/like", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isCreated());
    }

    @Test
    void likeEventTest_EventNotFound_Exception() throws Exception {
        mockMvc.perform(post("/events/{eventId}/like", 1L)
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void dislikeEventTest_Success() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        mockMvc.perform(post("/events/{eventId}/dislike", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isCreated());
    }

    @Test
    void dislikeEventTest_EventNotFound_Exception() throws Exception {
        mockMvc.perform(post("/events/{eventId}/dislike", 1L)
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeLikeEventTest_Success() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        eventLikeRepository.save(new EventLike("1", event));

        mockMvc.perform(delete("/events/{eventId}/like", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeLikeEventTest_NotLiked_Exception() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        mockMvc.perform(delete("/events/{eventId}/like", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeDislikeEventTest_Success() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        eventDislikeRepository.save(new EventDislike("1", event));

        mockMvc.perform(delete("/events/{eventId}/dislike", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeDislikeEventTest_NotDisliked_Exception() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        mockMvc.perform(delete("/events/{eventId}/dislike", event.getEventId())
                        .header("Authorization", "Bearer 123123"))
                .andExpect(status().isNotFound());
    }

}
