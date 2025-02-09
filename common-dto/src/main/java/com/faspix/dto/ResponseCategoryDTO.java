package com.faspix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCategoryDTO {

    private Long categoryId;

    private String name;

}
