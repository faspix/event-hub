package com.faspix.controller;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.mapper.CategoryMapper;
import com.faspix.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return categoryService.findCategories(page, size)
                .stream()
                .map(categoryMapper::categoryToResponse)
                .toList();
    }

    @GetMapping("{categoryId}")
    public ResponseCategoryDTO findCategoryById(
            @PathVariable Long categoryId
    ) {
        return categoryMapper.categoryToResponse(
                categoryService.findCategoryById(categoryId)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCategoryDTO createCategory(
            @RequestBody RequestCategoryDTO categoryDTO
    ) {
        return categoryMapper.categoryToResponse(
                categoryService.createCategory(categoryDTO)
        );
    }

    @PatchMapping("{categoryId}")
    public ResponseCategoryDTO editCategory(
            @PathVariable Long categoryId,
            @RequestBody RequestCategoryDTO categoryDTO
    ) {
        return categoryMapper.categoryToResponse(
                categoryService.editCategory(categoryId, categoryDTO)
        );
    }

    @DeleteMapping("{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
    }

}
