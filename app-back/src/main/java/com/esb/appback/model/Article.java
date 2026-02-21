package com.esb.appback.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "article_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Consommable.class, name = "CONSOMMABLE"),
        @JsonSubTypes.Type(value = Cartouche.class, name = "CARTOUCHE"),
        @JsonSubTypes.Type(value = Bureau.class, name = "BUREAU"),
        @JsonSubTypes.Type(value = Chaise.class, name = "CHAISE"),
        @JsonSubTypes.Type(value = EtagereIndustrielle.class, name = "ETAGERE"),
        @JsonSubTypes.Type(value = ModeleImprimante.class, name = "IMPRIMANTE"),
        @JsonSubTypes.Type(value = Meuble.class, name = "MEUBLE")
})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codeBarre;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private String unite;

    @Column(nullable = false)
    private String famille;

    @Column(nullable = false)
    private String fournisseur;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    @Column(columnDefinition = "TEXT")
    private String donnee;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
