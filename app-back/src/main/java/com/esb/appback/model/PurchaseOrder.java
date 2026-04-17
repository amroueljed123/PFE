package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @Column(nullable = false)
    private Long supplierId;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private String status; // DRAFT, SUBMITTED, APPROVED, REJECTED, COMPLETED, CANCELLED
    
    @Column(nullable = false)
    private Double totalAmount;
    
    @Column
    private Long requesterId;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column
    private String reference;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
