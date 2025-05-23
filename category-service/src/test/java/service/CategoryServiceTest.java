package service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCategoryDTO;
import com.faspix.entity.Category;
import com.faspix.exception.CategoryAlreadyExistException;
import com.faspix.exception.CategoryNotEmptyException;
import com.faspix.exception.CategoryNotFoundException;
import com.faspix.mapper.CategoryMapper;
import com.faspix.repository.CategoryRepository;
import com.faspix.service.CategoryService;
import com.faspix.shared.dto.ResponseCategoryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static utility.CategoryFactory.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventServiceClient eventServiceClient;

    @Mock
    private CacheManager cacheManager;

    @Spy
    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);


    @Test
    public void findCategoryByIdTest_Success() {
        Category category = makeCategory();
        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(category));

        ResponseCategoryDTO result = categoryService.findCategoryById(1L);

        assertThat(result.getCategoryId(), equalTo(category.getCategoryId()));
        assertThat(result.getName(), equalTo(category.getName()));

    }

    @Test
    public void findCategoriesTest_Success() {
        List<Category> categories = List.of(makeCategory());
        Page<Category> categoryPage = new PageImpl<>(categories);
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(categoryPage);

        List<ResponseCategoryDTO> result = categoryService.findCategories(0, 10);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getName(), equalTo(categories.getFirst().getName()));
    }

    @Test
    public void createCategoryTest_Success() {
        RequestCategoryDTO requestDTO = makeRequestCategory();
        Category category = makeCategory();
        when(categoryRepository.saveAndFlush(any()))
                .thenReturn(category);

        ResponseCategoryDTO result = categoryService.createCategory(requestDTO);

        assertThat(result.getName(), equalTo(category.getName()));
        assertThat(result.getCategoryId(), equalTo(category.getCategoryId()));

        verify(categoryRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void createCategoryTest_AlreadyExists() {
        RequestCategoryDTO requestDTO = makeRequestCategory();
        when(categoryRepository.saveAndFlush(any()))
                .thenThrow(DataIntegrityViolationException.class);

        CategoryAlreadyExistException exception = assertThrows(CategoryAlreadyExistException.class,
                () -> categoryService.createCategory(requestDTO)
        );
        assertEquals("Category with name 'Name' already exist", exception.getMessage());
    }

    @Test
    public void editCategoryTest_Success() {
        RequestCategoryDTO requestDTO = makeRequestCategory();
        Category existingCategory = makeCategory();
        Category updatedCategory = makeCategory();
        updatedCategory.setName("Updated Name");

        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(existingCategory));
        when(categoryRepository.saveAndFlush(any()))
                .thenReturn(updatedCategory);

        ResponseCategoryDTO result = categoryService.editCategory(1L, requestDTO);
        assertThat(result.getName(), equalTo(updatedCategory.getName()));
        verify(categoryRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void editCategoryTest_NotFound() {
        RequestCategoryDTO requestDTO = makeRequestCategory();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> categoryService.editCategory(1L, requestDTO)
        );
        assertEquals("Category with id 1 not found", exception.getMessage());
    }

    @Test
    public void deleteCategoryTest_Success() {
        when(categoryRepository.existsById(anyLong()))
                .thenReturn(true);
        when(eventServiceClient.isEventsExistsInCategory(anyLong()))
                .thenReturn(false);

        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void deleteCategoryTest_NotFound() {
        when(categoryRepository.existsById(anyLong()))
                .thenReturn(false);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(1L)
        );
        assertEquals("Category with id 1 not found", exception.getMessage());
    }

    @Test
    public void deleteCategoryTest_NotEmpty() {
        when(categoryRepository.existsById(anyLong()))
                .thenReturn(true);
        when(eventServiceClient.isEventsExistsInCategory(anyLong()))
                .thenReturn(true);

        CategoryNotEmptyException exception = assertThrows(CategoryNotEmptyException.class,
                () -> categoryService.deleteCategory(1L)
        );
        assertEquals("Category with id 1 is not empty", exception.getMessage());
    }

}
