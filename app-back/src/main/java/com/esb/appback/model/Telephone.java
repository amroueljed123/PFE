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
@DiscriminatorValue("TELEPHONE")
public class Telephone extends Article {

    @Column
    private String marque;

    @Column
    private String modele;

    @Column
    private String typeTelephone; // Fixe, Mobile, IP, VoIP

    @Column
    private String systemeExploitation; // Android, iOS, N/A

    @Column
    private String numeroTelephone;
}
