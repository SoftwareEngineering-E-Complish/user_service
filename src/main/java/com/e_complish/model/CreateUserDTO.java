package com.e_complish.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class CreateUserDTO {
    private final String username;
}
