package com.ecomplish.user_service.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class UserSessionResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("id_token")
    private String idToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;

    public UserSessionResponseDTO(String accessToken, String idToken, String refreshToken, int expiresIn) {
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public static UserSessionResponseDTO fromJSONString(String JSONString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(JSONString, UserSessionResponseDTO.class);
    }
}
