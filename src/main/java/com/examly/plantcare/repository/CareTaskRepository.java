package com.examly.plantcare.repository;

import com.examly.plantcare.entity.CareTask;
import com.examly.plantcare.entity.CareTask.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface CareTaskRepository extends JpaRepository<CareTask, Long> {

    List<CareTask> findByPlant_Id(Long plantId);

    List<CareTask> findByPlant_IdAndStatus(Long plantId, Status status);

    @Query("SELECT c FROM CareTask c WHERE c.scheduledDate >= :start AND c.scheduledDate < :end")
    List<CareTask> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM CareTask c WHERE c.status = 'PENDING' AND c.scheduledDate > CURRENT_TIMESTAMP")
    List<CareTask> findUpcoming();

    @Query("SELECT c FROM CareTask c WHERE c.status = 'PENDING' AND c.scheduledDate < CURRENT_TIMESTAMP")
    List<CareTask> findOverdue();
}
