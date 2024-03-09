package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.CreateUserDTO;
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
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.utils.SystemSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final String USER_POOL_ID = "eu-central-1_zAZldVUOu";

    private final CognitoIdentityProviderClient cognitoClient;

    public UserController() {
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        String regionString = System.getenv("AWS_REGION");
        Region region;
        if(regionString != null){
            region = Region.of(regionString);
        }else{
            region = Region.EU_CENTRAL_1;
        }

        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    // Endpoint for creating users in IAM
    @PostMapping("/createUser")
    public ResponseEntity createUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            Map<String, String> userAttributes = new HashMap<>();
            userAttributes.put("email", createUserDTO.getEmail());
            userAttributes.put("name", createUserDTO.getName());
            userAttributes.put("family_name", createUserDTO.getFamilyName());

            // Convert the map to a list of AttributeType
            List<AttributeType> userAttrs = userAttributes.entrySet().stream()
                    .map(entry -> AttributeType.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .build())
                    .collect(Collectors.toList());

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(createUserDTO.getUsername())
                    .temporaryPassword(createUserDTO.getPassword())
                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);

            logger.info(response.user().username());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
