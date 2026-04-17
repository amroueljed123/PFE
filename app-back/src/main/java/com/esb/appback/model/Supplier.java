package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String supplierCode;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column
    private String contact;
    
    @Column
    private String email;
    
    @Column
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column
    private Double rating;
    
    @Column
    private String paymentTerms;
    
    @Column
    private Boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
