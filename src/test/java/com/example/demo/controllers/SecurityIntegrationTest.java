package com.example.demo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void accessProtectedEndpoints_WithoutToken_ReturnsForbidden() throws Exception {
                mockMvc.perform(get("/tasks"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void accessPublicEndpoints_WithoutToken_Success() throws Exception {
                mockMvc.perform(post("/auth/register")
                                .contentType("application/json")
                                .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}"))
                                .andExpect(status().isOk());
        }

        @Test
        void accessWithInvalidToken_ReturnsForbidden() throws Exception {
                // Use a token with proper base64 encoding but invalid content
                mockMvc.perform(get("/tasks")
                                .header("Authorization",
                                                "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1MDAwMDAwMDB9.invalidsignature123456789"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void accessH2Console_WithoutAuthentication_ReturnsForbidden() throws Exception {
                mockMvc.perform(get("/h2-console"))
                                .andExpect(status().isForbidden());
        }
}