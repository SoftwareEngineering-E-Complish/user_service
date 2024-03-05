package com.e_complish.controller;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.e_complish.model.CreateUserDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AmazonIdentityManagement iamClient;

    public UserController() {
        String region = System.getenv("AWS_REGION");
        if(region == null) region = Regions.DEFAULT_REGION.getName();

        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
                "http://localstack:4566", region); // Adjust region if necessary


        this.iamClient = AmazonIdentityManagementClientBuilder.standard().withEndpointConfiguration(endpointConfig).build();
    }

    // Endpoint for creating users in IAM
    @PostMapping("/createUser")
    public boolean createUser(@RequestBody String createUserDTO) {
        try {
            logger.info("AAAAAA");
            logger.info(createUserDTO.toString());
            // logger.info(createUserDTO.getUsername());

//            logger.info(username);

            CreateUserRequest request = new CreateUserRequest()
                    .withUserName(createUserDTO);
            CreateUserResult result = this.iamClient.createUser(request);
            return true;
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }
}
