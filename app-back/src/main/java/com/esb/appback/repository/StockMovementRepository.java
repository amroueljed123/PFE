package com.esb.appback.repository;

import com.esb.appback.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByConsumableId(Long consumableId);
    List<StockMovement> findByType(String type);
    List<StockMovement> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<StockMovement> findByArticleId(Long articleId);
}
