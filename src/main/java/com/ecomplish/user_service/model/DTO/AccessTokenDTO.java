package com.ecomplish.user_service.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccessTokenDTO {
    private String username;
    private String password;
}
