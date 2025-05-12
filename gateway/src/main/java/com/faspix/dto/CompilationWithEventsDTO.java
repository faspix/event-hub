package com.faspix.dto;

import com.faspix.dto.external.ResponseEventShortDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationWithEventsDTO {

    Long id;

    String title;

    Boolean pinned;

    List<ResponseEventShortDTO> events;

}