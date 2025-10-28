package com.examly.plantcare.service;

import com.examly.plantcare.entity.CareTask;
import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.repository.CareTaskRepository;
import com.examly.plantcare.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class CareTaskService {

    @Autowired
    private CareTaskRepository careTaskRepository;
    
    @Autowired
    private PlantRepository plantRepository;

    public List<CareTask> getTasks(Long plantId, LocalDateTime date, CareTask.Status status) {
        List<CareTask> tasks;
        if (plantId != null && status != null) {
            tasks = careTaskRepository.findByPlant_IdAndStatus(plantId, status);
        } else if (plantId != null) {
            tasks = careTaskRepository.findByPlant_Id(plantId);
        } else if (date != null) {
            LocalDateTime start = date.withHour(0).withMinute(0);
            LocalDateTime end = date.withHour(23).withMinute(59);
            tasks = careTaskRepository.findByScheduledDateBetween(start, end);
        } else {
            tasks = careTaskRepository.findAll();
        }
        // Set plantId for JSON serialization and ensure plant data is loaded
        tasks.forEach(task -> {
            if (task.getPlant() != null) {
                task.setPlantId(task.getPlant().getId());
            }
        });
        System.out.println("Returning " + tasks.size() + " care tasks");
        return tasks;
    }

    public CareTask save(CareTask careTask) {
        // If plant is not set but we have a plant ID, fetch and set the plant
        if (careTask.getPlant() == null && careTask.getPlantId() != null) {
            Optional<Plant> plant = plantRepository.findById(careTask.getPlantId());
            if (plant.isPresent()) {
                careTask.setPlant(plant.get());
            } else {
                throw new RuntimeException("Plant not found with ID: " + careTask.getPlantId());
            }
        }
        // Set default status if not provided
        if (careTask.getStatus() == null) {
            careTask.setStatus(CareTask.Status.PENDING);
        }
        // Set creation date if not provided
        if (careTask.getCreatedDate() == null) {
            careTask.setCreatedDate(LocalDateTime.now());
        }
        return careTaskRepository.save(careTask);
    }

    public Optional<CareTask> getById(Long id) {
        return careTaskRepository.findById(id);
    }

    public void delete(Long id) {
        careTaskRepository.deleteById(id);
    }
    
    public CareTask update(Long id, CareTask updated) {
        CareTask existing = careTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CareTask not found with id " + id));
        
        // Update fields
        existing.setTaskType(updated.getTaskType());
        existing.setScheduledDate(updated.getScheduledDate());
        existing.setPriority(updated.getPriority());
        existing.setNotes(updated.getNotes());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        
        // Handle plant relationship
        if (updated.getPlantId() != null) {
            Optional<Plant> plant = plantRepository.findById(updated.getPlantId());
            if (plant.isPresent()) {
                existing.setPlant(plant.get());
            }
        }
        
        return careTaskRepository.save(existing);
    }

    public CareTask markComplete(Long id) {
        CareTask task = careTaskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(CareTask.Status.COMPLETED);
        task.setCompletedDate(LocalDateTime.now());
        return careTaskRepository.save(task);
    }

    public List<CareTask> getUpcoming() {
        return careTaskRepository.findUpcoming();
    }

    public List<CareTask> getOverdue() {
        return careTaskRepository.findOverdue();
    }

    public List<CareTask> getCalendar(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59);
        return careTaskRepository.findByScheduledDateBetween(start, end);
    }

    public List<CareTask> bulkComplete(List<Long> ids) {
        List<CareTask> tasks = careTaskRepository.findAllById(ids);
        tasks.forEach(task -> {
            task.setStatus(CareTask.Status.COMPLETED);
            task.setCompletedDate(LocalDateTime.now());
        });
        return careTaskRepository.saveAll(tasks);
    }

    public CareTask reschedule(Long id, LocalDateTime newDate) {
        CareTask task = careTaskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setScheduledDate(newDate);
        task.setStatus(CareTask.Status.PENDING);
        return careTaskRepository.save(task);
    }

    // stub for generate schedule
    public List<CareTask> generateSchedule(Long plantId) {
        // Implement based on plant care plan
        throw new UnsupportedOperationException("Schedule generation not implemented yet");
    }
}
