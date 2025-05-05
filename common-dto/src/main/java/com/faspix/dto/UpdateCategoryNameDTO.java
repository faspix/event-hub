package com.faspix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryNameDTO {

    @NotNull(message = "Category id cannot be null")
    private Long categoryId;

    @NotNull(message = "Category name cannot be null")
    private String categoryName;

}
