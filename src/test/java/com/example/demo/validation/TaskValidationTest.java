package com.example.demo.validation;

import com.example.demo.DTOs.TaskRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaskValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void taskRequest_WithBlankTitleButValidStatus_ShouldFailValidation() {
        TaskRequest request = new TaskRequest();
        request.setTitle(""); // Empty string - @NotNull + @Size(min=1) should fail this
        request.setDescription("Valid description");
        request.setStatus("PENDING");

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        System.out.println("Violations count: " + violations.size());
        violations.forEach(v -> System.out.println("Violation: " + v.getPropertyPath() + " - " + v.getMessage()));

        // @NotNull + @Size(min=1) should reject empty strings
        assertFalse(violations.isEmpty(), "Should have violations for empty title");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void taskRequest_WithNullTitleButValidStatus_ShouldFailValidation() {
        TaskRequest task = new TaskRequest();
        task.setTitle(null); // Null - @NotNull should fail this
        task.setDescription("Valid description");
        task.setStatus("PENDING");

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(task);
        System.out.println("Violations count: " + violations.size());
        violations.forEach(v -> System.out.println("Violation: " + v.getPropertyPath() + " - " + v.getMessage()));

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void taskRequest_WithWhitespaceTitleButValidStatus_ShouldPassValidation() {
        TaskRequest task = new TaskRequest();
        task.setTitle("   "); // Whitespace - @NotNull + @Size(min=1) allows this (unlike @NotBlank)
        task.setDescription("Valid description");
        task.setStatus("PENDING");

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(task);
        System.out.println("Violations count for whitespace: " + violations.size());
        violations.forEach(v -> System.out.println("Violation: " + v.getPropertyPath() + " - " + v.getMessage()));

        // @NotNull + @Size(min=1) allows whitespace-only strings
        assertTrue(violations.isEmpty(), "Whitespace-only titles should pass with @Size(min=1)");
    }

    @Test
    void taskRequest_WithInvalidStatus_ShouldFailValidation() {
        TaskRequest task = new TaskRequest();
        task.setTitle("Valid Title");
        task.setDescription("Valid description");
        task.setStatus("INVALID_STATUS"); // This should fail

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(task);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }

    @Test
    void taskRequest_WithAllValidData_ShouldPassValidation() {
        TaskRequest task = new TaskRequest();
        task.setTitle("Valid Title");
        task.setDescription("Valid description");
        task.setStatus("PENDING");

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(task);
        assertTrue(violations.isEmpty(), "Should have no violations but had: " + violations);
    }

}