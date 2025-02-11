package com.faspix.controller;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.mapper.CategoryMapper;
import com.faspix.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<ResponseCategoryDTO> findCategories(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return categoryService.findCategories(page, size);
    }

    @GetMapping("{categoryId}")
    public ResponseCategoryDTO findCategoryById(
            @PathVariable Long categoryId
    ) {
        return categoryService.findCategoryById(categoryId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCategoryDTO createCategory(
            @RequestBody RequestCategoryDTO categoryDTO
    ) {
        return categoryService.createCategory(categoryDTO);
    }

    @PatchMapping("{categoryId}")
    public ResponseCategoryDTO editCategory(
            @PathVariable Long categoryId,
            @RequestBody RequestCategoryDTO categoryDTO
    ) {
        return categoryService.editCategory(categoryId, categoryDTO);
    }

    // TODO: return value
    @DeleteMapping("{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
    }

}
