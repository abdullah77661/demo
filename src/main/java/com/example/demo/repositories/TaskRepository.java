package com.example.demo.repositories;

import com.example.demo.entities.Task;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all tasks for a specific user
    List<Task> findByUser(User user);
}
