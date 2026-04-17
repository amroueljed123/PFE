package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumption_forecasts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionForecast {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long consumableId;
    
    @Column(nullable = false)
    private LocalDateTime date;
    
    @Column(nullable = false)
    private Integer predictedQuantity;
    
    @Column
    private Double confidence;
    
    @Column
    private String algorithmUsed; // ARIMA, PROPHET, etc.
    
    @Column
    private Integer horizonDays; // Days ahead for forecast
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
