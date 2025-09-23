package com.example.demo.services;

import com.example.demo.DTOs.TaskRequest;
import com.example.demo.DTOs.TaskResponse;
import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import com.example.demo.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request, User user) {
        // Validate task title uniqueness for this user (optional)
        validateTaskTitleUniqueness(request.getTitle(), user);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public List<TaskResponse> getTasks(User user) {
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long id, String status, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Validate task ownership
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You can only update your own tasks");
        }

        // Validate status transition (optional business rule)
        validateStatusTransition(task.getStatus(), status);

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Validate task ownership
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You can only delete your own tasks");
        }

        taskRepository.delete(task);
    }

    private TaskResponse convertToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getUser().getId());
    }

    // Additional validation methods
    private void validateTaskTitleUniqueness(String title, User user) {
        List<Task> userTasks = taskRepository.findByUser(user);
        boolean titleExists = userTasks.stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title));

        if (titleExists) {
            throw new RuntimeException("Task with this title already exists");
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Example: Prevent moving from COMPLETED back to PENDING
        if ("COMPLETED".equals(currentStatus) && "PENDING".equals(newStatus)) {
            throw new RuntimeException("Cannot change status from COMPLETED to PENDING");
        }
    }
}