package com.examly.plantcare.controller;

import com.examly.plantcare.entity.HealthRecord;
import com.examly.plantcare.service.HealthRecordService;
import com.examly.plantcare.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/health-records")
@CrossOrigin(origins = "http://localhost:3000")
public class HealthRecordController {

    @Autowired
    private HealthRecordService service;
    
    @Autowired
    private HealthRecordRepository repository;

    // GET /api/health-records?plantId={id}&fromDate={date}
    @GetMapping
    public ResponseEntity<List<HealthRecord>> getRecords(
            @RequestParam(required = false) Long plantId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate
    ) {
        return ResponseEntity.ok(service.getRecords(plantId, fromDate));
    }

    // POST /api/health-records
    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody HealthRecord record) {
        try {
            System.out.println("Received health record: " + record);
            HealthRecord saved = service.save(record);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("Error creating health record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error creating health record: " + e.getMessage());
        }
    }

    // GET /api/health-records/{id}
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecord> getRecord(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/health-records/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecord(
            @PathVariable Long id,
            @RequestBody HealthRecord updated
    ) {
        try {
            System.out.println("Updating health record " + id + " with data: " + updated);
            HealthRecord updatedRecord = service.update(id, updated);
            return ResponseEntity.ok(updatedRecord);
        } catch (Exception e) {
            System.err.println("Error updating health record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body("Error updating health record: " + e.getMessage());
        }
    }

    // POST /api/health-records/{id}/photos
    @PostMapping("/{id}/photos")
    public ResponseEntity<String> addPhotos(@PathVariable Long id, @RequestBody String photosJson) {
        return ResponseEntity.ok("Photos added for record " + id + ": " + photosJson);
    }

    // GET /api/health-records/{id}/treatments
    @GetMapping("/{id}/treatments")
    public ResponseEntity<String> getTreatments(@PathVariable Long id) {
        return ResponseEntity.ok("List of treatments for record " + id);
    }

    // POST /api/health-records/{id}/treatments
    @PostMapping("/{id}/treatments")
    public ResponseEntity<String> addTreatment(@PathVariable Long id, @RequestBody String treatment) {
        return ResponseEntity.ok("Treatment added to record " + id + ": " + treatment);
    }

    // PUT /api/health-records/{id}/recovery-status
    @PutMapping("/{id}/recovery-status")
    public ResponseEntity<String> updateRecoveryStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok("Recovery status updated for record " + id + " to " + status);
    }

    // GET /api/health-records/symptoms-checker
    @GetMapping("/symptoms-checker")
    public ResponseEntity<String> symptomsChecker(@RequestParam String symptoms) {
        return ResponseEntity.ok("Possible diagnoses for symptoms: " + symptoms);
    }

    // POST /api/health-records/diagnose
    @PostMapping("/diagnose")
    public ResponseEntity<String> diagnose(@RequestBody String diagnosisRequest) {
        return ResponseEntity.ok("Diagnosis result: " + diagnosisRequest);
    }

    // GET /api/health-records/trends/{plantId}
    @GetMapping("/trends/{plantId}")
    public ResponseEntity<String> getTrends(@PathVariable Long plantId) {
        return ResponseEntity.ok("Health trends for plant " + plantId);
    }

    // DELETE /api/health-records/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        try {
            System.out.println("Deleting health record with id: " + id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error deleting health record: " + e.getMessage());
            return ResponseEntity.status(400).body("Error deleting health record: " + e.getMessage());
        }
    }
    
    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        long count = service.getRecords(null, null).size();
        long dbCount = repository.count();
        return ResponseEntity.ok("Total health records in database: " + dbCount + ", Service returns: " + count);
    }
    
    @GetMapping("/test")
    public ResponseEntity<List<HealthRecord>> test() {
        List<HealthRecord> allRecords = repository.findAll();
        System.out.println("Direct repository call returns " + allRecords.size() + " records");
        return ResponseEntity.ok(allRecords);
    }
}
