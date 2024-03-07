package com.e_complish.controller;

import com.e_complish.model.CreateUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.SystemSettingsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.utils.SystemSetting;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CognitoIdentityProviderClient cognitoClient;

    public UserController() {
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        String region = System.getenv("AWS_REGION");
        if(region == null) region = Region.EU_CENTRAL_1.id();

        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.US_EAST_1)
                .build();
    }

    // Endpoint for creating users in IAM
    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            AttributeType userAttrs = AttributeType.builder()
                    .name("email")
                    .value("test@example.com")
                    .build();

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId("eu-central-1_zAZldVUOu")
                    .username(createUserDTO.getUsername())
                    .temporaryPassword("test@123")
                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
            System.out.println(
                    "User " + response.user().username() + "is created. Status: " + response.user().userStatus());

            logger.info(response.user().username());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
