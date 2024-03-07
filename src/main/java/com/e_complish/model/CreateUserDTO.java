package com.e_complish.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDTO {
    private String username;

    public String getUsername() {
        return username;
    }
}
