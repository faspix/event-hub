package controller;

import com.faspix.CategoryApplication;
import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Category;
import com.faspix.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.CategoryFactory.*;

@SpringBootTest(classes = {CategoryApplication.class})
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockitoBean
    private EventServiceClient eventServiceClient;

    @BeforeEach
    void init() {
        categoryRepository.deleteAll();
    }


    @Test
    void findCategoriesTest_Success() throws Exception {
        Category category1 = categoryRepository.save(makeCategory());

        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(category1.getName()));
    }

    @Test
    void findCategoryByIdTest_Success() throws Exception {
        Category category = categoryRepository.save(makeCategory());

        mockMvc.perform(get("/categories/{categoryId}", category.getCategoryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(category.getCategoryId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    void findCategoryByIdTest_CategoryNotFound_Exception() throws Exception {
        mockMvc.perform(get("/categories/100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategoryTest_Success() throws Exception {
        RequestCategoryDTO categoryDTO = makeRequestCategory();

        MvcResult mvcResult = mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(categoryDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(categoryDTO.getName())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseCategoryDTO response = objectMapper.readValue(body, ResponseCategoryDTO.class);

        Category fromRepo = categoryRepository.findById(response.getCategoryId()).get();

        assertThat(fromRepo.getCategoryId(), equalTo(response.getCategoryId()));
        assertThat(fromRepo.getName(), equalTo(response.getName()));
    }

    @Test
    void createCategoryTest_CategoryAlreadyExist_Exception() throws Exception {
        RequestCategoryDTO categoryDTO = makeRequestCategory();

        mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(categoryDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        mockMvc.perform(post("/categories")
                .content(objectMapper.writeValueAsString(categoryDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());
    }

    @Test
    void editCategoryTest_Success() throws Exception {
        Category category = categoryRepository.save(makeCategory());
        RequestCategoryDTO updatedCategoryDTO = makeRequestCategory();
        updatedCategoryDTO.setName("Updated Name");

        mockMvc.perform(patch("/categories/{categoryId}", category.getCategoryId())
                        .content(objectMapper.writeValueAsString(updatedCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        Category updatedCategory = categoryRepository.findById(category.getCategoryId()).get();
        assertThat(updatedCategory.getName(), equalTo("Updated Name"));
    }

    @Test
    void editCategoryTest_CategoryNotFound_Exception() throws Exception {
        RequestCategoryDTO updatedCategoryDTO = makeRequestCategory();

        mockMvc.perform(patch("/categories/100")
                        .content(objectMapper.writeValueAsString(updatedCategoryDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoryTest_Success() throws Exception {
        Category category = categoryRepository.save(makeCategory());

        mockMvc.perform(delete("/categories/{categoryId}", category.getCategoryId()))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.existsById(category.getCategoryId()), equalTo(false));
    }

    @Test
    void deleteCategoryTest_CategoryNotFound_Exception() throws Exception {
        Long nonExistentCategoryId = 100L;

        mockMvc.perform(delete("/categories/{categoryId}", nonExistentCategoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoryTest_CategoryNotEmpty_Exception() throws Exception {
        Category category = categoryRepository.save(makeCategory());

        when(eventServiceClient.findEventsByCategoryId(category.getCategoryId()))
                .thenReturn(List.of(ResponseEventShortDTO.builder().build()));

        mockMvc.perform(delete("/categories/{categoryId}", category.getCategoryId()))
                .andExpect(status().isConflict());
    }

}
