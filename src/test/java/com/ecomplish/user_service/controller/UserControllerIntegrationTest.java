package com.ecomplish.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "CLIENT_ID=53mpp04dd8k69oj9gprorn0vic",
        "USER_POOL_ID=eu-central-1_tHxxikvel",
        "HOSTED_UI_BASE_URL=https://<DOMAIN>.auth.eu-central-1.amazoncognito.com"
})
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testLoginURL() throws Exception {
        mockMvc.perform(get("/loginURL"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignupURL() throws Exception {
        mockMvc.perform(get("/signupURL"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogoutURL() throws Exception {
        mockMvc.perform(get("/logoutURL"))
                .andExpect(status().isOk());
    }

    @Test
    public void testConfirmUser() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "testuser1");

        mockMvc.perform(post("/confirmUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "testuser1");
        jsonObject.put("name", "Test tttt");
        jsonObject.put("phoneNumber", "+22922233");
        jsonObject.put("email", "aabbba@example.com");

        mockMvc.perform(post("/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangePassword() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "testuser1");
        jsonObject.put("password", "Test@12345");

        MvcResult accessTokenResult = mockMvc.perform(post("/getAccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk()).andReturn();

        String accessToken = accessTokenResult.getResponse().getContentAsString();

        jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("oldPassword", "Test@12345");
        jsonObject.put("newPassword", "Test@123");

        mockMvc.perform(post("/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk());
    }

        @Test
        public void testGetAccessToken() throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "testuser1");
                jsonObject.put("password", "Test@123");

                mockMvc.perform(post("/getAccessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString()))
                        .andExpect(status().isOk());
        }

        @Test
        public void testUserId() throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", "testuser1");
                jsonObject.put("password", "Test@123");

                MvcResult accessTokenResult = mockMvc.perform(post("/getAccessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonObject.toString()))
                        .andExpect(status().isOk()).andReturn();

                String accessToken = accessTokenResult.getResponse().getContentAsString();

                jsonObject = new JSONObject();
                jsonObject.put("username", "testuser1");

                mockMvc.perform(get("/userId")
                                .param("accessToken", accessToken))
                        .andExpect(status().isOk());
        }
}
