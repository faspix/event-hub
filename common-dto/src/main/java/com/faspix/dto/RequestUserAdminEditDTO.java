package com.faspix.dto;

import com.faspix.roles.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestUserAdminEditDTO {

    private String username;

    private String email;

    private UserRoles addRole;

    private UserRoles removeRole;

}
