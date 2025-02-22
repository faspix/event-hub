package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.controller.EventController;
import com.faspix.dto.*;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import com.faspix.service.EventService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.CommentFactory.makeRequestComment;
import static utility.EventFactory.*;
import static utility.UserFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private CategoryServiceClient categoryServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventController eventController;

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
    }

    @Test
    void addCommentTest_Success() throws Exception {
        Long userId = 1L;
        Event event = eventRepository.save(makeEventTest());
        event.setState(EventState.PUBLISHED);
        RequestCommentDTO request = makeRequestComment();
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());

        MvcResult mvcResult = mockMvc.perform(post("/events/{eventId}/comment", event.getEventId())
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.text", is(request.getText())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseCommentDTO comment = objectMapper.readValue(body, ResponseCommentDTO.class);

        ResponseCommentDTO commentFromRepo = eventController.findEventById(event.getEventId()).getComments().getFirst();
        assertThat(comment.getId(), equalTo(commentFromRepo.getId()));
        assertThat(comment.getText(), equalTo(commentFromRepo.getText()));
    }


    @Test
    void addCommentTest_EventNotFound_Exception() throws Exception {
        Long userId = 1L;
        RequestCommentDTO request = makeRequestComment();
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());

        mockMvc.perform(post("/events/{eventId}/comment", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }



}

