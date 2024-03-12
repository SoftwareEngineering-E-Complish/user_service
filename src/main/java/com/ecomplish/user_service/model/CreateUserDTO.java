package com.ecomplish.user_service.model;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    public CreateUserDTO(String username, String email, String password,  String name, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
