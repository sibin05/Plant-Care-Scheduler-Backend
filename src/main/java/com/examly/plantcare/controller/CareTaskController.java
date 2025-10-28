package com.examly.plantcare.controller;

import com.examly.plantcare.entity.CareTask;
import com.examly.plantcare.service.CareTaskService;
import com.examly.plantcare.repository.CareTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/care-tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class CareTaskController {

    @Autowired
    private CareTaskService careTaskService;
    
    @Autowired
    private CareTaskRepository careTaskRepository;

    @GetMapping
    public ResponseEntity<List<CareTask>> getTasks(
            @RequestParam(required = false) Long plantId,
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) CareTask.Status status) {
        return ResponseEntity.ok(careTaskService.getTasks(plantId, date, status));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CareTask task) {
        try {
            System.out.println("Received care task: " + task);
            CareTask saved = careTaskService.save(task);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("Error creating care task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error creating care task: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareTask> getById(@PathVariable Long id) {
        return careTaskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CareTask updated) {
        try {
            System.out.println("Updating care task " + id + " with data: " + updated);
            CareTask updatedTask = careTaskService.update(id, updated);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            System.err.println("Error updating care task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error updating care task: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            System.out.println("Deleting care task with id: " + id);
            careTaskService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error deleting care task: " + e.getMessage());
            return ResponseEntity.status(400).body("Error deleting care task: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<CareTask> complete(@PathVariable Long id) {
        return ResponseEntity.ok(careTaskService.markComplete(id));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<CareTask>> getUpcoming() {
        return ResponseEntity.ok(careTaskService.getUpcoming());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<CareTask>> getOverdue() {
        return ResponseEntity.ok(careTaskService.getOverdue());
    }

    @PostMapping("/bulk-complete")
    public ResponseEntity<List<CareTask>> bulkComplete(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(careTaskService.bulkComplete(ids));
    }

    @GetMapping("/calendar/{month}/{year}")
    public ResponseEntity<List<CareTask>> getCalendar(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(careTaskService.getCalendar(month, year));
    }

    @PostMapping("/generate-schedule")
    public ResponseEntity<?> generateSchedule(@RequestParam Long plantId) {
        return ResponseEntity.ok(careTaskService.generateSchedule(plantId));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<CareTask> reschedule(@PathVariable Long id, @RequestParam LocalDateTime newDate) {
        return ResponseEntity.ok(careTaskService.reschedule(id, newDate));
    }

    @GetMapping("/statistics")
    public ResponseEntity<String> statistics() {
        return ResponseEntity.ok("Statistics not implemented yet");
    }
    
    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        long count = careTaskService.getTasks(null, null, null).size();
        long dbCount = careTaskRepository.count();
        return ResponseEntity.ok("Total care tasks in database: " + dbCount + ", Service returns: " + count);
    }
    
    @GetMapping("/test")
    public ResponseEntity<List<CareTask>> test() {
        List<CareTask> allTasks = careTaskRepository.findAll();
        System.out.println("Direct repository call returns " + allTasks.size() + " tasks");
        return ResponseEntity.ok(allTasks);
    }
}
