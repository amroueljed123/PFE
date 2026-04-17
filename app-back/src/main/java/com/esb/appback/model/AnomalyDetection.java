package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_detections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String entityType; // Article, Consumable, Consumption, etc.
    
    @Column(nullable = false)
    private Long entityId;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private String anomalyType; // UNUSUAL_CONSUMPTION, PRICE_ANOMALY, STOCK_DISCREPANCY, etc.
    
    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String status; // ACTIVE, INVESTIGATED, RESOLVED
    
    @Column
    private Long investigatedBy;
    
    @Column(columnDefinition = "TEXT")
    private String investigationNotes;
    
    @Column(nullable = false)
    private Integer anomalyScore; // 0-100
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
