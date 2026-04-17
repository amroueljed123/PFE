package com.esb.appback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedArticleDTO {
    private String type;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private Map<String, String> characteristics = new HashMap<>();
}
