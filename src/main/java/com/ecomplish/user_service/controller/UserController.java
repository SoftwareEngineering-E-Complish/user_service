package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.ConfirmUserDTO;
import com.ecomplish.user_service.model.CreateUserDTO;
import com.ecomplish.user_service.model.LoginUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

    private final String USER_POOL_ID = "eu-central-1_tHxxikvel";
    private final String CLIENT_ID = "2tcalk7bl60t5vass9smgu2l3q";
    private final String CLIENT_SECRET = "734hqbj2giqisj52t42f7pd1thija95bfgkik6i4e62g41v29mf";


    private final CognitoIdentityProviderClient cognitoClient;

    public UserController() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String regionString = System.getenv("AWS_DEFAULT_REGION");
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
        ResponseEntity res;
        try {
            Map<String, String> userAttributes = new HashMap<>();
            userAttributes.put("email", createUserDTO.getEmail());
            userAttributes.put("name", createUserDTO.getName());
            userAttributes.put("phone_number", createUserDTO.getPhoneNumber());

            // Convert the map to a list of AttributeType
            List<AttributeType> userAttrs = userAttributes.entrySet().stream()
                    .map(entry -> AttributeType.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .build())
                    .collect(Collectors.toList());

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .userAttributes(userAttrs)
                    .username(createUserDTO.getUsername())
                    .clientId(CLIENT_ID)
                    .secretHash(this.calculateSecretHash(createUserDTO.getUsername()))
                    .password(createUserDTO.getPassword())
                    .build();

            SignUpResponse response = this.cognitoClient.signUp(signUpRequest);

            res = ResponseEntity.status(HttpStatus.OK).build();

            this.confirmUserLocal(new ConfirmUserDTO(createUserDTO.getUsername()));
        }
        catch (Exception e){
            logger.error(e.getMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return res;
    }

    @PostMapping("/confirmUser")
    public ResponseEntity confirmUser(@RequestBody ConfirmUserDTO confirmUserDTO) {
        ResponseEntity res;
        try {
            this.confirmUserLocal(confirmUserDTO);

            res = ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            logger.info(e.getMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return res;
    }

    @PostMapping("/loginUser")
    public ResponseEntity loginUser(@RequestBody LoginUserDTO loginUserDTO) {
        ResponseEntity res;
        try {
            logger.info(loginUserDTO.getUsername());
            Map<String, String> authParameters = new HashMap<>();
            authParameters.put("USERNAME", loginUserDTO.getUsername());
            authParameters.put("PASSWORD", loginUserDTO.getPassword());
            authParameters.put("SECRET_HASH", this.calculateSecretHash(loginUserDTO.getUsername()));

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .clientId(CLIENT_ID)
                    .authParameters(authParameters)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .build();

            AdminInitiateAuthResponse response = this.cognitoClient.adminInitiateAuth(authRequest);
            res = ResponseEntity.status(HttpStatus.OK).build();
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return res;
    }

    @PostMapping("/logoutUser")
    public ResponseEntity logoutUser(@RequestBody ConfirmUserDTO confirmUserDTO) {
        ResponseEntity res;
        try {
            AdminUserGlobalSignOutRequest signOutRequest = AdminUserGlobalSignOutRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(confirmUserDTO.getUsername())
                    .build();

            AdminUserGlobalSignOutResponse response = this.cognitoClient.adminUserGlobalSignOut(signOutRequest);
            res = ResponseEntity.status(HttpStatus.OK).build();
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return res;
    }

    @PostMapping("/updateUser")
    public ResponseEntity updateUser(@RequestBody CreateUserDTO createUserDTO) {
        ResponseEntity res;
        try {
            AdminUpdateUserAttributesRequest updateUserAttributesRequest = AdminUpdateUserAttributesRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(createUserDTO.getUsername())
                    .userAttributes(AttributeType.builder().name("email").value(createUserDTO.getEmail()).build())
                    .build();

            AdminUpdateUserAttributesResponse response = this.cognitoClient.adminUpdateUserAttributes(updateUserAttributesRequest);
            res = ResponseEntity.status(HttpStatus.OK).build();
        } catch (CognitoIdentityProviderException e) {
            logger.info(e.awsErrorDetails().errorMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e){
            logger.info(e.getMessage());
            res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return res;
    }

    private void confirmUserLocal(ConfirmUserDTO confirmUserDTO) {
        logger.info(confirmUserDTO.getUsername());

        AdminConfirmSignUpRequest adminConfirmSignUpRequest = AdminConfirmSignUpRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(confirmUserDTO.getUsername()).build();

        AdminConfirmSignUpResponse confirmSignUpResponse = cognitoClient.adminConfirmSignUp(adminConfirmSignUpRequest);

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
}
