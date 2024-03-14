package com.ecomplish.user_service.model.DTO;

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
}
