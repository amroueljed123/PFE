package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long consumableId;
    
    @Column(nullable = false)
    private String type; // LOW_STOCK, REORDER, EXCESS
    
    @Column(nullable = false)
    private Integer level;
    
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    
    @Column(nullable = false)
    private String status; // ACTIVE, RESOLVED
    
    @Column
    private Long assignedTo;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
