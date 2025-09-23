package com.example.demo.validation;

import com.example.demo.DTOs.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_WithValidData_ShouldPassValidation() {
        // Correct order: (email, password, name)
        RegisterRequest request = new RegisterRequest("john@example.com", "validPassword123", "john");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Should have no violations but had: " + violations);
    }

    @Test
    void registerRequest_WithBlankName_ShouldFailValidation() {
        // Blank name - use correct constructor order
        RegisterRequest request = new RegisterRequest("john@example.com", "validPassword123", "");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void registerRequest_WithInvalidEmail_ShouldFailValidation() {
        RegisterRequest request = new RegisterRequest("invalid-email", "validPassword123", "john");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void registerRequest_WithShortPassword_ShouldFailValidation() {
        RegisterRequest request = new RegisterRequest("john@example.com", "short", "john");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}