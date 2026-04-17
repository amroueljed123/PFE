package com.esb.appback.repository;

import com.esb.appback.model.PerformanceKPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceKPIRepository extends JpaRepository<PerformanceKPI, Long> {
    List<PerformanceKPI> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<PerformanceKPI> findByKpiName(String kpiName);
    List<PerformanceKPI> findByPeriod(String period);
    List<PerformanceKPI> findByDateCalculatedBetween(LocalDateTime start, LocalDateTime end);
}
