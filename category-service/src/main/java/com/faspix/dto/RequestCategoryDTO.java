package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestCategoryDTO {

    @NotBlank(message = "Category name shouldn't be blank")
    private String name;

}
