package com.examly.plantcare.repository;

import com.examly.plantcare.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByPlant_Id(Long plantId);

    List<HealthRecord> findByPlant_IdAndAssessmentDateAfter(Long plantId, LocalDateTime fromDate);
}
