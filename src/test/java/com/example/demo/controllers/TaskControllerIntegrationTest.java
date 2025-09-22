package com.example.demo.controllers;

import com.example.demo.DTOs.RegisterRequest;
import com.example.demo.DTOs.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login to get token
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123", "Test User");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        authToken = "Bearer " + objectMapper.readTree(loginResponse).get("accessToken").asText();
    }

    @Test
    void createTask_AuthenticatedUser_Success() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Test Task", "Test Description", "PENDING");

        mockMvc.perform(post("/tasks")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void createTask_Unauthorized_ReturnsForbidden() throws Exception {
        TaskRequest taskRequest = new TaskRequest("Test Task", "Test Description", "PENDING");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasks_AuthenticatedUser_Success() throws Exception {
        mockMvc.perform(get("/tasks")
                .header("Authorization", authToken))
                .andExpect(status().isOk());
    }

    @Test
    void getTasks_Unauthorized_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_AuthenticatedUser_Success() throws Exception {
        // First create a task
        TaskRequest createRequest = new TaskRequest("Test Task", "Test Description", "PENDING");
        String createResponse = mockMvc.perform(post("/tasks")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        // Then update it
        mockMvc.perform(put("/tasks/" + taskId)
                .header("Authorization", authToken)
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTask_AuthenticatedUser_Success() throws Exception {
        // First create a task
        TaskRequest createRequest = new TaskRequest("Test Task", "Test Description", "PENDING");
        String createResponse = mockMvc.perform(post("/tasks")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        // Then delete it
        mockMvc.perform(delete("/tasks/" + taskId)
                .header("Authorization", authToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask_Unauthorized_ReturnsForbidden() throws Exception {
        mockMvc.perform(put("/tasks/1")
                .param("status", "COMPLETED"))
                .andExpect(status().isForbidden());
    }
}