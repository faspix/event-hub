package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.controller.EventController;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import com.faspix.service.EndpointStatisticsService;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.CommentFactory.makeRequestComment;
import static utility.EventFactory.*;
import static utility.UserFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockitoBean
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private EndpointStatisticsService endpointStatisticsService;

    @MockitoBean
    private StatisticsServiceClient statisticsServiceClient;

    @MockitoBean
    private CategoryServiceClient categoryServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventController eventController;

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
    void addCommentTest_Success() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        event.setState(EventState.PUBLISHED);
        RequestCommentDTO request = makeRequestComment();
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());

        MvcResult mvcResult = mockMvc.perform(post("/events/{eventId}/comment", event.getEventId())
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.text", is(request.getText())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseCommentDTO comment = objectMapper.readValue(body, ResponseCommentDTO.class);

        ResponseCommentDTO commentFromRepo = eventController.findEventById(event.getEventId(),
                        new MockHttpServletRequest())
                .getComments().getFirst();
        assertThat(comment.getId(), equalTo(commentFromRepo.getId()));
        assertThat(comment.getText(), equalTo(commentFromRepo.getText()));
    }


    @Test
    void addCommentTest_EventNotFound_Exception() throws Exception {
        RequestCommentDTO request = makeRequestComment();
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());

        mockMvc.perform(post("/events/{eventId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }


    @Test
    void addCommentTest_EventNotPublished_Exception() throws Exception {
        Event event = eventRepository.save(makeEventTest());
        RequestCommentDTO request = makeRequestComment();
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());

        mockMvc.perform(post("/events/{eventId}/comment", event.getEventId())
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

}

