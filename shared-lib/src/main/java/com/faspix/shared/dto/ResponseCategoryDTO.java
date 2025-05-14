package com.faspix.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseCategoryDTO implements Serializable {

    private Long categoryId;

    private String name;

}
