package com.esb.appback.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CUSTOM")
@EqualsAndHashCode(callSuper = true)
public class CustomArticle extends Article {
    @Column(name = "custom_category_name")
    private String customCategoryName;
}
