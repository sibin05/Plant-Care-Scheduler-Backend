package com.examly.plantcare.service;

import com.examly.plantcare.entity.EnvironmentData;
import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.repository.EnvironmentDataRepository;
import com.examly.plantcare.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvironmentDataService {

    @Autowired
    private EnvironmentDataRepository repository;
    
    @Autowired
    private PlantRepository plantRepository;

    public List<EnvironmentData> getEnvironmentData(Long plantId, String locationId, LocalDateTime fromDate) {
        List<EnvironmentData> data;
        if (plantId != null && fromDate != null) {
            data = repository.findByPlant_IdAndRecordedDateBetween(plantId, fromDate, LocalDateTime.now());
        } else if (locationId != null && fromDate != null) {
            data = repository.findByLocationIdAndRecordedDateBetween(locationId, fromDate, LocalDateTime.now());
        } else {
            data = repository.findAll();
        }
        // Set plantId for JSON serialization
        data.forEach(item -> {
            if (item.getPlant() != null) {
                item.setPlantId(item.getPlant().getId());
            }
        });
        return data;
    }

    public EnvironmentData save(EnvironmentData data) {
        // If plant is not set but we have a plant ID, fetch and set the plant
        if (data.getPlant() == null && data.getPlantId() != null) {
            Optional<Plant> plant = plantRepository.findById(data.getPlantId());
            if (plant.isPresent()) {
                data.setPlant(plant.get());
            } else {
                throw new RuntimeException("Plant not found with ID: " + data.getPlantId());
            }
        }
        // Set recorded date if not provided
        if (data.getRecordedDate() == null) {
            data.setRecordedDate(LocalDateTime.now());
        }
        // Set default data source if not provided
        if (data.getDataSource() == null) {
            data.setDataSource(EnvironmentData.DataSource.MANUAL);
        }
        return repository.save(data);
    }

    public EnvironmentData getCurrent(Long plantId) {
        return repository.findTopByPlant_IdOrderByRecordedDateDesc(plantId);
    }

    public List<EnvironmentData> getTrends(Long plantId, LocalDateTime from, LocalDateTime to) {
        return repository.findByPlant_IdAndRecordedDateBetween(plantId, from, to);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    public EnvironmentData update(Long id, EnvironmentData updated) {
        return repository.findById(id)
                .map(existing -> {
                    // Preserve the plant relationship
                    if (updated.getPlant() == null && updated.getPlantId() != null) {
                        Optional<Plant> plant = plantRepository.findById(updated.getPlantId());
                        if (plant.isPresent()) {
                            updated.setPlant(plant.get());
                        }
                    } else if (updated.getPlant() == null) {
                        updated.setPlant(existing.getPlant());
                    }
                    updated.setId(id);
                    // Preserve recorded date if not provided
                    if (updated.getRecordedDate() == null) {
                        updated.setRecordedDate(existing.getRecordedDate());
                    }
                    return repository.save(updated);
                })
                .orElseThrow(() -> new RuntimeException("EnvironmentData not found with id " + id));
    }
}
