package com.examly.plantcare.service;

import com.examly.plantcare.entity.Species;
import com.examly.plantcare.repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpeciesService {

    @Autowired
    private SpeciesRepository speciesRepository;

    public Page<Species> getAllSpecies(Pageable pageable, Species.CareDifficulty difficulty) {
        if (difficulty != null) {
            return speciesRepository.findByCareDifficulty(difficulty, pageable);
        }
        return speciesRepository.findAll(pageable);
    }

    public Optional<Species> getById(Long id) {
        return speciesRepository.findById(id);
    }

    public List<Species> search(String query) {
        return speciesRepository.searchByName(query);
    }

    public List<Species> getPopular() {
        return speciesRepository.findPopularSpecies();
    }

    public Species save(Species species) {
        return speciesRepository.save(species);
    }
}
