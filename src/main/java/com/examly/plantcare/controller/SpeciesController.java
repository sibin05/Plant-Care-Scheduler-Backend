package com.examly.plantcare.controller;

import com.examly.plantcare.entity.Species;
import com.examly.plantcare.service.SpeciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/species")
public class SpeciesController {

    @Autowired
    private SpeciesService speciesService;

    // GET /api/species?page=0&size=10&difficulty=EASY
    @GetMapping
    public Page<Species> getAllSpecies(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) Species.CareDifficulty difficulty) {
        Pageable pageable = PageRequest.of(page, size);
        return speciesService.getAllSpecies(pageable, difficulty);
    }

    // GET /api/species/{id}
    @GetMapping("/{id}")
    public Species getById(@PathVariable Long id) {
        return speciesService.getById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));
    }

    // GET /api/species/search?q=fern
    @GetMapping("/search")
    public List<Species> search(@RequestParam String q) {
        return speciesService.search(q);
    }

    // GET /api/species/popular
    @GetMapping("/popular")
    public List<Species> getPopular() {
        return speciesService.getPopular();
    }

    // GET /api/species/{id}/care-requirements
    @GetMapping("/{id}/care-requirements")
    public ResponseEntity<Map<String, Object>> getCareRequirements(@PathVariable Long id) {
        Species s = speciesService.getById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));

        Map<String, Object> requirements = new HashMap<>();
        requirements.put("lightRequirements", s.getLightRequirements());
        requirements.put("waterFrequencyDays", s.getWaterFrequencyDays());
        requirements.put("fertilizerFrequencyDays", s.getFertilizerFrequencyDays());
        requirements.put("pruningFrequencyDays", s.getPruningFrequencyDays());
        requirements.put("repottingFrequencyMonths", s.getRepottingFrequencyMonths());
        requirements.put("soilPhMin", s.getSoilPhMin());
        requirements.put("soilPhMax", s.getSoilPhMax());
        requirements.put("temperatureRange", s.getTemperatureMinCelsius() + " - " + s.getTemperatureMaxCelsius());

        return ResponseEntity.ok(requirements);
    }

    // GET /api/species/{id}/common-issues
    @GetMapping("/{id}/common-issues")
    public ResponseEntity<List<String>> getCommonIssues(@PathVariable Long id) {
        Species species = speciesService.getById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));
        return ResponseEntity.ok(species.getCommonIssues());
    }

    // POST /api/species/{id}/care-tips
    @PostMapping("/{id}/care-tips")
    public Species updateCareTips(@PathVariable Long id, @RequestBody List<String> tips) {
        Species species = speciesService.getById(id)
                .orElseThrow(() -> new RuntimeException("Species not found"));
        species.setCareTips(tips);
        return speciesService.save(species);
    }

    // POST /api/species
    @PostMapping
    public ResponseEntity<?> createSpecies(@RequestBody Map<String, Object> request) {
        try {
            Species species = new Species();
            species.setCommonName((String) request.get("commonName"));
            species.setScientificName((String) request.get("scientificName"));
            species.setFamilyName((String) request.get("familyName"));
            
            if (request.get("careDifficulty") != null) {
                species.setCareDifficulty(Species.CareDifficulty.valueOf((String) request.get("careDifficulty")));
            }
            if (request.get("lightRequirements") != null) {
                species.setLightRequirements(Species.LightRequirements.valueOf((String) request.get("lightRequirements")));
            }
            if (request.get("growthRate") != null) {
                species.setGrowthRate(Species.GrowthRate.valueOf((String) request.get("growthRate")));
            }
            
            Species savedSpecies = speciesService.save(species);
            return ResponseEntity.ok(savedSpecies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating species: " + e.getMessage());
        }
    }

    // GET /api/species/categories
    @GetMapping("/categories")
    public Species.CareDifficulty[] getCategories() {
        return Species.CareDifficulty.values();
    }
}
