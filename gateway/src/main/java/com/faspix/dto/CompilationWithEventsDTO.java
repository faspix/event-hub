package com.faspix.dto;

import com.faspix.dto.external.ResponseEventShortDTO;

import java.util.List;

public record CompilationWithEventsDTO (
        Long id,
        String title,
        Boolean pinned,
        List<ResponseEventShortDTO> events
){}