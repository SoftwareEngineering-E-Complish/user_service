package com.ecomplish.user_service.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordDTO {
    private String accessToken;
    private String oldPassword;
    private String newPassword;
}
