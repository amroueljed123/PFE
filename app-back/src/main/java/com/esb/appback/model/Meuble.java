package com.esb.appback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("MEUBLE")
@EqualsAndHashCode(callSuper = true)
public class Meuble extends Article {
    
    @Column(name = "materiau")
    private String materiau;
    
    @Column(name = "dimension")
    private String dimension;
    
    @Column(name = "poids")
    private Double poids;
}
