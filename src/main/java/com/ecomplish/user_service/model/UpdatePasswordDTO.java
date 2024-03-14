package com.ecomplish.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePasswordDTO {
    private String accessToken;
    private String oldPassword;
    private String newPassword;

    public UpdatePasswordDTO(String accessToken, String oldPassword, String newPassword) {
        this.accessToken = accessToken;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
