package com.esb.appback.repository;

import com.esb.appback.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByArticleId(Long articleId);
    List<Maintenance> findByType(String type);
    List<Maintenance> findByDateBetween(LocalDateTime start, LocalDateTime end);
}
