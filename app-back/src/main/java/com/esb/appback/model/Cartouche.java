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
@DiscriminatorValue("CARTOUCHE")
@EqualsAndHashCode(callSuper = true)
public class Cartouche extends Article {
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "couleur")
    private String couleur;
    
    @Column(name = "quantite_specifique")
    private Integer quantiteSpecifique;
}
