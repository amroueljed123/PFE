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
@DiscriminatorValue("ETAGERE")
@EqualsAndHashCode(callSuper = true)
public class EtagereIndustrielle extends Article {
    
    @Column(name = "charge_maximale")
    private Double chargeMaximale;
    
    @Column(name = "nombre_etageres")
    private Integer nombreEtageres;
}
