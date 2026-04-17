package com.esb.appback.repository;

import com.esb.appback.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByStatus(String status);
    List<Inventory> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<Inventory> findByCreatedBy(Long createdBy);
}
