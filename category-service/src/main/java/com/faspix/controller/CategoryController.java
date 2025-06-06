package com.faspix.controller;

import com.faspix.dto.RequestCategoryDTO;
import com.faspix.shared.dto.ResponseCategoryDTO;
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

    @GetMapping
    public List<ResponseCategoryDTO> findCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return categoryService.findCategories(from, size);
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

    @DeleteMapping("{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
    }

}
