package com.esb.appback.repository;

import com.esb.appback.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEntity(String entity);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
