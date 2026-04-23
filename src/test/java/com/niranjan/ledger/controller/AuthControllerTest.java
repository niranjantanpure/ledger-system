package com.niranjan.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niranjan.ledger.dto.LoginRequest;
import com.niranjan.ledger.dto.RegisterRequest;
import com.niranjan.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginUserAndReturnToken() throws Exception {
        // First, register
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setUsername("loginuser");
        regRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        // Then, login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        // First, register
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setUsername("wrongpass");
        regRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        // Then, login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongpass");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        // Register and Login to get token
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setUsername("protecteduser");
        regRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("protecteduser");
        loginRequest.setPassword("password123");

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = com.jayway.jsonpath.JsonPath.read(response, "$.token");

        // Access protected endpoint
        mockMvc.perform(get("/api/v1/accounts")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isForbidden());
    }
}
