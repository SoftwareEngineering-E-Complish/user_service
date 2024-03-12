package com.ecomplish.user_service.model;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfirmUserDTO {
    private String username;

    public ConfirmUserDTO(String username) {
        this.username = username;
    }
}
