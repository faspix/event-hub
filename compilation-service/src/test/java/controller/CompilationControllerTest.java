package controller;

import com.faspix.CompilationApplication;
import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.entity.Compilation;
import com.faspix.repository.CompilationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.hibernate.internal.build.AllowSysOut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ulitity.CompilationFactory.*;
import static ulitity.EventFactory.*;

@SpringBootTest(classes = {CompilationApplication.class})
@AutoConfigureMockMvc
public class CompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventServiceClient eventServiceClient;

    @Autowired
    private CompilationRepository compilationRepository;

    @BeforeEach
    void init() {
        compilationRepository.deleteAll();
    }

    @Test
    public void createCompilationTest_Success() throws Exception {
        RequestCompilationDTO requestDTO = makeRequestCompilation();
        when(eventServiceClient.getEventById(anyLong()))
                .thenReturn(makeResponseEventTest());

        MvcResult mvcResult = mockMvc.perform(post("/compilations")
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.title", is(requestDTO.getTitle())))
                .andExpectAll(jsonPath("$.pinned", is(requestDTO.getPinned())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseCompilationDTO result = objectMapper.readValue(body, ResponseCompilationDTO.class);

        Compilation fromRepo = compilationRepository.findById(result.getId()).get();

        assertThat(fromRepo.getTitle(), equalTo(requestDTO.getTitle()));
        assertThat(fromRepo.getPinned(), equalTo(requestDTO.getPinned()));

    }


    @Test
    public void createCompilationTest_CompilationAlreadyExist_Exception() throws Exception {
        RequestCompilationDTO requestDTO = makeRequestCompilation();
        when(eventServiceClient.getEventById(anyLong()))
                .thenReturn(makeResponseEventTest());

        mockMvc.perform(post("/compilations")
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        mockMvc.perform(post("/compilations")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @Test
    public void findCompilationByIdTest_Success() throws Exception {
        Compilation compilation = compilationRepository.save(makeCompilation());

        mockMvc.perform(get("/compilations/{compId}", compilation.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(compilation.getTitle())))
                .andExpect(jsonPath("$.pinned", is(compilation.getPinned())));
    }

    @Test
    public void findCompilationByIdTest_NotFound() throws Exception {
        Long compilationId = 100L;
        mockMvc.perform(get("/compilations/{compId}", compilationId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void findCompilationsTest_Success() throws Exception {
        compilationRepository.save(makeCompilation());
        Compilation comp2 = makeCompilation();
        comp2.setTitle("compilation 2 title");
        compilationRepository.save(comp2);

        mockMvc.perform(get("/compilations")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void editCompilationTest_Success() throws Exception {
        Compilation compilation = compilationRepository.save(makeCompilation());
        RequestCompilationDTO requestForUpdate = makeRequestCompilation();
        requestForUpdate.setTitle("new title");

        MvcResult mvcResult = mockMvc.perform(patch("/compilations/{compId}", compilation.getId())
                        .content(objectMapper.writeValueAsString(requestForUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.pinned", is(requestForUpdate.getPinned())))
                .andExpectAll(jsonPath("$.title", is(requestForUpdate.getTitle())))
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        ResponseCompilationDTO result = objectMapper.readValue(body, ResponseCompilationDTO.class);
        System.out.println(result);
        Compilation fromRepo = compilationRepository.findById(result.getId()).get();

        assertThat(fromRepo.getTitle(), equalTo(requestForUpdate.getTitle()));
        assertThat(fromRepo.getPinned(), equalTo(requestForUpdate.getPinned()));
    }

    @Test
    public void editCompilationTest_NotFound() throws Exception {
        Long compilationId = 100L;
        RequestCompilationDTO updatedRequest = makeRequestCompilation();

        mockMvc.perform(patch("/compilations/{compId}", compilationId)
                .content(objectMapper.writeValueAsString(updatedRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void deleteCompilationTest_Success() throws Exception {
        Compilation compilation = compilationRepository.save(makeCompilation());

        mockMvc.perform(delete("/compilations/{compId}", compilation.getId())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    public void deleteCompilationTest_NotFound() throws Exception {
        Long compilationId = 100L;
        mockMvc.perform(delete("/compilations/{compId}", compilationId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

}
