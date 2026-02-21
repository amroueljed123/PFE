package com.esb.appback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("CONSOMMABLE")
@EqualsAndHashCode(callSuper = true)
public class Consommable extends Article {
    // Hérite tous les attributs de Article
    // Ajouter des attributs spécifiques si nécessaire
}
