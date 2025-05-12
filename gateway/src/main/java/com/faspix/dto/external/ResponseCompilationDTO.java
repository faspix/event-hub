package com.faspix.dto.external;

import java.util.List;

public record ResponseCompilationDTO(
        Long id,
        String title,
        Boolean pinned,
        List<Long> events
){}
