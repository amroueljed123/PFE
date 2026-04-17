package com.esb.appback.repository;

import com.esb.appback.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    PurchaseOrder findByOrderNumber(String orderNumber);
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<PurchaseOrder> findByRequesterId(Long requesterId);
}
