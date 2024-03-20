package com.ecomplish.user_service.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserDTO {
    private String username;
    private String email;
    private String name;
    private String phoneNumber;

    public UpdateUserDTO(String username, String email, String name, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
