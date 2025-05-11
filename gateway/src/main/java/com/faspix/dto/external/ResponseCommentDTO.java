package com.faspix.dto.external;

import java.time.Instant;

public record ResponseCommentDTO (
        Long id,
        ResponseUserShortDTO author,
        String text,
        Instant createdAt,
        Instant updatedAt
){}
