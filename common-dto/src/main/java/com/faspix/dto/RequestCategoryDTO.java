package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;

public class RequestCategoryDTO {

    @NotBlank(message = "Category name shouldn't be blank")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
