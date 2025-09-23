package com.example.demo.DTOs;

import jakarta.validation.constraints.NotEmpty; // ← CHANGE IMPORT
import jakarta.validation.constraints.Pattern;

public class TaskRequest {

    @NotEmpty(message = "Title is required") // ← USE @NotEmpty
    private String title;

    private String description;

    @NotEmpty(message = "Status is required") // ← USE @NotEmpty
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Status must be PENDING, IN_PROGRESS, or COMPLETED")
    private String status;

    public TaskRequest() {
    }

    // All-args constructor
    public TaskRequest(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
