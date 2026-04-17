package com.esb.appback.repository;

import com.esb.appback.model.AnomalyDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnomalyDetectionRepository extends JpaRepository<AnomalyDetection, Long> {
    List<AnomalyDetection> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AnomalyDetection> findBySeverity(String severity);
    List<AnomalyDetection> findByStatus(String status);
    List<AnomalyDetection> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<AnomalyDetection> findByAnomalyType(String anomalyType);
}
