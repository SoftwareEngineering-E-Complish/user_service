package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.DTO.AccessTokenDTO;
import com.ecomplish.user_service.model.DTO.ConfirmUserDTO;
import com.ecomplish.user_service.model.DTO.UpdatePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.UserResponseDTO;
import com.ecomplish.user_service.model._enum.AuthType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${USER_POOL_ID}")
    private String USER_POOL_ID;
    @Value("${CLIENT_ID}")
    private String CLIENT_ID;
    @Value("${HOSTED_UI_BASE_URL}")
    private String HOSTED_UI_BASE_URL;


    private final CognitoIdentityProviderClient cognitoClient;

    public UserController() {
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @GetMapping("/loginURL")
    public ResponseEntity<String> loginURL() {
        try {
            String res = buildHostedUIURL(AuthType.LOGIN);
            logger.info(res);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            logger.error("Error generating login URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating login URL");
        }
    }

    @GetMapping("/signupURL")
    public ResponseEntity<String> signupURL() {
        try {
            String res = buildHostedUIURL(AuthType.SIGNUP);
            logger.info(res);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            logger.error("Error generating signup URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating signup URL");
        }
    }

    @GetMapping("/logoutURL")
    public ResponseEntity<String> logoutURL() {
        try {
            String res = buildHostedUIURL(AuthType.LOGOUT);
            logger.info(res);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            logger.error("Error generating signup URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating signup URL");
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        try {
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
            return ResponseEntity.ok().build();
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                    .accessToken(updatePasswordDTO.getAccessToken())
                    .previousPassword(updatePasswordDTO.getOldPassword())
                    .proposedPassword(updatePasswordDTO.getNewPassword())
                    .build();

            this.cognitoClient.changePassword(changePasswordRequest);

            return ResponseEntity.ok().build();
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verifyAccessToken")
    public ResponseEntity<Boolean> verifyAccessToken(@RequestParam String accessToken) {
        try {
            AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(accessToken)
                    .build();

            this.cognitoClient.adminGetUser(adminGetUserRequest);

            return ResponseEntity.ok(true);
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            return ResponseEntity.ok(false);
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> user(@RequestParam String accessToken) {
        try {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();

            GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

            UserResponseDTO user = getUserResponseDTO(getUserResponse);

            return ResponseEntity.ok(user);
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/userId")
    public ResponseEntity<String> userId(@RequestParam String accessToken) {
        try {
            GetUserRequest getUserRequest = GetUserRequest.builder()
                    .accessToken(accessToken)
                    .build();

            GetUserResponse getUserResponse = this.cognitoClient.getUser(getUserRequest);

            return ResponseEntity.ok(getUserResponse.username());
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/confirmUser")
    public ResponseEntity<String> confirmUser(@RequestBody ConfirmUserDTO confirmUserDTO) {
        try {
            this.confirmUserLocal(confirmUserDTO);

            return ResponseEntity.ok().build();
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/getAccessToken")
    public ResponseEntity<String> getAccessToken(@RequestBody AccessTokenDTO accessTokenDTO) {
        try {
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

            return ResponseEntity.ok(authResponse.authenticationResult().accessToken());
        } catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void confirmUserLocal(ConfirmUserDTO confirmUserDTO) {
        AdminConfirmSignUpRequest adminConfirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(confirmUserDTO.getUsername()).build();

        cognitoClient.adminConfirmSignUp(adminConfirmSignUpRequest);
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
