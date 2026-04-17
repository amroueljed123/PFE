package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String registration;
    
    @Column
    private String brand;
    
    @Column
    private String model;
    
    @Column
    private Integer year;
    
    @Column(nullable = false)
    private String type;
    
    @Column
    private String status;
    
    @Column
    private Long serviceId;
    
    @Column
    private Double mileage;
    
    @Column
    private String fuelType;
    
    @Column
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
