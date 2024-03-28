package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.DTO.*;
import com.ecomplish.user_service.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.net.URISyntaxException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

    UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @GetMapping("/loginURL")
    public String loginURL() throws URISyntaxException {
        return userService.getLoginURL();
    }

    @GetMapping("/signupURL")
    public String signupURL() throws URISyntaxException {
        return userService.getSignupURL();
    }

    @GetMapping("/logoutURL")
    public String logoutURL() throws URISyntaxException {
        return userService.getLogoutURL();
    }

    @GetMapping("/session")
    public UserSessionResponseDTO session(String authorizationCode) throws URISyntaxException, IOException, InterruptedException {
        return userService.getSession(authorizationCode);
    }

    @PostMapping("/updateUser")
    public Boolean updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO);
    }

    @PostMapping("/changePassword")
    public Boolean changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        return userService.changePassword(changePasswordDTO);
    }

    @GetMapping("/deleteUser")
    public Boolean deleteUser(@RequestParam String accessToken) {
        return userService.deleteUser(accessToken);
    }

    @GetMapping("/verifyAccessToken")
    public Boolean verifyAccessToken(@RequestParam String accessToken) {
        return userService.verifyAccessToken(accessToken);
    }
    @GetMapping("/refreshAccessToken")
    public UserSessionResponseDTO refreshAccessToken(@RequestParam String refreshToken) {
        return userService.refreshAccessToken(refreshToken);
    }

    @GetMapping("/user")
    public UserResponseDTO user(@RequestParam String accessToken) {
        return userService.getUser(accessToken);
    }

    @GetMapping("/userId")
    public String userId(@RequestParam String accessToken) {
        return userService.getUserId(accessToken);
    }
}
