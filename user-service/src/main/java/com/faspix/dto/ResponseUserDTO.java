package com.faspix.dto;

import lombok.Builder;
import lombok.Data;

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
