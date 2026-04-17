package com.esb.appback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object combining StockMovement with Article metadata 
 * to provide rich frontend grids without additional network requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDTO {
    
    private Long id;
    private Long articleId;
    private String type; // IN / OUT
    private Integer quantity;
    private LocalDateTime date;
    private String reference;
    private String fournisseur;
    private String documentInfo;
    private Double cost;
    private String description;
    private String userName;
    
    // Enriched Article Info
    private String articleName;
    private String articleCategory;
    private String articleStatus;
    private String articleImage;
}
