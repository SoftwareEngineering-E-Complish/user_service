package com.ecomplish.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "testUser0");
        jsonObject.put("email", "test@example.com");
        jsonObject.put("password", "Test@123");

        mockMvc.perform(post("/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testLoginUser() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "testUser10");
        jsonObject.put("password", "Test@123");

        mockMvc.perform(post("/loginUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk());
    }



//    @Test
//    public void testConfirmUser() throws Exception {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("username", "testUser5");
//
//        mockMvc.perform(post("/confirmUser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonObject.toString()))
//                .andExpect(status().isOk());
//    }
}
