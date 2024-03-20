package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.DTO.*;
import com.ecomplish.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    private final UserController userController;

    public UserControllerTest() {
        CognitoIdentityProviderClient cognitoClient = Mockito.mock(CognitoIdentityProviderClient.class);
        UserService userService = Mockito.mock(UserService.class);
        userService.cognitoClient = cognitoClient;
        userService.USER_POOL_ID = "eu-central-1_123";
        userService.CLIENT_ID = "AAAAAAA";
        userService.HOSTED_UI_BASE_URL = "https://<DOMAIN>.auth.test.example.com";

        this.userController = new UserController();
        this.userController.userService = userService;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testLoginURL() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/loginURL"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testSignupURL() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signupURL"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testLogoutURL() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/logoutURL"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testSession() throws Exception {

        when(this.userController.userService.getSession("accessToken")).thenReturn(new UserSessionResponseDTO(
                "accessToken",
                "idToken",
                "refreshToken",
                3600));
        mockMvc.perform(MockMvcRequestBuilders.get("/session").param("accessToken", "accessToken"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testUpdateUser() throws Exception {
        when(this.userController.userService.updateUser(any(UpdateUserDTO.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/updateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangePassword() throws Exception {
        when(this.userController.userService.changePassword(any(ChangePasswordDTO.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(this.userController.userService.deleteUser("accessToken")).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/deleteUser")
                .param("accessToken", "accessToken"))
                .andExpect(status().isOk());
    }

    @Test
    public void testVerifyAccessToken() throws Exception {
        when(this.userController.userService.verifyAccessToken("accessToken")).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/verifyAccessToken")
                .param("accessToken", "accessToken"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUser() throws Exception {
        when(this.userController.userService.getUser("accessToken")).thenReturn(new UserResponseDTO(
                "testuser1",
                "test@example.com",
                "name",
                "+123456"));
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .param("accessToken", "accessToken"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserId() throws Exception {
        when(this.userController.userService.getUserId("accessToken")).thenReturn("userId");
        mockMvc.perform(MockMvcRequestBuilders.get("/userId")
                .param("accessToken", "accessToken"))
                .andExpect(status().isOk());
    }
}
