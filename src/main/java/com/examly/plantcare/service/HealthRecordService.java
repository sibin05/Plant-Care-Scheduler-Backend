package com.examly.plantcare.service;

import com.examly.plantcare.entity.HealthRecord;
import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.repository.HealthRecordRepository;
import com.examly.plantcare.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository repository;
    
    @Autowired
    private PlantRepository plantRepository;

    // Fetch health records by plant and optional fromDate
    public List<HealthRecord> getRecords(Long plantId, LocalDateTime fromDate) {
        List<HealthRecord> records;
        if (plantId != null && fromDate != null) {
            records = repository.findByPlant_IdAndAssessmentDateAfter(plantId, fromDate);
        } else if (plantId != null) {
            records = repository.findByPlant_Id(plantId);
        } else {
            records = repository.findAll();
        }
        // Set plantId for JSON serialization and ensure plant data is loaded
        records.forEach(record -> {
            if (record.getPlant() != null) {
                record.setPlantId(record.getPlant().getId());
            }
        });
        System.out.println("Returning " + records.size() + " health records");
        return records;
    }

    // Save new record
    public HealthRecord save(HealthRecord record) {
        // If plant is not set but we have a plant ID, fetch and set the plant
        if (record.getPlant() == null && record.getPlantId() != null) {
            Optional<Plant> plant = plantRepository.findById(record.getPlantId());
            if (plant.isPresent()) {
                record.setPlant(plant.get());
            } else {
                throw new RuntimeException("Plant not found with ID: " + record.getPlantId());
            }
        }
        // Set assessment date if not provided
        if (record.getAssessmentDate() == null) {
            record.setAssessmentDate(LocalDateTime.now());
        }
        return repository.save(record);
    }

    // Get by ID
    public Optional<HealthRecord> getById(Long id) {
        return repository.findById(id);
    }

    // Update record
    public HealthRecord update(Long id, HealthRecord updated) {
        HealthRecord existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HealthRecord not found with id " + id));
        
        // Update fields
        existing.setOverallHealth(updated.getOverallHealth());
        existing.setSymptoms(updated.getSymptoms());
        existing.setNotes(updated.getNotes());
        existing.setRecoveryStatus(updated.getRecoveryStatus());
        
        // Handle plant relationship
        if (updated.getPlantId() != null) {
            Optional<Plant> plant = plantRepository.findById(updated.getPlantId());
            if (plant.isPresent()) {
                existing.setPlant(plant.get());
            }
        }
        
        return repository.save(existing);
    }

    // Delete record
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
