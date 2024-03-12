package com.ecomplish.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginUserDTO {
    private String username;
    private String password;

    public LoginUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
