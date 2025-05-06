package com.faspix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ResponseCompilationDTO {

    private Long id;

    private String title;

    private Boolean pinned;

}
