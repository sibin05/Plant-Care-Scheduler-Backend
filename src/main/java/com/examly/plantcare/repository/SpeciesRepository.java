package com.examly.plantcare.repository;

import com.examly.plantcare.entity.Species;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpeciesRepository extends JpaRepository<Species, Long> {
    
    Optional<Species> findByCommonName(String commonName);

    Page<Species> findByCareDifficulty(Species.CareDifficulty difficulty, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT s FROM Species s WHERE LOWER(s.commonName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.scientificName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Species> searchByName(String query);

    @Query("SELECT s FROM Species s ORDER BY s.createdDate DESC")
    List<Species> findPopularSpecies();
}
