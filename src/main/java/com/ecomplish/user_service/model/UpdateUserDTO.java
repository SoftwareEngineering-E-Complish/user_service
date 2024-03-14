package com.ecomplish.user_service.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserDTO {
    private String username;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    public UpdateUserDTO(String username, String email, String password,  String name, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
