package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.ConfirmUserDTO;
import com.ecomplish.user_service.model.UpdateUserDTO;
import com.ecomplish.user_service.model._enum.AuthType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final String USER_POOL_ID;
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;
    private final String HOSTED_UI_BASE_URL;


    private final CognitoIdentityProviderClient cognitoClient;

    public UserController() {
        this.USER_POOL_ID = System.getenv("USER_POOL_ID");
        this.CLIENT_ID = System.getenv("CLIENT_ID");
        this.CLIENT_SECRET = System.getenv("CLIENT_SECRET");
        this.HOSTED_UI_BASE_URL = System.getenv("HOSTED_UI_BASE_URL");

        AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();

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

            // Convert the map to a list of AttributeType
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

    @PostMapping("/confirmUser")
    public ResponseEntity<String> confirmUser(@RequestBody ConfirmUserDTO confirmUserDTO) {
        try {
            this.confirmUserLocal(confirmUserDTO);

            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void confirmUserLocal(ConfirmUserDTO confirmUserDTO) {
        logger.info(confirmUserDTO.getUsername());

        AdminConfirmSignUpRequest adminConfirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(confirmUserDTO.getUsername()).build();

        cognitoClient.adminConfirmSignUp(adminConfirmSignUpRequest);
    }

    private String calculateSecretHash(String username) throws SignatureException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(CLIENT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            mac.update(username.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(CLIENT_ID.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate secret hash: " + e.getMessage());
        }
    }

    private String buildHostedUIURL(AuthType authType) throws URISyntaxException {
        String baseUrl = HOSTED_UI_BASE_URL + "/" + authType.toPath();

        Map<String, String> params = new HashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("response_type", "code");
        params.put("scope", "email+openid+phone");
        params.put("redirect_uri", "http://localhost");

        URI uri = getURI(baseUrl, params);
        return uri.toString();
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
