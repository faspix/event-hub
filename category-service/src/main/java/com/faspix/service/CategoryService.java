package com.faspix.service;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.entity.Category;

import java.util.List;

public interface CategoryService {

    List<ResponseCategoryDTO> findCategories(Integer page, Integer size);

    ResponseCategoryDTO findCategoryById(Long categoryId);

    ResponseCategoryDTO createCategory(RequestCategoryDTO categoryDTO);

    ResponseCategoryDTO editCategory(Long categoryId, RequestCategoryDTO categoryDTO);

    void deleteCategory(Long categoryId);

}
