package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    
    @Column(nullable = false)
    private Long supplierId;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private Double amountHT;
    
    @Column(nullable = false)
    private Double tva;
    
    @Column(nullable = false)
    private Double amountTTC;
    
    @Column
    private String filePath;
    
    @Column
    private Double ocrConfidence;
    
    @Column(nullable = false)
    private String status; // PENDING, PROCESSED, VERIFIED
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
