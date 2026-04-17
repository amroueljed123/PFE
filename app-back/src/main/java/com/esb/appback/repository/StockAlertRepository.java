package com.esb.appback.repository;

import com.esb.appback.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByConsumableId(Long consumableId);
    List<StockAlert> findByStatus(String status);
    List<StockAlert> findByType(String type);
    List<StockAlert> findByAssignedTo(Long assignedTo);
}
