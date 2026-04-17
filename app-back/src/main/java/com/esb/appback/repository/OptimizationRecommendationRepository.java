package com.esb.appback.repository;

import com.esb.appback.model.OptimizationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OptimizationRecommendationRepository extends JpaRepository<OptimizationRecommendation, Long> {
    List<OptimizationRecommendation> findByType(String type);
    List<OptimizationRecommendation> findByStatus(String status);
    List<OptimizationRecommendation> findByEntityId(Long entityId);
    List<OptimizationRecommendation> findByPriority(String priority);
}
