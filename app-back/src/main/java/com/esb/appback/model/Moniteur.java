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
@DiscriminatorValue("MONITEUR")
public class Moniteur extends Article {

    @Column
    private String marque;

    @Column
    private String taille; // Screen size in inches

    @Column
    private String resolution; // e.g. 1920x1080, 2560x1440

    @Column
    private String typeConnecteur; // HDMI, DisplayPort, VGA, DVI

    @Column
    private String typeEcran; // LCD, LED, IPS, OLED
}
