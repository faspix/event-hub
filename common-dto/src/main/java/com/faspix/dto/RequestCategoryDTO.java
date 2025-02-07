package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
public class RequestCategoryDTO {

    @NotBlank(message = "Category name shouldn't be blank")
    private String name;

}
