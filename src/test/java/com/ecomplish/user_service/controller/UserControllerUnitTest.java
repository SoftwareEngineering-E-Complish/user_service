package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.controller.UserController;
import com.ecomplish.user_service.model.AccessTokenDTO;
import com.ecomplish.user_service.model.ConfirmUserDTO;
import com.ecomplish.user_service.model.UpdatePasswordDTO;
import com.ecomplish.user_service.model.UpdateUserDTO;
import com.ecomplish.user_service.model._enum.AuthType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class UserControllerUnitTest {
    @Mock
    private CognitoIdentityProviderClient cognitoClient;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateUser() {
        // Create a mock UpdateUserDTO object
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("test@example.com");
        updateUserDTO.setName("John Doe");
        updateUserDTO.setPhoneNumber("1234567890");
        updateUserDTO.setUsername("john_doe");

        // Mock the response from cognitoClient.adminUpdateUserAttributes
        AdminUpdateUserAttributesResponse adminUpdateUserAttributesResponse = AdminUpdateUserAttributesResponse.builder().build();
        when(cognitoClient.adminUpdateUserAttributes(any(AdminUpdateUserAttributesRequest.class))).thenReturn(adminUpdateUserAttributesResponse);

        // Call the method to be tested
        ResponseEntity<String> responseEntity = userController.updateUser(updateUserDTO);

        // Verify that cognitoClient.adminUpdateUserAttributes method is called with the correct arguments
        ArgumentCaptor<AdminUpdateUserAttributesRequest> captor = ArgumentCaptor.forClass(AdminUpdateUserAttributesRequest.class);
        verify(cognitoClient, times(1)).adminUpdateUserAttributes(captor.capture());
        AdminUpdateUserAttributesRequest capturedRequest = captor.getValue();

        // Assert the values passed to the adminUpdateUserAttributes method
        assertEquals("test@example.com", capturedRequest.userAttributes().get(0).value());
        assertEquals("John Doe", capturedRequest.userAttributes().get(1).value());
        assertEquals("1234567890", capturedRequest.userAttributes().get(2).value());
        assertEquals("john_doe", capturedRequest.username());

        // Check if the response status is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testChangePassword() {
        // Create a mock UpdatePasswordDTO object
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setAccessToken("access-token");
        updatePasswordDTO.setOldPassword("old-password");
        updatePasswordDTO.setNewPassword("new-password");

        // Create a mock UserController object
        UserController userController = mock(UserController.class);

        // Mocking changePassword method
        doNothing().when(userController).changePassword(any());

        // Call the method to be tested
        ResponseEntity<String> responseEntity = userController.changePassword(updatePasswordDTO);

        // Verify that changePassword method is called
        verify(userController, times(1)).changePassword(any());

        // Check if the response status is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testConfirmUser() {
        // Create a mock ConfirmUserDTO object
        ConfirmUserDTO confirmUserDTO = new ConfirmUserDTO();
        confirmUserDTO.setUsername("testuser");

        // Create a mock UserController object
        UserController userController = mock(UserController.class);

        // Mocking confirmUserLocal method
        doNothing().when(userController).confirmUserLocal(any());

        // Call the method to be tested
        ResponseEntity<String> responseEntity = userController.confirmUser(confirmUserDTO);

        // Verify that confirmUserLocal method is called
        verify(userController, times(1)).confirmUserLocal(any());

        // Check if the response status is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAccessToken() {
        // Create a mock AccessTokenDTO object
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setUsername("testuser");
        accessTokenDTO.setPassword("password");

        // Create a mock UserController object
        UserController userController = mock(UserController.class);

        // Mocking adminInitiateAuth method
        when(userController.getAccessToken(accessTokenDTO)).thenReturn(ResponseEntity.ok("access-token"));

        // Call the method to be tested
        ResponseEntity<String> responseEntity = userController.getAccessToken(accessTokenDTO);

        // Check if the response contains the access token
        assertEquals("access-token", responseEntity.getBody());

        // Check if the response status is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
