package com.esb.appback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleStatsDTO {
    private Long totalArticles;
    private Double totalValue;
    private Map<String, Long> countByType;
    private Map<String, Long> countByFamille;
    private Long lowStockCount;
}
