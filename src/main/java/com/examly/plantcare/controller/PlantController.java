package com.examly.plantcare.controller;

import com.examly.plantcare.dto.PlantCreateRequest;
import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.service.PlantService;
import com.examly.plantcare.service.PlantScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

// import java.util.List;
// import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "http://localhost:3000")
public class PlantController {

    @Autowired
    private final PlantService plantService;
    
    @Autowired
    private PlantScheduleService scheduleService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    // ✅ GET /api/plants?page=&size=&sort=
    @GetMapping
    public Page<Plant> getAllPlants(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return plantService.getAllPlants(pageable);
    }

    // ✅ GET /api/plants/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        Optional<Plant> plant = plantService.getPlantById(id);
        return plant.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ✅ POST /api/plants (owner auto-assigned from JWT)
    @PostMapping
    public ResponseEntity<Plant> createPlant(@RequestBody PlantCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Plant plant = request.toPlant();
        Plant savedPlant = plantService.createPlantForLoggedInUser(username, plant);
        return ResponseEntity.ok(savedPlant);
    }

    // ✅ PUT /api/plants/{id}
    @PutMapping("/{id}")
    public Plant updatePlant(@PathVariable Long id, @RequestBody Plant plant) {
        return plantService.updatePlant(id, plant);
    }

    // ✅ DELETE /api/plants/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ GET /api/plants/{id}/schedule
    @GetMapping("/{id}/schedule")
    public ResponseEntity<?> getPlantSchedule(@PathVariable Long id) {
        Optional<Plant> plantOpt = plantService.getPlantById(id);
        if (plantOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scheduleService.getPlantSchedule(plantOpt.get()));
    }

    // // ✅ GET /api/plants/search?q=&filters=
    // @GetMapping("/search")
    // public List<Plant> searchPlants(@RequestParam String q,
    //                                 @RequestParam(required = false) Map<String, String> filters) {
    //     return plantService.searchPlants(q, filters);
    // }

    // // ✅ POST /api/plants/{id}/photos
    // @PostMapping("/{id}/photos")
    // public ResponseEntity<String> uploadPlantPhoto(@PathVariable Long id, @RequestBody String photoUrl) {
    //     plantService.addPhoto(id, photoUrl);
    //     return ResponseEntity.ok("Photo added successfully");
    // }

    // // ✅ GET /api/plants/{id}/photos
    // @GetMapping("/{id}/photos")
    // public List<String> getPlantPhotos(@PathVariable Long id) {
    //     return plantService.getPhotos(id);
    // }

    // // ✅ PUT /api/plants/{id}/health-status
    // @PutMapping("/{id}/health-status")
    // public ResponseEntity<Plant> updateHealthStatus(@PathVariable Long id, @RequestBody String status) {
    //     return ResponseEntity.ok(plantService.updateHealthStatus(id, status));
    // }

    // // ✅ GET /api/plants/{id}/care-history
    // @GetMapping("/{id}/care-history")
    // public List<String> getCareHistory(@PathVariable Long id) {
    //     return plantService.getCareHistory(id);
    // }

    // // ✅ POST /api/plants/{id}/notes
    // @PostMapping("/{id}/notes")
    // public ResponseEntity<Plant> addNotes(@PathVariable Long id, @RequestBody String notes) {
    //     return ResponseEntity.ok(plantService.addNotes(id, notes));
    // }

    // // ✅ GET /api/plants/dashboard-summary
    // @GetMapping("/dashboard-summary")
    // public Map<String, Object> getDashboardSummary() {
    //     return plantService.getDashboardSummary();
    // }

    // // ✅ GET /api/plants/needing-attention
    // @GetMapping("/needing-attention")
    // public List<Plant> getPlantsNeedingAttention() {
    //     return plantService.getPlantsNeedingAttention();
    // }

    // // ✅ POST /api/plants/bulk-update
    // @PostMapping("/bulk-update")
    // public ResponseEntity<String> bulkUpdate(@RequestBody List<Plant> plants) {
    //     plantService.bulkUpdate(plants);
    //     return ResponseEntity.ok("Bulk update completed");
    // }
}
