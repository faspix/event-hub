package controller;

import com.faspix.StatisticsApplication;
import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import com.faspix.repository.StatisticsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import confg.TestSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.StatisticsFactory.*;

@SpringBootTest(classes = {StatisticsApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @MockitoBean
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

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
        statisticsRepository.deleteAll();
    }

    @Test
    void hitEndpointTest_Success() throws Exception {
        RequestEndpointStatsDTO request = makeRequestEndpoint();

        mockMvc.perform(post("/statistics/hit")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        EndpointStats fromRepo = statisticsRepository.findAll().getFirst();
        assertThat(request.getApp(), equalTo(fromRepo.getApp()));
        assertThat(request.getIp(), equalTo(fromRepo.getIp()));
        assertThat(request.getUri(), equalTo(fromRepo.getUri()));
        assertThat(request.getTimestamp(), equalTo(fromRepo.getTimestamp()));
    }

    @Test
    void getEndpointStatsTest_Success() throws Exception {
        statisticsRepository.save(makeEndpointStats());
        EndpointStats endpointStats = statisticsRepository.save(makeEndpointStats());

        MvcResult mvcResult = mockMvc.perform(get("/statistics/stats")
                        .param("start", "1990-09-06 11:00:23")
                        .param("end", "2020-09-06 11:00:23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.length()", equalTo(1)))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEndpointStatsDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.getFirst().getUri(), equalTo(endpointStats.getUri()));
        assertThat(events.getFirst().getApp(), equalTo(endpointStats.getApp()));
        assertThat(events.getFirst().getHits(), equalTo(2L));
    }


    @Test
    void getEndpointStatsTest_Uniq_Success() throws Exception {
        statisticsRepository.save(makeEndpointStats());
        EndpointStats endpointStats = statisticsRepository.save(makeEndpointStats());

        MvcResult mvcResult = mockMvc.perform(get("/statistics/stats")
                                .param("start", "1990-09-06 11:00:23")
                                .param("end", "2020-09-06 11:00:23")
                                .param("unique", "true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.length()", equalTo(1)))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEndpointStatsDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.getFirst().getUri(), equalTo(endpointStats.getUri()));
        assertThat(events.getFirst().getApp(), equalTo(endpointStats.getApp()));
        assertThat(events.getFirst().getHits(), equalTo(1L));
    }

}
