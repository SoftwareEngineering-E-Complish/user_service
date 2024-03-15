package com.ecomplish.user_service.unit.controller;

import com.ecomplish.user_service.controller.UserController;
import com.ecomplish.user_service.model.DTO.AccessTokenDTO;
import com.ecomplish.user_service.model.DTO.UpdatePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.UserResponseDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@TestPropertySource(properties = {
        "CLIENT_ID=53mpp04dd8k69oj9gprorn0vic",
        "USER_POOL_ID=eu-central-1_tHxxikvel",
        "HOSTED_UI_BASE_URL=https://<DOMAIN>.auth.eu-central-1.amazoncognito.com"
})
class UserControllerUnitTest {

    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoginURL() throws URISyntaxException {
        ResponseEntity<String> responseEntity = userController.loginURL();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testSignupURL() throws URISyntaxException {
        ResponseEntity<String> responseEntity = userController.signupURL();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testLogoutURL() throws URISyntaxException {
        ResponseEntity<String> responseEntity = userController.logoutURL();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateUser() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("username");
        updateUserDTO.setEmail("email@example.com");
        updateUserDTO.setName("John Doe");
        updateUserDTO.setPhoneNumber("1234567890");

        when(cognitoClient.adminUpdateUserAttributes(any(AdminUpdateUserAttributesRequest.class)))
                .thenReturn(AdminUpdateUserAttributesResponse.builder().build());

        ResponseEntity<String> responseEntity = userController.updateUser(updateUserDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testChangePassword() {
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setAccessToken("accessToken");
        updatePasswordDTO.setOldPassword("oldPassword");
        updatePasswordDTO.setNewPassword("newPassword");

        when(cognitoClient.changePassword(any(ChangePasswordRequest.class)))
                .thenReturn(ChangePasswordResponse.builder().build());

        ResponseEntity<String> responseEntity = userController.changePassword(updatePasswordDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testVerifyAccessToken() {
        String accessToken = "accessToken";

        when(cognitoClient.getUser(any(GetUserRequest.class)))
                .thenReturn(GetUserResponse.builder().build());

        ResponseEntity<Boolean> responseEntity = userController.verifyAccessToken(accessToken);
        assertTrue(responseEntity.getBody());
    }

    @Test
    void testUser() {
        String accessToken = "accessToken";

        List<AttributeType> attributes = new ArrayList<>();
        attributes.add(AttributeType.builder().name("email").value("email@example.com").build());
        attributes.add(AttributeType.builder().name("name").value("John Doe").build());
        attributes.add(AttributeType.builder().name("phone_number").value("1234567890").build());

        when(cognitoClient.getUser(any(GetUserRequest.class)))
                .thenReturn(GetUserResponse.builder().userAttributes(attributes).build());

        ResponseEntity<UserResponseDTO> responseEntity = userController.user(accessToken);
        assertNotNull(responseEntity.getBody());
        assertEquals("email@example.com", responseEntity.getBody().getEmail());
        assertEquals("John Doe", responseEntity.getBody().getName());
        assertEquals("1234567890", responseEntity.getBody().getPhoneNumber());
    }

    @Test
    void testUserId() {
        String accessToken = "accessToken";

        when(cognitoClient.getUser(any(GetUserRequest.class)))
                .thenReturn(GetUserResponse.builder().username("username").build());

        ResponseEntity<String> responseEntity = userController.userId(accessToken);
        assertEquals("username", responseEntity.getBody());
    }

    @Test
    void testGetAccessToken() {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setUsername("username");
        accessTokenDTO.setPassword("password");

        when(cognitoClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class)))
                .thenReturn(AdminInitiateAuthResponse.builder()
                        .authenticationResult(AuthenticationResultType.builder().accessToken("accessToken").build())
                        .build());

        ResponseEntity<String> responseEntity = userController.getAccessToken(accessTokenDTO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("accessToken", responseEntity.getBody());
    }
}
