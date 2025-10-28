package com.examly.plantcare.controller;

import com.examly.plantcare.entity.CareTask;
import com.examly.plantcare.service.CareTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private CareTaskService careTaskService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CareTask task) {
        try {
            CareTask savedTask = careTaskService.save(task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating task: " + e.getMessage());
        }
    }
}