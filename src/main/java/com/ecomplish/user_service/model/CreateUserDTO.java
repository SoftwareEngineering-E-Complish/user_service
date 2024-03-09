package com.ecomplish.user_service.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
public class CreateUserDTO {
    private String username;
    private String name;
    private String familyName;
    private String email;
    private String password;

    public CreateUserDTO(String username, String name, String familyName, String email, String password) {
        this.username = username;
        this.name = name;
        this.familyName = familyName;
        this.email = email;
        this.password = password;
    }
}
