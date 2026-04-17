package com.esb.appback.repository;

import com.esb.appback.model.ConsumptionForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsumptionForecastRepository extends JpaRepository<ConsumptionForecast, Long> {
    List<ConsumptionForecast> findByConsumableId(Long consumableId);
    List<ConsumptionForecast> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<ConsumptionForecast> findByAlgorithmUsed(String algorithmUsed);
}
