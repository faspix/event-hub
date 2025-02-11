package com.faspix.service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;
import com.faspix.exception.CategoryAlreadyExistException;
import com.faspix.exception.CategoryNotEmptyException;
import com.faspix.exception.CategoryNotFoundException;
import com.faspix.mapper.CategoryMapper;
import com.faspix.repository.CategoryRepository;
import com.faspix.utility.PageRequestMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    private final EventServiceClient eventServiceClient;

    @Override
    public List<ResponseCategoryDTO> findCategories(Integer page, Integer size) {
        Pageable pageRequest = PageRequestMaker.makePageRequest(page, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(categoryMapper::categoryToResponse)
                .toList();
    }

    @Override
    public ResponseCategoryDTO findCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("Category with id " + categoryId + " not found")
        );
        return categoryMapper.categoryToResponse(category);
    }

    @Override
    @Transactional
    public ResponseCategoryDTO createCategory(RequestCategoryDTO categoryDTO) {
        Category category = categoryMapper.requestToCategory(categoryDTO);
        try {
            return categoryMapper.categoryToResponse(
                    categoryRepository.saveAndFlush(category)
            );
        } catch (DataIntegrityViolationException e) {
            throw new CategoryAlreadyExistException("Category with name '" + category.getName() + "' already exist");
        }
    }

    @Override
    @Transactional
    public ResponseCategoryDTO editCategory(Long categoryId, RequestCategoryDTO categoryDTO) {
        ResponseCategoryDTO category = findCategoryById(categoryId);
        Category updatedCategory = categoryMapper.requestToCategory(categoryDTO);
        updatedCategory.setCategoryId(category.getCategoryId());
        try {
            return categoryMapper.categoryToResponse(
                    categoryRepository.saveAndFlush(updatedCategory)
            );
        } catch (DataIntegrityViolationException e) {
            throw new CategoryAlreadyExistException("Category with name '" + updatedCategory.getName() + "' already exist");
        }

    }

    @Override
    @Transactional
    public Boolean deleteCategory(Long categoryId) {
        findCategoryById(categoryId);
        if (! eventServiceClient.findEventsByCategoryId(categoryId).isEmpty())
            throw new CategoryNotEmptyException("Category with id " + categoryId + " is not empty");
        categoryRepository.deleteById(categoryId);
        return true;
    }
}
