package com.faspix.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
public class RequestCategoryDTO {

    @NotBlank(message = "Category name shouldn't be blank")
    private String name;

}
