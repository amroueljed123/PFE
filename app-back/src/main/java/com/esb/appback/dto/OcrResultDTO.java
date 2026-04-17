package com.esb.appback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultDTO {
    private String invoiceNumber;
    private String date; // yyyy-MM-dd
    private Double amountHT;
    private Double tva;
    private Double amountTTC;
    private Long supplierId;
    private Integer confidence;
    private java.util.List<ParsedArticleDTO> articles = new java.util.ArrayList<>();
}
