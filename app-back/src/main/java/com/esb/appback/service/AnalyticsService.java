package com.esb.appback.service;

import com.esb.appback.model.*;
import com.esb.appback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    @Autowired
    private ConsumptionForecastRepository forecastRepository;
    @Autowired
    private AnomalyDetectionRepository anomalyRepository;
    @Autowired
    private OptimizationRecommendationRepository recommendationRepository;
    @Autowired
    private PerformanceKPIRepository kpiRepository;

    // Consumption Forecast Methods
    public ConsumptionForecast createForecast(Long consumableId, Integer predictedQuantity, Double confidence, String algorithm) {
        ConsumptionForecast forecast = new ConsumptionForecast();
        forecast.setConsumableId(consumableId);
        forecast.setPredictedQuantity(predictedQuantity);
        forecast.setConfidence(confidence);
        forecast.setAlgorithmUsed(algorithm);
        forecast.setDate(LocalDateTime.now());
        forecast.setHorizonDays(30); // Default 30 days
        return forecastRepository.save(forecast);
    }

    public List<ConsumptionForecast> getForecastsForConsumable(Long consumableId) {
        return forecastRepository.findByConsumableId(consumableId);
    }

    // Anomaly Detection Methods
    public AnomalyDetection detectAnomaly(String entityType, Long entityId, String anomalyType, String severity, String description, Integer score) {
        AnomalyDetection anomaly = new AnomalyDetection();
        anomaly.setEntityType(entityType);
        anomaly.setEntityId(entityId);
        anomaly.setDate(LocalDateTime.now());
        anomaly.setAnomalyType(anomalyType);
        anomaly.setSeverity(severity);
        anomaly.setDescription(description);
        anomaly.setStatus("ACTIVE");
        anomaly.setAnomalyScore(score);
        return anomalyRepository.save(anomaly);
    }

    public List<AnomalyDetection> getActiveAnomalies() {
        return anomalyRepository.findByStatus("ACTIVE");
    }

    public List<AnomalyDetection> getAnomaliesBySeverity(String severity) {
        return anomalyRepository.findBySeverity(severity);
    }

    public AnomalyDetection investigateAnomaly(Long anomalyId, Long investigator, String notes) {
        Optional<AnomalyDetection> opt = anomalyRepository.findById(anomalyId);
        if (opt.isPresent()) {
            AnomalyDetection anomaly = opt.get();
            anomaly.setInvestigatedBy(investigator);
            anomaly.setInvestigationNotes(notes);
            anomaly.setStatus("INVESTIGATED");
            return anomalyRepository.save(anomaly);
        }
        return null;
    }

    // Optimization Recommendation Methods
    public OptimizationRecommendation createRecommendation(String type, Long entityId, String recommendation, Double expectedSavings, String priority) {
        OptimizationRecommendation rec = new OptimizationRecommendation();
        rec.setType(type);
        rec.setEntityId(entityId);
        rec.setRecommendation(recommendation);
        rec.setExpectedSavings(expectedSavings);
        rec.setPriority(priority);
        rec.setStatus("NEW");
        return recommendationRepository.save(rec);
    }

    public List<OptimizationRecommendation> getRecommendationsByStatus(String status) {
        return recommendationRepository.findByStatus(status);
    }

    public List<OptimizationRecommendation> getHighPriorityRecommendations() {
        return recommendationRepository.findByPriority("CRITICAL");
    }

    public OptimizationRecommendation approveRecommendation(Long recId) {
        Optional<OptimizationRecommendation> opt = recommendationRepository.findById(recId);
        if (opt.isPresent()) {
            OptimizationRecommendation rec = opt.get();
            rec.setStatus("APPROVED");
            return recommendationRepository.save(rec);
        }
        return null;
    }

    // Performance KPI Methods
    public PerformanceKPI recordKPI(String entityType, Long entityId, String period, String kpiName, Double value, Double benchmark) {
        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setEntityType(entityType);
        kpi.setEntityId(entityId);
        kpi.setPeriod(period);
        kpi.setKpiName(kpiName);
        kpi.setValue(value);
        kpi.setBenchmark(benchmark);
        if (benchmark != null && benchmark > 0) {
            kpi.setPerformance((value / benchmark) * 100 - 100);
        }
        kpi.setDateCalculated(LocalDateTime.now());
        return kpiRepository.save(kpi);
    }

    public List<PerformanceKPI> getKPIsForEntity(String entityType, Long entityId) {
        return kpiRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<PerformanceKPI> getKPIsByPeriod(String period) {
        return kpiRepository.findByPeriod(period);
    }
}
