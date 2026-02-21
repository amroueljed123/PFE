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
@DiscriminatorValue("CHAISE")
@EqualsAndHashCode(callSuper = true)
public class Chaise extends Article {
    
    @Column(name = "type_de_dossier")
    private String typeDeDossier;
    
    @Column(name = "materiau")
    private String materiau;
}
