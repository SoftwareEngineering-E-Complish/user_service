package com.ecomplish.user_service.service;

import com.ecomplish.user_service.model.DTO.*;
import com.ecomplish.user_service.model._enum.AuthType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class UserService {
    public String USER_POOL_ID;
    public String CLIENT_ID;
    public String HOSTED_UI_BASE_URL;

    public CognitoIdentityProviderClient cognitoClient;

    public HttpClient httpClient;

    public UserService() {
        String region = System.getenv("AWS_REGION");
        if (region != null && !region.isBlank()) {
            AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

            this.cognitoClient = CognitoIdentityProviderClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .build();
        }
        USER_POOL_ID = System.getenv("USER_POOL_ID");
        CLIENT_ID = System.getenv("CLIENT_ID");
        HOSTED_UI_BASE_URL = System.getenv("HOSTED_UI_BASE_URL");

        this.httpClient = HttpClient.newHttpClient();
    }

    public String getLoginURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.LOGIN);
    }

    public String getSignupURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.SIGNUP);
    }

    public String getLogoutURL() throws URISyntaxException {

        return buildHostedUIURL(AuthType.LOGOUT);
    }

    public UserSessionResponseDTO getSession(String authorizationCode)
            throws URISyntaxException, IOException, InterruptedException {
        AuthType authType = AuthType.AUTH_CODE;

        Map<String, String> uris = getURIParams(authType);

        String requestBody = "grant_type=" + URLEncoder.encode("authorization_code", StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&code=" + URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(uris.get("redirect_uri"), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(buildHostedUIURL(authType)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonResponse = response.body();
            return UserSessionResponseDTO.fromJSONString(jsonResponse);
        } else {
            throw new IOException("Failed to get user session. HTTP status code: " + response.statusCode());
        }
    }

    public Boolean updateUser(UpdateUserDTO updateUserDTO) {
        Map<String, String> userAttributes = new HashMap<>();
        userAttributes.put("email", updateUserDTO.getEmail());
        userAttributes.put("name", updateUserDTO.getName());
        userAttributes.put("phone_number", updateUserDTO.getPhoneNumber());

        List<AttributeType> userAttrs = userAttributes.entrySet().stream()
                .map(entry -> AttributeType.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        AdminUpdateUserAttributesRequest updateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(updateUserDTO.getUsername())
                .userAttributes(userAttrs)
                .build();

        this.cognitoClient.adminUpdateUserAttributes(updateUserAttributesRequest);

        return true;
    }

    public Boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .accessToken(changePasswordDTO.getAccessToken())
                .previousPassword(changePasswordDTO.getOldPassword())
                .proposedPassword(changePasswordDTO.getNewPassword())
                .build();

        this.cognitoClient.changePassword(changePasswordRequest);

        return true;
    }

    public Boolean deleteUser(String accessToken) {
        DeleteUserRequest deleteUserRequest = DeleteUserRequest.builder()
                .accessToken(accessToken)
                .build();

        this.cognitoClient.deleteUser(deleteUserRequest);

        return true;
    }

    public Boolean verifyAccessToken(String accessToken) {
        GetUserRequest getUserRequest = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        this.cognitoClient.getUser(getUserRequest);

        return true;
    }

    public UserSessionResponseDTO refreshAccessToken(String refreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("REFRESH_TOKEN", refreshToken);

        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .clientId(CLIENT_ID)
                .authFlow("REFRESH_TOKEN_AUTH")
                .authParameters(params)
                .build();

        InitiateAuthResponse initiateAuthResponse = this.cognitoClient.initiateAuth(initiateAuthRequest);

        System.out.println();

        return UserSessionResponseDTO.fromAuthenticationResultType(initiateAuthResponse.authenticationResult());
    }

    public UserResponseDTO getUser(String accessToken) {
        GetUserRequest getUserRequest = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

        return UserResponseDTO.fromGetUserResponse(getUserResponse);
    }

    public String getUserId(String accessToken) {
        GetUserRequest getUserRequest = GetUserRequest.builder()
                .accessToken(accessToken)
                .build();

        GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

        return getUserResponse.username();
    }

    private Map<String, String> getURIParams(AuthType authType) {
        DescribeUserPoolClientRequest request = DescribeUserPoolClientRequest.builder()
                .userPoolId(USER_POOL_ID)
                .clientId(CLIENT_ID)
                .build();

        DescribeUserPoolClientResponse response = this.cognitoClient.describeUserPoolClient(request);

        Map<String, String> uris = new HashMap<>();
        if (authType == AuthType.LOGOUT && response.userPoolClient().hasLogoutURLs()) {
            uris.put("logout_uri", response.userPoolClient().logoutURLs().get(0));
        }
        uris.put("redirect_uri", response.userPoolClient().callbackURLs().get(0));

        return uris;
    }

    private String buildHostedUIURL(AuthType authType) throws URISyntaxException {
        if (HOSTED_UI_BASE_URL == null || CLIENT_ID == null) {
            throw new URISyntaxException("HOSTED_UI_BASE_URL or CLIENT_ID", "Missing environment variables");
        }

        String domain = this.getDomain();

        String baseUrl = HOSTED_UI_BASE_URL.replace("<DOMAIN>", domain) + "/" + authType.toPath();

        Map<String, String> params = new HashMap<>();
        if (authType != AuthType.AUTH_CODE) {
            params.put("client_id", CLIENT_ID);
            params.put("response_type", "code");
            params.put("scope", "email+openid+phone");

            Map<String, String> uris = this.getURIParams(authType);
            params.putAll(uris);
        }

        URI uri = getURI(baseUrl, params);
        return uri.toString();
    }

    private String getDomain() {
        DescribeUserPoolRequest describeUserPoolRequest = DescribeUserPoolRequest.builder().userPoolId(USER_POOL_ID)
                .build();
        UserPoolType res = this.cognitoClient.describeUserPool(describeUserPoolRequest).userPool();
        return res.domain();
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
}
