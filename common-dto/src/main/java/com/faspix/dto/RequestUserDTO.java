package com.faspix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RequestUserDTO {

    @NotBlank(message = "Name shouldn't be blank")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email shouldn't be blank")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
