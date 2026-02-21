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
@DiscriminatorValue("IMPRIMANTE")
@EqualsAndHashCode(callSuper = true)
public class ModeleImprimante extends Article {
    
    @Column(name = "marque")
    private String marque;
    
    @Column(name = "type_imprimante")
    private String typeImprimante;
}
