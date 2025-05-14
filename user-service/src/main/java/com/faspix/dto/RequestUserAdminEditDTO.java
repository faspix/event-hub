package com.faspix.dto;

import com.faspix.shared.roles.UserRoles;
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
