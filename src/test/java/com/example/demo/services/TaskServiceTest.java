package com.example.demo.services;

import com.example.demo.DTOs.TaskRequest;
import com.example.demo.DTOs.TaskResponse;
import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import com.example.demo.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private TaskRequest taskRequest;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        taskRequest = new TaskRequest("Test Task", "Test Description", "PENDING");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("PENDING");
        task.setUser(user);
    }

    @Test
    void createTask_Success() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponse result = taskService.createTask(taskRequest, user);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals("PENDING", result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTasks_Success() {
        // Arrange
        when(taskRepository.findByUser(user)).thenReturn(Arrays.asList(task));

        // Act
        List<TaskResponse> result = taskService.getTasks(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByUser(user);
    }

    @Test
    void updateTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        task.setStatus("COMPLETED");

        // Act
        TaskResponse result = taskService.updateTask(1L, "COMPLETED", user);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_TaskNotFound_ThrowsException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, "COMPLETED", user));
    }

    @Test
    void updateTask_UnauthorizedUser_ThrowsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, "COMPLETED", otherUser));
    }

    @Test
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        // Act
        taskService.deleteTask(1L, user);

        // Assert
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);
    }
}