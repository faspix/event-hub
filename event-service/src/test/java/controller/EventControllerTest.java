package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.controller.EventController;
import com.faspix.domain.entity.Event;
import com.faspix.domain.enums.EventStateAction;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.utility.EventState;
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

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.CategoryFactory.makeResponseCategory;
import static utility.EventFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class EventControllerTest {

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
    public void createEventTest_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(categoryServiceClient.getCategoryById(any()))
                .thenReturn((makeResponseCategory()));

        MvcResult mvcResult = mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO event = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(event.getEventId()).get();

        assertThat(event.getEventId(), equalTo(event1.getEventId()));
        assertThat(event.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(event.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(event1.getPaid()));
        assertThat(event.getDescription(), equalTo(event1.getDescription()));
        assertThat(event.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(event1.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));
    }


    @Test
    public void createEventTest_EventStartsToSoon_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2).plusMinutes(1));
        when(categoryServiceClient.getCategoryById(any()))
                .thenReturn((makeResponseCategory()));

        MvcResult mvcResult = mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));
    }

    @Test
    public void createEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2));

        mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());

    }

    @Test
    public void editEventTest_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));
    }


    @Test
    public void    editEventTest_EventStartsToSoon_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2).plusMinutes(1));
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));

    }

    @Test
    public void editEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2));
        Event savedEvent = eventRepository.save(makeEventTest());

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(requestEventDTO))
                .header("Authorization", "Bearer 123123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }


    @Test
    public void editEventTest_UserDidntCreateEvent_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setInitiatorId("22");

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());

    }

    @Test
    public void editEventTest_PublishedEvent_Exception() throws Exception {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        RequestEventDTO requestEventDTO = makeRequestEventTest();

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(requestEventDTO))
                .header("Authorization", "Bearer 123123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void adminEditEventTest_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();

        MvcResult mvcResult = mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        // Data from DB
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));

        // Response from controller
        assertThat(response.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(response.getPaid(), equalTo(adminDTO.getPaid()));
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        event.setEventDate(OffsetDateTime.now().plusHours(1));
        Event savedEvent = eventRepository.save(event);

       mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminRequest))
                       .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Success() throws Exception {
        Event event = makeEventTest();
        event.setEventDate(OffsetDateTime.now().plusHours(2));
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();

        MvcResult mvcResult = mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminDTO))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        // Data from DB
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));

        // Response from controller
        assertThat(response.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(response.getPaid(), equalTo(adminDTO.getPaid()));

    }

    @Test
    public void adminEditEventTest_InvalidState_Exception() throws Exception {
        Event event = makeEventTest();
        event.setState(EventState.CANCELED);
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        adminRequest.setStateAction(EventStateAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(adminRequest))
                .header("Authorization", "Bearer 123123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

}
