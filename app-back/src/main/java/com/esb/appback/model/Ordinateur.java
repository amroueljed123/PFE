package com.esb.appback.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ORDINATEUR")
public class Ordinateur extends Article {

    @Column
    private String marque;

    @Column
    private String modele;

    @Column
    private String processeur;

    @Column
    private String ram;

    @Column
    private String stockage;

    @Column
    private String systemeExploitation;

    @Column
    private String typeOrdinateur; // Fixe, Portable, Serveur
}
