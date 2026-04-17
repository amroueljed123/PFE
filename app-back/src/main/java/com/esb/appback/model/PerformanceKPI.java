package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_kpis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceKPI {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String entityType; // Article, Service, Consumable, etc.
    
    @Column(nullable = false)
    private Long entityId;
    
    @Column(nullable = false)
    private String period; // DAY, WEEK, MONTH, QUARTER, YEAR
    
    @Column(nullable = false)
    private String kpiName; // StockTurnover, CostPerUnit, UtilizationRate, etc.
    
    @Column(nullable = false)
    private Double value;
    
    @Column
    private Double benchmark;
    
    @Column
    private String trend; // UP, DOWN, STABLE
    
    @Column
    private Double performance; // vs benchmark (%)
    
    @Column(nullable = false)
    private LocalDateTime dateCalculated;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
