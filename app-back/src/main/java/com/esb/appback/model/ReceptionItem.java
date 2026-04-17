package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reception_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long receptionId;
    
    @Column(nullable = false)
    private Long orderItemId;
    
    @Column(nullable = false)
    private Integer quantityReceived;
    
    @Column
    private String qualityCheck; // OK, NOT_OK
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
}
