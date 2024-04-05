package com.ecomplish.user_service.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

@Data
@NoArgsConstructor
public class UserSessionResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("id_token")
    private String idToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;

    public UserSessionResponseDTO(String accessToken, String tokenType, String idToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static UserSessionResponseDTO fromJSONString(String JSONString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(JSONString, UserSessionResponseDTO.class);
    }

    public static UserSessionResponseDTO fromAuthenticationResultType(AuthenticationResultType authenticationResult) {
        return new UserSessionResponseDTO(
                authenticationResult.accessToken(),
                authenticationResult.tokenType(),
                authenticationResult.idToken(),
                authenticationResult.refreshToken(),
                authenticationResult.expiresIn()
        );
    }
}
