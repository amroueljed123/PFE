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
@DiscriminatorValue("BUREAU")
@EqualsAndHashCode(callSuper = true)
public class Bureau extends Article {
    
    @Column(name = "tiroirs")
    private Integer tiroirs;
    
    @Column(name = "type_de_surface")
    private String typeDeSurface;
}
