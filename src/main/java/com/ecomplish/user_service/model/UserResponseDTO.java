package com.ecomplish.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserResponseDTO {
    private String username;
    private String email;
    private String name;
    private String phoneNumber;

    public UserResponseDTO(String username, String email,  String name, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
