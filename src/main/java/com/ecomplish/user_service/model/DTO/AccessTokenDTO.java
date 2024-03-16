package com.ecomplish.user_service.model.DTO;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccessTokenDTO {
    private String username;
    private String password;
}
