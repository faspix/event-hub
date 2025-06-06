package com.faspix.client;

import com.faspix.shared.dto.ResponseCategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service",
        fallback = CategoryServiceClientFallback.class)
public interface CategoryServiceClient {

    @GetMapping("categories/{categoryId}")
    ResponseCategoryDTO getCategoryById(@PathVariable Long categoryId);

}
