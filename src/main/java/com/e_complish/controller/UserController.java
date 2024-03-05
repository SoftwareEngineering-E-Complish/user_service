package com.e_complish.controller;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;

public class UserController {
    private final AmazonIdentityManagement iamClient;

    public UserController() {
        this.iamClient = AmazonIdentityManagementClientBuilder.defaultClient();
    }

    // Endpoint for creating users in IAM
    public CreateUserResult createUser(String userName) {

        CreateUserRequest request = new CreateUserRequest()
                .withUserName(userName);

        return this.iamClient.createUser(request);
    }
}
