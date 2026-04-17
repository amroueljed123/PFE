package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long consumableId;
    
    @Column(nullable = false)
    private String type; // IN/OUT
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column
    private Long userId;
    
    @Column
    private Long articleId;
    
    @Column
    private String reference;

    @Column
    private String fournisseur;

    @Column
    private String documentInfo;
    
    @Column
    private Double cost;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
