package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.DTO.*;
import com.ecomplish.user_service.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginURL() throws URISyntaxException {
        when(userService.getLoginURL()).thenReturn("mockLoginURL");
        assertEquals("mockLoginURL", userController.loginURL());
    }

    @Test
    public void testSignupURL() throws URISyntaxException {
        when(userService.getSignupURL()).thenReturn("mockSignupURL");
        assertEquals("mockSignupURL", userController.signupURL());
    }

    @Test
    public void testLogoutURL() throws URISyntaxException {
        when(userService.getLogoutURL()).thenReturn("mockLogoutURL");
        assertEquals("mockLogoutURL", userController.logoutURL());
    }

    @Test
    public void testSession() throws URISyntaxException, IOException, InterruptedException {
        String authorizationCode = "testAuthorizationCode";
        UserSessionResponseDTO expectedResponse = new UserSessionResponseDTO();
        when(userService.getSession(authorizationCode)).thenReturn(expectedResponse);
        assertEquals(expectedResponse, userController.session(authorizationCode));
    }

    @Test
    public void testUpdateUser() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        when(userService.updateUser(updateUserDTO)).thenReturn(true);
        assertEquals(true, userController.updateUser(updateUserDTO));
    }

    @Test
    public void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        when(userService.changePassword(changePasswordDTO)).thenReturn(true);
        assertEquals(true, userController.changePassword(changePasswordDTO));
    }

    @Test
    public void testDeleteUser() {
        String accessToken = "testAccessToken";
        when(userService.deleteUser(accessToken)).thenReturn(true);
        assertEquals(true, userController.deleteUser(accessToken));
    }

    @Test
    public void testVerifyAccessToken() {
        String accessToken = "testAccessToken";
        when(userService.verifyAccessToken(accessToken)).thenReturn(true);
        assertEquals(true, userController.verifyAccessToken(accessToken));
    }

    @Test
    public void testRefreshAccessToken() {
        String refreshToken = "testRefreshToken";
        UserSessionResponseDTO expectedResponse = new UserSessionResponseDTO();
        when(userService.refreshAccessToken(refreshToken)).thenReturn(expectedResponse);
        assertEquals(expectedResponse, userController.refreshAccessToken(refreshToken));
    }

    @Test
    public void testUser() {
        String accessToken = "testAccessToken";
        UserResponseDTO expectedResponse = new UserResponseDTO();
        when(userService.getUser(accessToken)).thenReturn(expectedResponse);
        assertEquals(expectedResponse, userController.user(accessToken));
    }

    @Test
    public void testUserId() {
        String accessToken = "testAccessToken";
        String expectedResponse = "testUserId";
        when(userService.getUserId(accessToken)).thenReturn(expectedResponse);
        assertEquals(expectedResponse, userController.userId(accessToken));
    }
}