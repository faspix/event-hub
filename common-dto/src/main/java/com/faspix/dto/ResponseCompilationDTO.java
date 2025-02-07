package com.faspix.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseCompilationDTO {

    private Long id;

    private String title;

    private Boolean pinned;

    private List<ResponseEventShortDTO> events;

}
