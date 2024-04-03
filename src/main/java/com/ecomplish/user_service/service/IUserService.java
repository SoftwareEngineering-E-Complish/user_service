package com.ecomplish.user_service.service;

import com.ecomplish.user_service.model.DTO.ChangePasswordDTO;
import com.ecomplish.user_service.model.DTO.UpdateUserDTO;
import com.ecomplish.user_service.model.DTO.UserResponseDTO;
import com.ecomplish.user_service.model.DTO.UserSessionResponseDTO;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IUserService {
    String getLoginURL() throws URISyntaxException;

    String getSignupURL() throws URISyntaxException;

    String getLogoutURL() throws URISyntaxException;

    UserSessionResponseDTO getSession(String authorizationCode) throws URISyntaxException, IOException, InterruptedException;

    Boolean updateUser(UpdateUserDTO updateUserDTO);

    Boolean changePassword(ChangePasswordDTO changePasswordDTO);

    Boolean deleteUser(String accessToken);

    Boolean verifyAccessToken(String accessToken);

    UserSessionResponseDTO refreshAccessToken(String refreshToken);

    UserResponseDTO getUser(String accessToken);

    String getUserId(String accessToken);
}
