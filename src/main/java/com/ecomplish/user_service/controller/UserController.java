package com.ecomplish.user_service.controller;

import com.ecomplish.user_service.model.DTO.AccessTokenDTO;
import com.ecomplish.user_service.model.DTO.ChangePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.UserResponseDTO;
import com.ecomplish.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
public class UserController {

    UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @GetMapping("/loginURL")
    public String loginURL() throws URISyntaxException {
        return userService.loginURL();
    }

    @GetMapping("/signupURL")
    public String StringsignupURL() throws URISyntaxException {
        return userService.signupURL();
    }

    @GetMapping("/logoutURL")
    public String StringlogoutURL() throws URISyntaxException {
        return userService.logoutURL();
    }

    @PostMapping("/updateUser")
    public Boolean updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO);
    }

    @PostMapping("/changePassword")
    public Boolean changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        return userService.changePassword(changePasswordDTO);
    }

    @PostMapping("/deleteUser")
    public Boolean deleteUser(@RequestParam String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("/verifyAccessToken")
    public Boolean verifyAccessToken(@RequestParam String accessToken) {
        return userService.verifyAccessToken(accessToken);
    }

    @GetMapping("/user")
    public UserResponseDTO user(@RequestParam String accessToken) {
        return userService.user(accessToken);
    }

    @GetMapping("/userId")
    public String userId(@RequestParam String accessToken) {
        return userService.userId(accessToken);
    }

    @PostMapping("/getAccessToken")
    public String getAccessToken(@RequestBody AccessTokenDTO accessTokenDTO) {
        return userService.getAccessToken(accessTokenDTO);
    }
}
