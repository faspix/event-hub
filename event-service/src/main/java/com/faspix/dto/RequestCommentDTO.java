package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestCommentDTO {

    @NotBlank(message = "Text shouldn't be blank")
    @Size(max = 5000, message = "Text cannot be greater then 5000 characters")
    private String text;

}
