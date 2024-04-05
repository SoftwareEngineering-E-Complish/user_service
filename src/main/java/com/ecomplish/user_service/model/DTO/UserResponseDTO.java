package com.ecomplish.user_service.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import java.util.List;

@Data
@NoArgsConstructor
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

    public static UserResponseDTO fromGetUserResponse(GetUserResponse getUserResponse) {
        List<AttributeType> userAttributes = getUserResponse.userAttributes();

        String name = "";
        String email = "";
        String phoneNumber = "";

        for (AttributeType attr : userAttributes) {
            switch (attr.name()) {
                case "name":
                    name = attr.value();
                    break;
                case "email":
                    email = attr.value();
                    break;
                case "phone_number":
                    phoneNumber = attr.value();
                    break;
            }
        }

        return new UserResponseDTO(
                getUserResponse.username(),
                email,
                name,
                phoneNumber
        );
    }
}
