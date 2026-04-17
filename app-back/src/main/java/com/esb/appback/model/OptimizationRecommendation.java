package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "optimization_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type; // STOCK_OPTIMIZATION, COST_REDUCTION, REORDER_POINT, etc.
    
    @Column(nullable = false)
    private Long entityId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String recommendation;
    
    @Column
    private Double expectedSavings;
    
    @Column
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Column(nullable = false)
    private String status; // NEW, REVIEWED, APPROVED, IMPLEMENTED, REJECTED
    
    @Column(columnDefinition = "TEXT")
    private String metrics;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
