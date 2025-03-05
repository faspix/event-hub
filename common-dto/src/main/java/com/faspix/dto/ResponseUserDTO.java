package com.faspix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ResponseUserDTO implements Serializable {

    private String userId;

    private String username;

    private String email;

    private List<String> roles;

}
