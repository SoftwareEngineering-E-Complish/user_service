package com.ecomplish.user_service.service;

import com.ecomplish.user_service.model.DTO.AccessTokenDTO;
import com.ecomplish.user_service.model.DTO.ChangePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.UserResponseDTO;
import com.ecomplish.user_service.model._enum.AuthType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class UserService {
    public String USER_POOL_ID;
    public String CLIENT_ID;
    public String HOSTED_UI_BASE_URL;

    public CognitoIdentityProviderClient cognitoClient;

    public UserService() {
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(credentialsProvider)
                .build();

        USER_POOL_ID = System.getenv("USER_POOL_ID");
        CLIENT_ID = System.getenv("CLIENT_ID");
        HOSTED_UI_BASE_URL = System.getenv("HOSTED_UI_BASE_URL");
    }

    public String loginURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.LOGIN);
    }

    public String signupURL() throws URISyntaxException {
        return buildHostedUIURL(AuthType.SIGNUP);
    }

    public String logoutURL() throws URISyntaxException {

        return buildHostedUIURL(AuthType.LOGOUT);
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

    public Boolean deleteUser(String username) {
            AdminDeleteUserRequest deleteUserRequest = AdminDeleteUserRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(username)
                    .build();

            this.cognitoClient.adminDeleteUser(deleteUserRequest);

            return true;
    }

    public Boolean verifyAccessToken(String accessToken) {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();

            this.cognitoClient.getUser(getUserRequest);

            return true;
    }

    public UserResponseDTO user(String accessToken) {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();

            GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

            UserResponseDTO user = getUserResponseDTO(getUserResponse);

            return user;
    }

    public String userId(String accessToken) {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();

            GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

            return getUserResponse.username();
    }

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", accessTokenDTO.getUsername());
            authParams.put("PASSWORD", accessTokenDTO.getPassword());

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .clientId(CLIENT_ID)
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .authParameters(authParams)
                    .build();

            AdminInitiateAuthResponse authResponse = this.cognitoClient.adminInitiateAuth(authRequest);


            return authResponse.authenticationResult().accessToken();
    }

    @NotNull
    private static UserResponseDTO getUserResponseDTO(GetUserResponse getUserResponse) {
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

    private Map<String, String> getURIParams(AuthType authType) {
        DescribeUserPoolClientRequest request = DescribeUserPoolClientRequest.builder()
                .userPoolId(USER_POOL_ID)
                .clientId(CLIENT_ID)
                .build();

        DescribeUserPoolClientResponse response = this.cognitoClient.describeUserPoolClient(request);

        Map<String, String> uris = new HashMap<>();
        if(authType == AuthType.LOGOUT && response.userPoolClient().hasLogoutURLs()) {
            uris.put("logout_uri", response.userPoolClient().logoutURLs().get(0));
        }
        uris.put("redirect_uri", response.userPoolClient().callbackURLs().get(0));

        return uris;
    }

    private String buildHostedUIURL(AuthType authType) throws URISyntaxException {
        if(HOSTED_UI_BASE_URL == null || CLIENT_ID == null) {
            throw new URISyntaxException("HOSTED_UI_BASE_URL or CLIENT_ID", "Missing environment variables");
        }

        String domain = this.getDomain();
        Map<String, String> uris = this.getURIParams(authType);

        String baseUrl = HOSTED_UI_BASE_URL.replace("<DOMAIN>", domain) + "/" + authType.toPath();

        Map<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("response_type", "code");
        params.put("scope", "email+openid+phone");
        params.putAll(uris);

        URI uri = getURI(baseUrl, params);
        return uri.toString();
    }

    private String getDomain() {
        DescribeUserPoolRequest describeUserPoolRequest = DescribeUserPoolRequest.builder().userPoolId(USER_POOL_ID).build();
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
