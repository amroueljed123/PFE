package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long inventoryId;
    
    @Column(nullable = false)
    private Long consumableId;
    
    @Column(nullable = false)
    private Integer theoreticalQty;
    
    @Column(nullable = false)
    private Integer physicalQty;
    
    @Column(nullable = false)
    private Integer variance;
    
    @Column(columnDefinition = "TEXT")
    private String justification;
}
