package com.ecomplish.user_service.service;

import com.ecomplish.user_service.model.DTO.ChangePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.DTO.UserResponseDTO;
import com.ecomplish.user_service.model.DTO.UserSessionResponseDTO;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.fail;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

@SpringBootTest
public class UserServiceTest {
    UserService userService;

    @BeforeEach
    public void setUp() {
        CognitoIdentityProviderClient cognitoClient = Mockito.mock(CognitoIdentityProviderClient.class);
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        userService = new UserService();
        userService.cognitoClient = cognitoClient;
        userService.httpClient = httpClient;
        userService.USER_POOL_ID = "eu-central-1_123";
        userService.CLIENT_ID = "AAAAAAA";
        userService.HOSTED_UI_BASE_URL = "https://<DOMAIN>.auth.test.example.com";
    }

    @Test
    public void testLoginUrlSuccess() {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("e-complish");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        DescribeUserPoolClientResponse describeUserPoolClientResponse = Mockito
                .mock(DescribeUserPoolClientResponse.class);
        UserPoolClientType userPoolClientType = Mockito.mock(UserPoolClientType.class);
        Mockito.when(userPoolClientType.callbackURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(describeUserPoolClientResponse.userPoolClient()).thenReturn(userPoolClientType);
        Mockito.when(userService.cognitoClient.describeUserPoolClient((DescribeUserPoolClientRequest) any()))
                .thenReturn(describeUserPoolClientResponse);

        // Invocation of loginURL method
        String loginURL;
        try {
            loginURL = userService.getLoginURL();
            // Asserting that the return value is a string
            assertNotNull(loginURL);
        } catch (Exception e) {
            // Failing the test if an error occurs during loginURL invocation
            fail("loginURL method threw an exception: " + e.getMessage());
        }

        // Verifying that describeUserPoolClient was invoked
        Mockito.verify(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));
    }

