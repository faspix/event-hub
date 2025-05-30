package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.controller.EventController;
import com.faspix.domain.entity.Event;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.dto.ResponseEventShortDTO;
import com.faspix.shared.utility.EventState;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import confg.TestSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class EventSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventController eventController;

    @MockitoBean
    private StatisticsServiceClient statisticsServiceClient;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @MockitoBean
    private CategoryServiceClient categoryServiceClient;

    @Autowired
    private EventSearchRepository eventSearchRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @Container
    private static final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.15.0"))
            .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL properties
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);


        // Elasticsearch properties
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
    }

    @Test
    public void findEventByIdTest_Success() throws Exception {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        Event savedEvent = eventRepository.save(event);


        MvcResult mvcResult = mockMvc.perform(get("/events/{eventId}", savedEvent.getEventId())
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO result = objectMapper.readValue(body, ResponseEventDTO.class);

        assertThat(result.getEventId(), equalTo(savedEvent.getEventId()));
        assertThat(result.getTitle(), equalTo(savedEvent.getTitle()));
        assertThat(result.getAnnotation(), equalTo(savedEvent.getAnnotation()));
        assertThat(result.getDescription(), equalTo(savedEvent.getDescription()));
        assertThat(result.getPaid(), equalTo(savedEvent.getPaid()));
        assertThat(result.getState(), equalTo(savedEvent.getState()));
    }

    @Test
    public void findEventByIdTest_EventNotFound_ThrowsException() throws Exception {
        Long eventId = 100L;
        mockMvc.perform(get("/events/{eventId}", eventId)
                .header("Authorization", "Bearer 123123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void findAllUserEventsTest_Success() throws Exception {
        Long eventId = 1L;
        Event event1 = makeEventTest();
        event1.setTitle("Title 1");
        event1.setEventId(null);
        eventRepository.save(event1);

        Event event2 = makeEventTest();
        event2.setTitle("Title 2");
        event2.setEventId(null);
        eventRepository.save(event2);

        MvcResult mvcResult = mockMvc.perform(get("/events/users/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(2));
        assertThat(events.get(0).getTitle(), equalTo(event1.getTitle()));
        assertThat(events.get(1).getTitle(), equalTo(event2.getTitle()));
    }

    @Test
    public void findAllUserEventsTest_UserHasNoEvents_ReturnsEmptyList() throws Exception {
        Long eventId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/events/users/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(0));
    }

    @Test
    public void findEventsByIdsTest_Success() throws Exception {
        Event event1 = makeEventTest();
        event1.setState(EventState.PUBLISHED);
        eventRepository.save(event1);

        Event event2 = makeEventTest();
        event2.setState(EventState.PUBLISHED);
        eventRepository.save(event2);

        Set<Long> eventIds = Set.of(event1.getEventId(), event2.getEventId());

        MvcResult mvcResult = mockMvc.perform(post("/events/batch")
                        .content(objectMapper.writeValueAsString(eventIds))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(2));
        assertThat(events, hasItems(
                hasProperty("eventId", is(event1.getEventId())),
                hasProperty("eventId", is(event2.getEventId()))
        ));
    }

    @Test
    public void findEventsByIdsTest_EmptyList() throws Exception {
        Set<Long> eventIds = Set.of();

        MvcResult mvcResult = mockMvc.perform(post("/events/batch")
                        .content(objectMapper.writeValueAsString(eventIds))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(0));
    }

    @Test
    public void findEventsTest_NoResults() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/events")
                        .param("text", "NonExistentEvent")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(0));
    }

    @Test
    public void findEventsByAdminTest_NoResults() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/events/admin/search")
                        .param("users", "999")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(0));
    }



}
