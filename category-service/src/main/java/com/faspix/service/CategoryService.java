package com.faspix.service;



import com.faspix.shared.dto.RequestCategoryDTO;
import com.faspix.shared.dto.ResponseCategoryDTO;

import java.util.List;

public interface CategoryService {

    List<ResponseCategoryDTO> findCategories(Integer from, Integer size);

    ResponseCategoryDTO findCategoryById(Long categoryId);

    ResponseCategoryDTO createCategory(RequestCategoryDTO categoryDTO);

    ResponseCategoryDTO editCategory(Long categoryId, RequestCategoryDTO categoryDTO);

    void deleteCategory(Long categoryId);

}
