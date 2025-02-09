package com.faspix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCompilationDTO {

    private Long id;

    private String title;

    private Boolean pinned;

    private List<ResponseEventShortDTO> events;

}
