package com.example.demo.controllers;

import com.example.demo.DTOs.TaskRequest;
import com.example.demo.DTOs.TaskResponse;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.services.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.taskService = taskService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    private User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
            @RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        return ResponseEntity.ok(taskService.createTask(request, user));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        return ResponseEntity.ok(taskService.getTasks(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
            @RequestParam @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Status must be PENDING, IN_PROGRESS, or COMPLETED") String status,
            @RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        return ResponseEntity.ok(taskService.updateTask(id, status, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        taskService.deleteTask(id, user);
        return ResponseEntity.ok().build();
    }
}