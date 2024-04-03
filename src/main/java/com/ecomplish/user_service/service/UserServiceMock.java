package com.ecomplish.user_service.service;

import com.ecomplish.user_service.model.DTO.ChangePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.DTO.UserResponseDTO;
import com.ecomplish.user_service.model.DTO.UserSessionResponseDTO;
import com.ecomplish.user_service.model._enum.AuthType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class UserServiceMock implements IUserService {
    public String CLIENT_ID = "clientId";
    public String HOSTED_UI_BASE_URL = "https://<DOMAIN>.auth.eu-central-1.amazoncognito.com";

    @Override
    public String getLoginURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.LOGIN);
    }

    @Override
    public String getSignupURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.SIGNUP);
    }

    @Override
    public String getLogoutURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.LOGOUT);
    }

    @Override
    public UserSessionResponseDTO getSession(String authorizationCode) {
        return new UserSessionResponseDTO("accessToken", "tokenType", "idToken", "refreshToken", 3600);
    }

    @Override
    public Boolean updateUser(UpdateUserDTO updateUserDTO) {
        return true;
    }

    @Override
    public Boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        return true;
    }

    @Override
    public Boolean deleteUser(String accessToken) {
        return true;
    }

    @Override
    public Boolean verifyAccessToken(String accessToken) {
        return true;
    }

    @Override
    public UserSessionResponseDTO refreshAccessToken(String refreshToken) {
        return new UserSessionResponseDTO("accessToken", "tokenType", "idToken", "refreshToken", 3600);
    }

    @Override
    public UserResponseDTO getUser(String accessToken) {
        return new UserResponseDTO("username", "email", "name",  "phoneNumber");
    }

    @Override
    public String getUserId(String accessToken) {
        return "username";
    }

    private Map<String, String> getURIParams(AuthType authType) {
        Map<String, String> uris = new HashMap<>();
        if (authType == AuthType.LOGOUT) {
            uris.put("logout_uri", "http://localhost:8000/callback");
        }
        uris.put("redirect_uri", "http://localhost:8000/callback");

        return uris;
    }

    private static URI getURI(String baseUrl, Map<String, String> params) throws URISyntaxException {
        URI uri = new URI(baseUrl);
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey());
            queryString.append("=");
            queryString.append(entry.getValue());
        }
        uri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), queryString.toString(), null);
        return uri;
    }

    private String buildHostedUIURL(AuthType authType) throws URISyntaxException {
        String domain = "e-complish";

        String baseUrl = HOSTED_UI_BASE_URL.replace("<DOMAIN>", domain) + "/" + authType.toPath();

        Map<String, String> params = new HashMap<>();
        if (authType != AuthType.AUTH_CODE) {
            params.put("client_id", CLIENT_ID);
            params.put("response_type", "code");
            params.put("scope", "email+openid+phone+profile+aws.cognito.signin.user.admin");

            Map<String, String> uris = this.getURIParams(authType);
            params.putAll(uris);
        }

        URI uri = getURI(baseUrl, params);
        return uri.toString();
    }
}
