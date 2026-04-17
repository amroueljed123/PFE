package com.esb.appback.controller;

import com.esb.appback.model.*;
import com.esb.appback.service.AnalyticsService;
import com.esb.appback.service.ChronosForecastService;
import com.esb.appback.service.StockDataSeederService;
import com.esb.appback.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private com.esb.appback.service.AnomalyEngineService anomalyEngineService;

    @Autowired
    private ChronosForecastService chronosForecastService;

    @Autowired
    private StockDataSeederService stockDataSeederService;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private com.esb.appback.repository.UserRepository userRepository;

    // Consumption Forecast Endpoints
    @PostMapping("/forecasts/create/{consumableId}/{quantity}/{confidence}/{algorithm}")
    public ResponseEntity<ConsumptionForecast> createForecast(@PathVariable Long consumableId, @PathVariable Integer quantity, @PathVariable Double confidence, @PathVariable String algorithm) {
        return ResponseEntity.ok(analyticsService.createForecast(consumableId, quantity, confidence, algorithm));
    }

    @GetMapping("/forecasts/consumable/{consumableId}")
    public ResponseEntity<List<ConsumptionForecast>> getForecastsForConsumable(@PathVariable Long consumableId) {
        return ResponseEntity.ok(analyticsService.getForecastsForConsumable(consumableId));
    }

    // Anomaly Detection Endpoints
    @PostMapping("/anomalies/trigger-engine")
    public ResponseEntity<java.util.Map<String, String>> triggerAnomalyEngine() {
        String result = anomalyEngineService.runDetectionEngine();
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/anomalies/detect")
    public ResponseEntity<AnomalyDetection> detectAnomaly(@RequestParam String entityType, @RequestParam Long entityId, @RequestParam String anomalyType, @RequestParam String severity, @RequestParam String description, @RequestParam Integer score) {
        return ResponseEntity.ok(analyticsService.detectAnomaly(entityType, entityId, anomalyType, severity, description, score));
    }

    @GetMapping("/anomalies/active")
    public ResponseEntity<List<AnomalyDetection>> getActiveAnomalies() {
        return ResponseEntity.ok(analyticsService.getActiveAnomalies());
    }

    @GetMapping("/anomalies/severity/{severity}")
    public ResponseEntity<List<AnomalyDetection>> getAnomaliesBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(analyticsService.getAnomaliesBySeverity(severity));
    }

    @PostMapping("/anomalies/{anomalyId}/investigate/{investigator}")
    public ResponseEntity<AnomalyDetection> investigateAnomaly(@PathVariable Long anomalyId, @PathVariable Long investigator, @RequestParam String notes) {
        return ResponseEntity.ok(analyticsService.investigateAnomaly(anomalyId, investigator, notes));
    }

    // Optimization Recommendation Endpoints
    @PostMapping("/recommendations/create")
    public ResponseEntity<OptimizationRecommendation> createRecommendation(@RequestParam String type, @RequestParam Long entityId, @RequestParam String recommendation, @RequestParam(required = false) Double expectedSavings, @RequestParam String priority) {
        return ResponseEntity.ok(analyticsService.createRecommendation(type, entityId, recommendation, expectedSavings, priority));
    }

    @GetMapping("/recommendations/status/{status}")
    public ResponseEntity<List<OptimizationRecommendation>> getRecommendationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(analyticsService.getRecommendationsByStatus(status));
    }

    @GetMapping("/recommendations/high-priority")
    public ResponseEntity<List<OptimizationRecommendation>> getHighPriorityRecommendations() {
        return ResponseEntity.ok(analyticsService.getHighPriorityRecommendations());
    }

    @PostMapping("/recommendations/{recId}/approve")
    public ResponseEntity<OptimizationRecommendation> approveRecommendation(@PathVariable Long recId) {
        return ResponseEntity.ok(analyticsService.approveRecommendation(recId));
    }

    // Performance KPI Endpoints
    @PostMapping("/kpis/record/{entityType}/{entityId}/{period}/{kpiName}/{value}")
    public ResponseEntity<PerformanceKPI> recordKPI(@PathVariable String entityType, @PathVariable Long entityId, @PathVariable String period, @PathVariable String kpiName, @PathVariable Double value, @RequestParam(required = false) Double benchmark) {
        return ResponseEntity.ok(analyticsService.recordKPI(entityType, entityId, period, kpiName, value, benchmark));
    }

    @GetMapping("/kpis/{entityType}/{entityId}")
    public ResponseEntity<List<PerformanceKPI>> getKPIsForEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(analyticsService.getKPIsForEntity(entityType, entityId));
    }

    @GetMapping("/kpis/period/{period}")
    public ResponseEntity<List<PerformanceKPI>> getKPIsByPeriod(@PathVariable String period) {
        return ResponseEntity.ok(analyticsService.getKPIsByPeriod(period));
    }

    // ════════════════════════════════════════════════════════════════
    // CHRONOS Time-Series Forecasting Endpoints
    // ════════════════════════════════════════════════════════════════

    @PostMapping("/chronos/run")
    public ResponseEntity<Map<String, Object>> runChronosAnalysis() {
        return ResponseEntity.ok(chronosForecastService.runChronosAnalysis());
    }

    @GetMapping("/chronos/article/{articleId}")
    public ResponseEntity<Map<String, Object>> getArticleForecast(@PathVariable Long articleId) {
        return ResponseEntity.ok(chronosForecastService.getForecastForArticle(articleId));
    }

    // ════════════════════════════════════════════════════════════════
    // Data Seeding Endpoints (for demo/testing)
    // ════════════════════════════════════════════════════════════════

    @PostMapping("/seed/movements")
    public ResponseEntity<Map<String, String>> seedStockMovements() {
        String result = stockDataSeederService.seedStockMovements();
        return ResponseEntity.ok(Map.of("message", result));
    }

    @DeleteMapping("/seed/movements")
    public ResponseEntity<Map<String, String>> clearSeededData() {
        String result = stockDataSeederService.clearSeededData();
        return ResponseEntity.ok(Map.of("message", result));
    }

    @Autowired
    private com.esb.appback.repository.ArticleRepository articleRepository;

    @GetMapping("/movements/recent")
    public ResponseEntity<List<StockMovement>> getRecentMovements() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return ResponseEntity.ok(stockMovementRepository.findByDateBetween(thirtyDaysAgo, LocalDateTime.now()));
    }

    @GetMapping("/movements/all-detailed")
    public ResponseEntity<List<StockMovementDTO>> getAllDetailedMovements() {
        List<StockMovement> movements = stockMovementRepository.findAll();
        // Sort effectively (newest first)
        movements.sort((m1, m2) -> m2.getDate().compareTo(m1.getDate()));

        java.util.Map<Long, Article> articleCache = new java.util.HashMap<>();
        java.util.Map<Long, String> userCache = new java.util.HashMap<>();
        
        List<StockMovementDTO> results = movements.stream().map(m -> {
            StockMovementDTO dto = new StockMovementDTO();
            dto.setId(m.getId());
            dto.setArticleId(m.getArticleId());
            dto.setType(m.getType());
            dto.setQuantity(m.getQuantity());
            dto.setDate(m.getDate());
            dto.setReference(m.getReference());
            dto.setFournisseur(m.getFournisseur());
            dto.setDocumentInfo(m.getDocumentInfo());
            dto.setCost(m.getCost());
            dto.setDescription(m.getDescription());

            if (m.getUserId() != null) {
                String userName = userCache.computeIfAbsent(m.getUserId(), id -> 
                    userRepository.findById(id).map(u -> u.getName()).orElse("Inconnu")
                );
                dto.setUserName(userName);
            } else {
                dto.setUserName("Système");
            }
            
            if (m.getArticleId() != null) {
                Article article = articleCache.computeIfAbsent(m.getArticleId(), id -> 
                    articleRepository.findById(id).orElse(null)
                );
                
                if (article != null) {
                    dto.setArticleName(article.getNom());
                    dto.setArticleCategory(article.getFamille() != null ? article.getFamille() : "Non Classé");
                    dto.setArticleStatus(article.getStatut());
                    dto.setArticleImage(article.getImage());
                }
            } else {
                dto.setArticleName("Article Inconnu");
                dto.setArticleCategory("-");
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/movements/count")
    public ResponseEntity<Map<String, Object>> getMovementCount() {
        long total = stockMovementRepository.count();
        return ResponseEntity.ok(Map.of("total", total));
    }
}

