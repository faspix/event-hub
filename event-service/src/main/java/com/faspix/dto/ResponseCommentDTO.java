package com.faspix.dto;

import com.faspix.shared.dto.ResponseUserShortDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCommentDTO {

    private Long id;

    private ResponseUserShortDTO author;

    private String text;

    private Instant createdAt;

    private Instant updatedAt;

}
