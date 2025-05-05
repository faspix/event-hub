package com.faspix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUsernameDTO {

    @NotNull(message = "User id cannot be null")
    private String userId;

    @NotNull(message = "Username cannot be null")
    private String username;

}
