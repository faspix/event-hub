package controller;


import com.faspix.UserApplication;
import com.faspix.config.KeycloakConfiguration;
import com.faspix.dao.UserDAO;
import com.faspix.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.TestSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import utility.UserFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {UserApplication.class}, properties = "keycloak.enabled=false")
@AutoConfigureMockMvc
@Testcontainers
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN", "MICROSERVICE"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDAO userDAO;

    @MockitoBean
    private RealmResource resource;

    @MockitoBean
    private KeycloakConfiguration keycloakConfiguration;

    @MockitoBean
    private Keycloak keycloak;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }


    @Test
    void searchUsers_Success() throws Exception {
        String nickname = "user";
        String email = "mail@mail.com";
        int page = 0;
        int size = 20;
        ResponseUserDTO responseDTO = UserFactory.makeResponseUser();

        when(userDAO.findUsers(nickname, email, page, size)).thenReturn(Collections.singletonList(responseDTO));

        MvcResult mvcResult = mockMvc.perform(get("/users")
                        .param("nickname", nickname)
                        .param("email", email)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseUserDTO> result = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(result.getFirst().getUserId(), equalTo(responseDTO.getUserId()));
        assertThat(result.getFirst().getUsername(), equalTo(responseDTO.getUsername()));
        assertThat(result.getFirst().getEmail(), equalTo(responseDTO.getEmail()));
        verify(userDAO, times(1)).findUsers(nickname, email, page, size);
    }

    @Test
    void findUserByIds_Success() throws Exception {
        Set<String> userIds = Set.of("1");
        ResponseUserShortDTO responseDTO = UserFactory.makeResponseUserShort();

        when(userDAO.findAll(userIds)).thenReturn(Collections.singletonList(responseDTO));

        MvcResult mvcResult = mockMvc.perform(post("/users/batch")
                        .content(objectMapper.writeValueAsString(userIds))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)))
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseUserShortDTO> result = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(result.getFirst().getUserId(), equalTo(responseDTO.getUserId()));
        assertThat(result.getFirst().getUsername(), equalTo(responseDTO.getUsername()));
        verify(userDAO, times(1)).findAll(userIds);
    }
}
