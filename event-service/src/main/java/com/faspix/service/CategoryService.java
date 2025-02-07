package com.faspix.service;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findCategories(Integer page, Integer size);

    Category findCategoryById(Long categoryId);

    Category createCategory(RequestCategoryDTO categoryDTO);

    Category editCategory(Long categoryId, RequestCategoryDTO categoryDTO);

    Boolean deleteCategory(Long categoryId);

}
