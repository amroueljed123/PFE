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
@DiscriminatorValue("PERIPHERIQUE")
public class Peripherique extends Article {

    @Column
    private String marque;

    @Column
    private String typePeripherique; // Clavier, Souris, Webcam, Scanner, Imprimante, USB Hub, etc.

    @Column
    private String interfaceConnexion; // USB, Bluetooth, Sans fil, PS/2

    @Column
    private String modele;
}
