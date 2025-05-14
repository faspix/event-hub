package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.controller.EventController;
import com.faspix.domain.entity.Event;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class InternalEventControllerTest {

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
    public void isEventsExistsInCategory_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(get("/events/categories/exists")
                        .param("id", savedEvent.getCategoryId().toString())
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        boolean boolBody = Boolean.parseBoolean(body);

        assertThat(boolBody, equalTo(true));
    }


    @Test
    public void isEventExists_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(get("/events/exists")
                        .param("id", savedEvent.getCategoryId().toString())
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        boolean boolBody = Boolean.parseBoolean(body);

        assertThat(boolBody, equalTo(true));
    }

}
