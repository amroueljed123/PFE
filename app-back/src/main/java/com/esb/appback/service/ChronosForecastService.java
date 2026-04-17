package com.esb.appback.service;

import com.esb.appback.model.StockMovement;
import com.esb.appback.model.ConsumptionForecast;
import com.esb.appback.repository.StockMovementRepository;
import com.esb.appback.repository.ConsumptionForecastRepository;
import com.esb.appback.repository.ArticleRepository;
import com.esb.appback.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Chronos Time-Series Forecasting Engine
 * 
 * Implements a lightweight time-series analysis algorithm inspired by
 * Facebook Prophet / Amazon Chronos for stock trend prediction.
 * 
 * Features:
 * - Moving Average decomposition
 * - Trend detection (linear regression on daily consumption)
 * - Seasonality detection (weekly patterns)
 * - Stock level projection for next 30 days
 * - Confidence intervals based on historical variance
 */
@Service
public class ChronosForecastService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ConsumptionForecastRepository forecastRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Run Chronos analysis on all articles with sufficient movement history.
     * Returns a summary of all forecasts generated.
     */
    public Map<String, Object> runChronosAnalysis() {
        List<Article> articles = articleRepository.findAll();
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        LocalDateTime now = LocalDateTime.now();

        List<Map<String, Object>> forecasts = new ArrayList<>();
        int analyzed = 0;
        int trendsUp = 0;
        int trendsDown = 0;
        int stable = 0;

        for (Article article : articles) {
            List<StockMovement> movements = stockMovementRepository
                    .findByDateBetween(threeMonthsAgo, now)
                    .stream()
                    .filter(m -> m.getArticleId() != null && m.getArticleId().equals(article.getId()))
                    .sorted(Comparator.comparing(StockMovement::getDate))
                    .collect(Collectors.toList());

            if (movements.size() < 10) continue; // Need minimum data points
            analyzed++;

            Map<String, Object> forecast = analyzeArticle(article, movements);
            forecasts.add(forecast);

            String trend = (String) forecast.get("trend");
            if ("HAUSSE".equals(trend)) trendsUp++;
            else if ("BAISSE".equals(trend)) trendsDown++;
            else stable++;

            // Save forecast to DB
            saveForecast(article, forecast);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", "CHRONOS v1.0");
        result.put("analyzedArticles", analyzed);
        result.put("trendsUp", trendsUp);
        result.put("trendsDown", trendsDown);
        result.put("stable", stable);
        result.put("forecasts", forecasts);
        result.put("timestamp", LocalDateTime.now().toString());

        return result;
    }

    /**
     * Analyze a single article's stock movement history
     */
    private Map<String, Object> analyzeArticle(Article article, List<StockMovement> movements) {
        // Separate IN and OUT
        List<StockMovement> outMovements = movements.stream()
                .filter(m -> "OUT".equalsIgnoreCase(m.getType()))
                .collect(Collectors.toList());

        List<StockMovement> inMovements = movements.stream()
                .filter(m -> "IN".equalsIgnoreCase(m.getType()))
                .collect(Collectors.toList());

        // Build daily consumption time series
        Map<String, Integer> dailyConsumption = new TreeMap<>();
        for (StockMovement m : outMovements) {
            String day = m.getDate().toLocalDate().toString();
            dailyConsumption.merge(day, m.getQuantity(), (a, b) -> a + b);
        }

        List<Integer> dailyValues = new ArrayList<>(dailyConsumption.values());
        if (dailyValues.isEmpty()) {
            return createEmptyForecast(article);
        }

        List<Map<String, Object>> historyData = new ArrayList<>();
        int hIndex = 0;
        for (Map.Entry<String, Integer> entry : dailyConsumption.entrySet()) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", entry.getKey());
            point.put("actual", entry.getValue());
            point.put("dayIndex", hIndex++);
            historyData.add(point);
        }

        // === STATISTICAL ANALYSIS ===
        double mean = dailyValues.stream().mapToInt(i -> i).average().orElse(0);
        double variance = dailyValues.stream().mapToDouble(q -> Math.pow(q - mean, 2)).average().orElse(0);
        double stdDev = Math.sqrt(variance);

        // === TREND DETECTION (Linear Regression) ===
        double[] trend = linearRegression(dailyValues);
        double slope = trend[0];
        double intercept = trend[1];
        double rSquared = trend[2];

        // === MOVING AVERAGES ===
        List<Double> ma7 = movingAverage(dailyValues, 7);
        List<Double> ma14 = movingAverage(dailyValues, 14);

        // === WEEKLY SEASONALITY ===
        double[] weeklyPattern = calculateWeeklyPattern(outMovements);

        // === TREND CLASSIFICATION ===
        String trendDirection;
        double trendStrength;

        if (Math.abs(slope) < 0.1) {
            trendDirection = "STABLE";
            trendStrength = 0;
        } else if (slope > 0) {
            trendDirection = "HAUSSE";
            trendStrength = Math.min(100, (slope / mean) * 100);
        } else {
            trendDirection = "BAISSE";
            trendStrength = Math.min(100, Math.abs(slope / mean) * 100);
        }

        // === FORECAST: Next 30 days projected consumption ===
        List<Map<String, Object>> projections = new ArrayList<>();
        double cumulativeProjected = 0;
        for (int day = 1; day <= 30; day++) {
            double projectedValue = Math.max(0, intercept + slope * (dailyValues.size() + day));
            double lowerBound = Math.max(0, projectedValue - 1.96 * stdDev);
            double upperBound = projectedValue + 1.96 * stdDev;

            // Add weekly seasonality
            int dayOfWeek = LocalDateTime.now().plusDays(day).getDayOfWeek().getValue() - 1;
            projectedValue *= weeklyPattern[dayOfWeek];

            cumulativeProjected += projectedValue;

            Map<String, Object> dayForecast = new LinkedHashMap<>();
            dayForecast.put("day", day);
            dayForecast.put("date", LocalDateTime.now().plusDays(day).toLocalDate().toString());
            dayForecast.put("predicted", Math.round(projectedValue * 10.0) / 10.0);
            dayForecast.put("lowerBound", Math.round(lowerBound * 10.0) / 10.0);
            dayForecast.put("upperBound", Math.round(upperBound * 10.0) / 10.0);
            projections.add(dayForecast);
        }

        // === STOCK DEPLETION ESTIMATE ===
        int currentStock = article.getQuantite() != null ? article.getQuantite() : 0;
        double avgDailyOut = outMovements.stream().mapToInt(StockMovement::getQuantity).sum()
                / (double) Math.max(1, dailyConsumption.size());
        int daysUntilDepletion = avgDailyOut > 0 ? (int) Math.ceil(currentStock / avgDailyOut) : 999;

        // === CONFIDENCE ===
        double confidence = Math.min(95, 50 + (rSquared * 30) + Math.min(15, movements.size() / 5.0));

        // Build result
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", article.getId());
        result.put("articleName", article.getNom());
        result.put("currentStock", currentStock);
        result.put("trend", trendDirection);
        result.put("trendStrength", Math.round(trendStrength * 10.0) / 10.0);
        result.put("dailyAvgConsumption", Math.round(mean * 10.0) / 10.0);
        result.put("stdDev", Math.round(stdDev * 10.0) / 10.0);
        result.put("slope", Math.round(slope * 1000.0) / 1000.0);
        result.put("rSquared", Math.round(rSquared * 1000.0) / 1000.0);
        result.put("confidence", Math.round(confidence * 10.0) / 10.0);
        result.put("projected30DayConsumption", Math.round(cumulativeProjected));
        result.put("daysUntilDepletion", daysUntilDepletion);
        result.put("weeklyPattern", weeklyPattern);
        result.put("movingAvg7", ma7.isEmpty() ? 0 : Math.round(ma7.get(ma7.size() - 1) * 10.0) / 10.0);
        result.put("movingAvg14", ma14.isEmpty() ? 0 : Math.round(ma14.get(ma14.size() - 1) * 10.0) / 10.0);
        result.put("totalOutMovements", outMovements.size());
        result.put("totalInMovements", inMovements.size());
        result.put("history", historyData);
        result.put("projections", projections);

        return result;
    }

    /**
     * Simple Linear Regression: y = slope * x + intercept
     * Returns [slope, intercept, rSquared]
     */
    private double[] linearRegression(List<Integer> values) {
        int n = values.size();
        if (n < 2) return new double[]{0, 0, 0};

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values.get(i);
            sumXY += i * values.get(i);
            sumX2 += (double) i * i;
        }

        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) return new double[]{0, sumY / n, 0};

        double slope = (n * sumXY - sumX * sumY) / denom;
        double intercept = (sumY - slope * sumX) / n;

        // R-squared
        double ssRes = 0, ssTot = 0;
        double meanY = sumY / n;
        for (int i = 0; i < n; i++) {
            double predicted = slope * i + intercept;
            ssRes += Math.pow(values.get(i) - predicted, 2);
            ssTot += Math.pow(values.get(i) - meanY, 2);
        }
        double rSquared = ssTot > 0 ? 1 - (ssRes / ssTot) : 0;

        return new double[]{slope, intercept, Math.max(0, rSquared)};
    }

    /**
     * Calculate moving average
     */
    private List<Double> movingAverage(List<Integer> values, int window) {
        List<Double> result = new ArrayList<>();
        if (values.size() < window) return result;

        for (int i = window - 1; i < values.size(); i++) {
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += values.get(j);
            }
            result.add(sum / window);
        }
        return result;
    }

    /**
     * Calculate weekly seasonality pattern (multiplier for each day of week)
     * Returns array of 7 doubles [Mon, Tue, Wed, Thu, Fri, Sat, Sun]
     */
    private double[] calculateWeeklyPattern(List<StockMovement> outMovements) {
        double[] daySums = new double[7];
        int[] dayCounts = new int[7];

        for (StockMovement m : outMovements) {
            int dow = m.getDate().getDayOfWeek().getValue() - 1; // 0=Mon, 6=Sun
            daySums[dow] += m.getQuantity();
            dayCounts[dow]++;
        }

        double[] pattern = new double[7];
        double totalAvg = 0;
        int activeDays = 0;
        for (int i = 0; i < 7; i++) {
            if (dayCounts[i] > 0) {
                pattern[i] = daySums[i] / dayCounts[i];
                totalAvg += pattern[i];
                activeDays++;
            }
        }
        totalAvg = activeDays > 0 ? totalAvg / activeDays : 1;

        // Normalize to multipliers around 1.0
        for (int i = 0; i < 7; i++) {
            pattern[i] = totalAvg > 0 ? pattern[i] / totalAvg : 1.0;
            pattern[i] = Math.round(pattern[i] * 100.0) / 100.0;
        }

        return pattern;
    }

    private Map<String, Object> createEmptyForecast(Article article) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("articleId", article.getId());
        result.put("articleName", article.getNom());
        result.put("trend", "INSUFFICIENT_DATA");
        result.put("confidence", 0);
        return result;
    }

    private void saveForecast(Article article, Map<String, Object> forecast) {
        ConsumptionForecast cf = new ConsumptionForecast();
        cf.setConsumableId(article.getId());
        cf.setDate(LocalDateTime.now());
        Object projected = forecast.getOrDefault("projected30DayConsumption", 0);
        cf.setPredictedQuantity(projected instanceof Number ? ((Number) projected).intValue() : 0);
        Object confidenceVal = forecast.getOrDefault("confidence", 0.0);
        cf.setConfidence(confidenceVal instanceof Number ? ((Number) confidenceVal).doubleValue() : 0.0);
        cf.setAlgorithmUsed("CHRONOS");
        cf.setHorizonDays(30);
        cf.setCreatedAt(LocalDateTime.now());
        forecastRepository.save(cf);
    }

    /**
     * Get forecast for a specific article
     */
    public Map<String, Object> getForecastForArticle(Long articleId) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) return Collections.singletonMap("error", "Article not found");

        Article article = articleOpt.get();
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<StockMovement> movements = stockMovementRepository
                .findByDateBetween(threeMonthsAgo, LocalDateTime.now())
                .stream()
                .filter(m -> m.getArticleId() != null && m.getArticleId().equals(articleId))
                .sorted(Comparator.comparing(StockMovement::getDate))
                .collect(Collectors.toList());

        if (movements.size() < 10) {
            return createEmptyForecast(article);
        }

        return analyzeArticle(article, movements);
    }
}
