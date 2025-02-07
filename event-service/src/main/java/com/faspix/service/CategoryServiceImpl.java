package com.faspix.service;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.entity.Category;
import com.faspix.exception.CategoryNotFoundException;
import com.faspix.mapper.CategoryMapper;
import com.faspix.repository.CategoryRepository;
import com.faspix.utility.PageRequestMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findCategories(Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .toList();
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("Category with id " + categoryId + " not found")
        );
    }

    @Override
    public Category createCategory(RequestCategoryDTO categoryDTO) {
        Category category = categoryMapper.requestToCategory(categoryDTO);
        return categoryRepository.save(category);
    }

    @Override
    public Category editCategory(Long categoryId, RequestCategoryDTO categoryDTO) {
        Category category = findCategoryById(categoryId);
        Category updatedCategory = categoryMapper.requestToCategory(categoryDTO);
        updatedCategory.setCategoryId(category.getCategoryId());
        return categoryRepository.save(updatedCategory);
    }

    @Override
    public Boolean deleteCategory(Long categoryId) {
        findCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
        return true;
    }
}
