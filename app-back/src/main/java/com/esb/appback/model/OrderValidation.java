package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_validations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderValidation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column(nullable = false)
    private Long validatorId;
    
    @Column(nullable = false)
    private Integer level; // 1, 2, 3, 4
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
