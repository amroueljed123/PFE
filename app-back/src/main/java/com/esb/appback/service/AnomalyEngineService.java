package com.esb.appback.service;

import com.esb.appback.model.AnomalyDetection;
import com.esb.appback.model.StockMovement;
import com.esb.appback.repository.AnomalyDetectionRepository;
import com.esb.appback.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnomalyEngineService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private AnomalyDetectionRepository anomalyDetectionRepository;

    @Autowired
    private AnalyticsService analyticsService;

    // Runs automatically every night at Midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledAnomalyDetection() {
        runDetectionEngine();
    }

    public String runDetectionEngine() {
        System.out.println("[ANOMALY ENGINE] Starting Intelligent Detection Engine...");
        int anomaliesFound = 0;

        // Evaluate OUT movements from the last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<StockMovement> recentMovements = stockMovementRepository.findByDateBetween(sevenDaysAgo, LocalDateTime.now())
                .stream()
                .filter(m -> "OUT".equalsIgnoreCase(m.getType()))
                .collect(Collectors.toList());

        for (StockMovement movement : recentMovements) {
            if (checkBusinessRuleAnomalies(movement)) anomaliesFound++;
            if (checkStatisticalAnomalies(movement)) anomaliesFound++;
        }
        
        System.out.println("[ANOMALY ENGINE] Scan Complete.");
        return "Analyse IA terminée: " + recentMovements.size() + " mouvements évalués, " + anomaliesFound + " anomalies détectées.";
    }

    private boolean checkBusinessRuleAnomalies(StockMovement movement) {
        LocalDateTime date = movement.getDate() != null ? movement.getDate() : movement.getCreatedAt();
        DayOfWeek day = date.getDayOfWeek();
        int hour = date.getHour();

        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        boolean isOutOfHours = (hour < 8 || hour >= 18);

        if (isWeekend || isOutOfHours) {
            String timing = isWeekend ? "Weekend" : "Heures non ouvrables (" + hour + "h)";
            
            // Avoid duplicate tracking
            boolean exists = anomalyDetectionRepository.findAll().stream()
                    .anyMatch(a -> a.getEntityId().equals(movement.getId()) && a.getAnomalyType().equals("HORS_HEURES"));

            if (!exists) {
                int score = isWeekend ? 85 : 60; // 85 = High Action, 60 = Medium Investigation
                String severity = score > 70 ? "CRITICAL" : "MEDIUM";
                String description = "Consommation (" + movement.getQuantity() + " unités) effectuée en " + timing;

                analyticsService.detectAnomaly(
                        "StockMovement",
                        movement.getId(),
                        "HORS_HEURES",
                        severity,
                        description,
                        score
                );
                System.out.println("[ANOMALY ENGINE] Flagged Business Anomaly: " + description);
                return true;
            }
        }
        return false;
    }

    private boolean checkStatisticalAnomalies(StockMovement movement) {
        // Collect 30 days history for this specific item
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<StockMovement> history = stockMovementRepository.findByDateBetween(thirtyDaysAgo, LocalDateTime.now())
                .stream()
                .filter(m -> "OUT".equalsIgnoreCase(m.getType()))
                .filter(m -> m.getConsumableId() != null && m.getConsumableId().equals(movement.getConsumableId()))
                .collect(Collectors.toList());

        if (history.size() < 5) return false; // Not enough data for robust ML/Stats

        List<Integer> quantities = history.stream().map(StockMovement::getQuantity).collect(Collectors.toList());
        double mean = quantities.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        double variance = quantities.stream()
                .mapToDouble(q -> Math.pow(q - mean, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);

        // Z-Score Check (Mean + 2 * StdDev)
        double zScore = stdDev > 0 ? (movement.getQuantity() - mean) / stdDev : 0;
        
        // ML IQR Pseudo-Check (Interquartile Range)
        Collections.sort(quantities);
        int q1 = quantities.get(Math.max(0, quantities.size() / 4));
        int q3 = quantities.get(Math.min(quantities.size() - 1, (quantities.size() * 3) / 4));
        int iqr = q3 - q1;
        double outlierThreshold = q3 + 1.5 * iqr;

        if (zScore > 2.0 || movement.getQuantity() > outlierThreshold) {
            boolean exists = anomalyDetectionRepository.findAll().stream()
                    .anyMatch(a -> a.getEntityId().equals(movement.getId()) && a.getAnomalyType().equals("STAT_SUDDEN_SPIKE"));

            if (!exists) {
                int score = zScore > 3.0 ? 95 : 75;
                String severity = score > 70 ? "CRITICAL" : "MEDIUM";
                String description = String.format("Alerte Quantitative : %d unités (Moyenne = %.1f, Z = %.2f). Dépassement Q3+1.5IQR.", movement.getQuantity(), mean, zScore);

                analyticsService.detectAnomaly(
                        "StockMovement",
                        movement.getId(),
                        "STAT_SUDDEN_SPIKE",
                        severity,
                        description,
                        score
                );
                System.out.println("[ANOMALY ENGINE] Flagged Statistical Spike: " + description);
                return true;
            }
        }
        return false;
    }
}