    @Test
    public void testLoginURLFailure() {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        // Throw an exception when describeUserPoolClient is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));

        // Invocation of loginURL method
        String loginURL;
        try {
            loginURL = userService.getLoginURL();
            // Failing the test if no exception is thrown
            fail("loginURL method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }

        // Verifying that describeUserPoolClient was invoked
        Mockito.verify(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));
    }

    @Test
    public void testSignupURLSuccess() {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("e-complish");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        DescribeUserPoolClientResponse describeUserPoolClientResponse = Mockito
                .mock(DescribeUserPoolClientResponse.class);
        UserPoolClientType userPoolClientType = Mockito.mock(UserPoolClientType.class);
        Mockito.when(userPoolClientType.callbackURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(describeUserPoolClientResponse.userPoolClient()).thenReturn(userPoolClientType);
        Mockito.when(userService.cognitoClient.describeUserPoolClient((DescribeUserPoolClientRequest) any()))
                .thenReturn(describeUserPoolClientResponse);

        // Invocation of signupURL method
        String signupURL;
        try {
            signupURL = userService.getSignupURL();
            // Asserting that the return value is a string
            assertNotNull(signupURL);
        } catch (Exception e) {
            // Failing the test if an error occurs during signupURL invocation
            fail("signupURL method threw an exception: " + e.getMessage());
        }

        // Verifying that describeUserPoolClient was invoked
        Mockito.verify(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));
    }

    @Test
    public void testSignupURLFailure() {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        // Throw an exception when describeUserPoolClient is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));

        // Invocation of signupURL method
        String signupURL;
        try {
            signupURL = userService.getSignupURL();
            // Failing the test if no exception is thrown
            fail("signupURL method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }

    @Test
    public void testLogoutURLSuccess() throws URISyntaxException {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("e-complish");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        DescribeUserPoolClientResponse describeUserPoolClientResponse = Mockito
                .mock(DescribeUserPoolClientResponse.class);
        UserPoolClientType userPoolClientType = Mockito.mock(UserPoolClientType.class);
        Mockito.when(userPoolClientType.hasLogoutURLs()).thenReturn(true);
        Mockito.when(userPoolClientType.logoutURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(userPoolClientType.callbackURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(describeUserPoolClientResponse.userPoolClient()).thenReturn(userPoolClientType);
        Mockito.when(userService.cognitoClient.describeUserPoolClient((DescribeUserPoolClientRequest) any()))
                .thenReturn(describeUserPoolClientResponse);

        // Invocation of logoutURL method
        String logoutURL;
        try {
            logoutURL = userService.getLogoutURL();
            // Asserting that the return value is a string
            assertNotNull(logoutURL);
        } catch (Exception e) {
            // Failing the test if an error occurs during logoutURL invocation
            fail("logoutURL method threw an exception: " + e.getMessage());
        }

        // Verifying that describeUserPoolClient was invoked
        Mockito.verify(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));
    }

    @Test
    public void testLogoutURLFailure() {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        // Throw an exception when describeUserPoolClient is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));

        // Invocation of logoutURL method
        String logoutURL;
        try {
            logoutURL = userService.getLogoutURL();
            // Failing the test if no exception is thrown
            fail("logoutURL method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }

    @Test
    public void testSessionSuccess() throws IOException, InterruptedException {
        // Stubbing the describeUserPoolClient method
        DescribeUserPoolResponse describeUserPoolResponse = Mockito.mock(DescribeUserPoolResponse.class);
        UserPoolType userPoolType = Mockito.mock(UserPoolType.class);
        Mockito.when(userPoolType.domain()).thenReturn("e-complish");
        Mockito.when(describeUserPoolResponse.userPool()).thenReturn(userPoolType);
        Mockito.when(userService.cognitoClient.describeUserPool(Mockito.any(DescribeUserPoolRequest.class)))
                .thenReturn(describeUserPoolResponse);

        DescribeUserPoolClientResponse describeUserPoolClientResponse = Mockito
                .mock(DescribeUserPoolClientResponse.class);
        UserPoolClientType userPoolClientType = Mockito.mock(UserPoolClientType.class);
        Mockito.when(userPoolClientType.hasLogoutURLs()).thenReturn(true);
        Mockito.when(userPoolClientType.logoutURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(userPoolClientType.callbackURLs()).thenReturn(List.of("http://localhost:8000"));
        Mockito.when(describeUserPoolClientResponse.userPoolClient()).thenReturn(userPoolClientType);
        Mockito.when(userService.cognitoClient.describeUserPoolClient((DescribeUserPoolClientRequest) any()))
                .thenReturn(describeUserPoolClientResponse);

        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponse.body()).thenReturn("{\"access_token\":\"accessToken\",\"token_type\":\"tokenType\",\"id_token\":\"idToken\",\"refresh_token\":\"refreshToken\",\"expires_in\":3600}");
        Mockito.when(httpResponse.statusCode()).thenReturn(200);
        Mockito.when(userService.httpClient.send(any(), any())).thenReturn(httpResponse);

        UserSessionResponseDTO userSessionResponseDTO;
        try {
            userSessionResponseDTO = userService.getSession("authorizationCode");
            assertNotNull(userSessionResponseDTO);
        } catch (Exception e) {
            fail("getSession method threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testSessionFailure() {
        // Throw an exception when describeUserPoolClient is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .describeUserPoolClient(Mockito.any(DescribeUserPoolClientRequest.class));

        UserSessionResponseDTO userSessionResponseDTO;
        try {
            userSessionResponseDTO = userService.getSession("authorizationCode");
            fail("getSession method did not throw an exception");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUpdateUserSuccess() {
        // Stubbing the adminUpdateUserAttributes method
        AdminUpdateUserAttributesResponse adminUpdateUserAttributesResponse = Mockito
                .mock(AdminUpdateUserAttributesResponse.class);
        Mockito.when(userService.cognitoClient.adminUpdateUserAttributes((AdminUpdateUserAttributesRequest) any()))
                .thenReturn(adminUpdateUserAttributesResponse);

        // Invocation of updateUser method
        Boolean updateUser;
        try {
            updateUser = userService.updateUser(new UpdateUserDTO());
            // Asserting that the return value is a boolean
            assertNotNull(updateUser);
        } catch (Exception e) {
            // Failing the test if an error occurs during updateUser invocation
            fail("updateUser method threw an exception: " + e.getMessage());
        }

        // Verifying that adminUpdateUserAttributes was invoked
        Mockito.verify(userService.cognitoClient)
                .adminUpdateUserAttributes(Mockito.any(AdminUpdateUserAttributesRequest.class));
    }

    @Test
    public void testUpdateUserFailure() {
        // Throw an exception when adminUpdateUserAttributes is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .adminUpdateUserAttributes((AdminUpdateUserAttributesRequest) any());

        // Invocation of updateUser method
        Boolean updateUser;
        try {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    "username",
                    "email",
                    "name",
                    "phonenumber");
            updateUser = userService.updateUser(new UpdateUserDTO());
            // Failing the test if no exception is thrown
            fail("updateUser method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }

    @Test
    public void testChangePasswordSuccess() {
        // Stubbing the changePassword method
        ChangePasswordResponse changePasswordResponse = Mockito.mock(ChangePasswordResponse.class);
        Mockito.when(userService.cognitoClient.changePassword((ChangePasswordRequest) any()))
                .thenReturn(changePasswordResponse);

        // Invocation of changePassword method
        Boolean changePassword;
        try {
            changePassword = userService.changePassword(new ChangePasswordDTO());
            // Asserting that the return value is a boolean
            assertNotNull(changePassword);
        } catch (Exception e) {
            // Failing the test if an error occurs during changePassword invocation
            fail("changePassword method threw an exception: " + e.getMessage());
        }

        // Verifying that changePassword was invoked
        Mockito.verify(userService.cognitoClient).changePassword(Mockito.any(ChangePasswordRequest.class));
    }

    @Test
    public void testChangePasswordFailure() {
        // Throw an exception when changePassword is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .changePassword((ChangePasswordRequest) any());

        // Invocation of changePassword method
        Boolean changePassword;
        try {
            changePassword = userService.changePassword(new ChangePasswordDTO());
            // Failing the test if no exception is thrown
            fail("changePassword method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }

    @Test
    public void testDeleteUserSuccess() {
        // Stubbing the adminDeleteUser method
        DeleteUserResponse deleteUserResponse = Mockito.mock(DeleteUserResponse.class);
        Mockito.when(userService.cognitoClient.deleteUser((DeleteUserRequest) any()))
                .thenReturn(deleteUserResponse);

        // Invocation of deleteUser method
        Boolean deleteUser;
        try {
            deleteUser = userService.deleteUser("accessToken");
            // Asserting that the return value is a boolean
            assertNotNull(deleteUser);
        } catch (Exception e) {
            // Failing the test if an error occurs during deleteUser invocation
            fail("deleteUser method threw an exception: " + e.getMessage());
        }

        // Verifying that adminDeleteUser was invoked
        Mockito.verify(userService.cognitoClient).deleteUser(Mockito.any(DeleteUserRequest.class));
    }

    @Test
    public void testDeleteUserFailure() {
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .deleteUser((DeleteUserRequest) any());

        Boolean deleteUser;
        try {
            deleteUser = userService.deleteUser("accessToken");
            fail("deleteUser method did not throw an exception");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testVerifyAccessTokenSuccess() {
        GetUserResponse getUserResponse = Mockito.mock(GetUserResponse.class);
        Mockito.when(userService.cognitoClient.getUser((GetUserRequest) any())).thenReturn(getUserResponse);

        Boolean verifyAccessToken;
        try {
            verifyAccessToken = userService.verifyAccessToken("accessToken");
            assertNotNull(verifyAccessToken);
        } catch (Exception e) {
            fail("verifyAccessToken method threw an exception: " + e.getMessage());
        }

        // Verifying that getUser was invoked
        Mockito.verify(userService.cognitoClient).getUser(Mockito.any(GetUserRequest.class));
    }

    @Test
    public void testVerifyAccessTokenFailure() {
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .getUser((GetUserRequest) any());

        Boolean verifyAccessToken;
        try {
            verifyAccessToken = userService.verifyAccessToken("accessToken");
            assertFalse(null , verifyAccessToken);
        } catch (Exception e) {
            fail("verifyAccessToken method threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testRefreshAccessTokenSuccess() {
        InitiateAuthResponse initiateAuthResponse = Mockito.mock(InitiateAuthResponse.class);
        AuthenticationResultType authenticationResultType = Mockito.mock(AuthenticationResultType.class);
        Mockito.when(initiateAuthResponse.authenticationResult()).thenReturn(authenticationResultType);
        Mockito.when(userService.cognitoClient.initiateAuth((InitiateAuthRequest) any())).thenReturn(initiateAuthResponse);

        UserSessionResponseDTO userSessionResponseDTO;
        try {
            userSessionResponseDTO = userService.refreshAccessToken("refreshToken");
            assertNotNull(userSessionResponseDTO);
        } catch (Exception e) {
            fail("refreshAccessToken method threw an exception: " + e.getMessage());
        }

        // Verifying that getUser was invoked
        Mockito.verify(userService.cognitoClient).initiateAuth(Mockito.any(InitiateAuthRequest.class));
    }

    @Test
    public void testRefreshAccessTokenFailure() {
        InitiateAuthResponse initiateAuthResponse = Mockito.mock(InitiateAuthResponse.class);
        AuthenticationResultType authenticationResultType = Mockito.mock(AuthenticationResultType.class);
        Mockito.when(initiateAuthResponse.authenticationResult()).thenReturn(authenticationResultType);
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient).initiateAuth((InitiateAuthRequest) any());

        UserSessionResponseDTO userSessionResponseDTO;
        try {
            userSessionResponseDTO = userService.refreshAccessToken("refreshToken");
            fail("refreshAccessToken method did not throw an exception");
        } catch (Exception e) {
            assertTrue(true);
        }

        Mockito.verify(userService.cognitoClient).initiateAuth(Mockito.any(InitiateAuthRequest.class));
    }

    @Test
    public void testUserSuccess() {
        // Stubbing the getUser method
        GetUserResponse getUserResponse = Mockito.mock(GetUserResponse.class);
        Mockito.when(userService.cognitoClient.getUser((GetUserRequest) any())).thenReturn(getUserResponse);

        // Invocation of user method
        UserResponseDTO user;
        try {
            user = userService.getUser("accessToken");
            // Asserting that the return value is a UserResponseDTO object
            assertNotNull(user);
        } catch (Exception e) {
            // Failing the test if an error occurs during user invocation
            fail("user method threw an exception: " + e.getMessage());
        }

        // Verifying that getUser was invoked
        Mockito.verify(userService.cognitoClient).getUser(Mockito.any(GetUserRequest.class));
    }

    @Test
    public void testUserFailure() {
        // Throw an exception when getUser is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .getUser((GetUserRequest) any());

        // Invocation of user method
        UserResponseDTO user;
        try {
            user = userService.getUser("accessToken");
            // Failing the test if no exception is thrown
            fail("user method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }

    @Test
    public void testUserIdSuccess() {
        // Stubbing the getUser method
        GetUserResponse getUserResponse = Mockito.mock(GetUserResponse.class);
        Mockito.when(getUserResponse.username()).thenReturn("username");
        Mockito.when(userService.cognitoClient.getUser((GetUserRequest) any())).thenReturn(getUserResponse);

        // Invocation of userId method
        String userId;
        try {
            userId = userService.getUserId("accessToken");
            // Asserting that the return value is a string
            assertNotNull(userId);
        } catch (Exception e) {
            // Failing the test if an error occurs during userId invocation
            fail("userId method threw an exception: " + e.getMessage());
        }

        // Verifying that getUser was invoked
        Mockito.verify(userService.cognitoClient).getUser(Mockito.any(GetUserRequest.class));
    }

    @Test
    public void testUserIdFailure() {
        // Throw an exception when getUser is called
        Mockito.doThrow(ResourceNotFoundException.class).when(userService.cognitoClient)
                .getUser((GetUserRequest) any());

        // Invocation of userId method
        String userId;
        try {
            userId = userService.getUserId("accessToken");
            // Failing the test if no exception is thrown
            fail("userId method did not throw an exception");
        } catch (Exception e) {
            // Asserting that the exception message is as expected
            assertTrue(true);
        }
    }
}
