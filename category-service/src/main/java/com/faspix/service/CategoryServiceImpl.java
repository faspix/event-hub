package com.faspix.service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;
import com.faspix.exception.CategoryAlreadyExistException;
import com.faspix.exception.CategoryNotEmptyException;
import com.faspix.exception.CategoryNotFoundException;
import com.faspix.mapper.CategoryMapper;
import com.faspix.dao.CategoryRepository;
import com.faspix.utility.PageRequestMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    private final EventServiceClient eventServiceClient;

    private final CacheManager cacheManager;

    @Override
    public List<ResponseCategoryDTO> findCategories(Integer page, Integer size) {
        Pageable pageRequest = PageRequestMaker.makePageRequest(page, size);
        return categoryRepository.findAll(pageRequest)
                .stream()
                .map(categoryMapper::categoryToResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "CategoryService::findCategoryById", key = "#categoryId")
    public ResponseCategoryDTO findCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("Category with id " + categoryId + " not found")
        );
        return categoryMapper.categoryToResponse(category);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseCategoryDTO createCategory(RequestCategoryDTO categoryDTO) {
        Category category = categoryMapper.requestToCategory(categoryDTO);
        try {
            ResponseCategoryDTO responseCategory = categoryMapper.categoryToResponse(
                    categoryRepository.saveAndFlush(category)
            );

            Cache cache = cacheManager.getCache("CategoryService::findCategoryById");
            if (cache != null) {
                cache.put(category.getCategoryId(), responseCategory);
            } else {
                log.error("Cache CategoryService::findCategoryById is null");
            }

            return responseCategory;

        } catch (DataIntegrityViolationException e) {
            throw new CategoryAlreadyExistException("Category with name '" + category.getName() + "' already exist");
        }
    }

    @Override
    @Transactional
    @CachePut(value = "CategoryService::findCategoryById", key = "#categoryId")
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    @CacheEvict(value = "CategoryService::findCategoryById", key = "#categoryId")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category with id " + categoryId + " not found");
        }
        if (!eventServiceClient.findEventsByCategoryId(categoryId).isEmpty())
            throw new CategoryNotEmptyException("Category with id " + categoryId + " is not empty");

        categoryRepository.deleteById(categoryId);
    }
}
