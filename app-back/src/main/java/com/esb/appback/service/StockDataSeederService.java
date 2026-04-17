package com.esb.appback.service;

import com.esb.appback.model.StockMovement;
import com.esb.appback.repository.StockMovementRepository;
import com.esb.appback.repository.ArticleRepository;
import com.esb.appback.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class StockDataSeederService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Generates 3 months of realistic stock movements for all existing articles.
     * Creates IN (supply) and OUT (consumption) movements with realistic patterns:
     * - Higher consumption on weekdays
     * - Periodic restocking (IN) every 1-2 weeks
     * - Occasional weekend/off-hours activity (for anomaly detection)
     * - Trending patterns (some items trending up, some down)
     */
    public String seedStockMovements() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            return "Aucun article trouvé. Ajoutez des articles d'abord.";
        }

        Random random = new Random(42);
        int totalMovements = 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3);

        for (Article article : articles) {
            // Each article gets a consumption profile
            int baseConsumption = 1 + random.nextInt(5); // 1-5 units base
            double trendFactor = random.nextDouble() * 0.4 - 0.1; // -0.1 to +0.3 (some trending up, few down)
            int restockInterval = 7 + random.nextInt(10); // 7-16 days between restocks
            int restockQuantity = baseConsumption * restockInterval + random.nextInt(10);

            LocalDateTime cursor = threeMonthsAgo;
            int dayCounter = 0;

            int netChange = 0;
            while (cursor.isBefore(now)) {
                int dayOfWeek = cursor.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
                boolean isWeekend = dayOfWeek >= 6;

                // Consumption (OUT) — mostly weekdays, occasional weekends
                if (!isWeekend || random.nextDouble() < 0.15) { // 15% chance of weekend activity
                    // Apply trend: consumption grows/shrinks over time
                    double trend = 1.0 + trendFactor * (dayCounter / 90.0);
                    int qty = Math.max(1, (int) Math.round(baseConsumption * trend * (0.5 + random.nextDouble())));

                    // Occasional spike (anomaly candidate)
                    if (random.nextDouble() < 0.05) {
                        qty = qty * (3 + random.nextInt(3)); // 3-5x spike
                    }

                    int hour = isWeekend
                            ? (random.nextBoolean() ? 10 + random.nextInt(4) : 21 + random.nextInt(2)) // Weekend: day or night
                            : 8 + random.nextInt(9); // Weekday: 8-16h

                    StockMovement out = new StockMovement();
                    out.setConsumableId(article.getId());
                    out.setArticleId(article.getId());
                    out.setType("OUT");
                    out.setQuantity(qty);
                    out.setDate(cursor.withHour(hour).withMinute(random.nextInt(60)));
                    out.setDescription("Consommation quotidienne - " + article.getNom());
                    out.setReference("AUTO-SEED-" + UUID.randomUUID().toString().substring(0, 8));
                    out.setCreatedAt(cursor.withHour(hour));
                    stockMovementRepository.save(out);
                    
                    netChange -= qty;
                    totalMovements++;
                }

                // Restocking (IN) — periodic
                if (dayCounter % restockInterval == 0 && dayCounter > 0) {
                    int restockQty = restockQuantity + random.nextInt(restockQuantity / 2);
                    StockMovement in = new StockMovement();
                    in.setConsumableId(article.getId());
                    in.setArticleId(article.getId());
                    in.setType("IN");
                    in.setQuantity(restockQty);
                    in.setDate(cursor.withHour(9).withMinute(0));
                    in.setDescription("Réapprovisionnement - " + article.getNom());
                    in.setReference("RESTOCK-" + UUID.randomUUID().toString().substring(0, 8));
                    in.setCost((double) restockQty * (article.getPrix() != null ? article.getPrix() : 100));
                    in.setCreatedAt(cursor.withHour(9));
                    stockMovementRepository.save(in);
                    
                    netChange += restockQty;
                    totalMovements++;
                }

                cursor = cursor.plusDays(1);
                dayCounter++;
            }
            
            // Update actual article quantity
            if (article.getQuantite() == null) {
                article.setQuantite(Math.max(0, netChange));
            } else {
                article.setQuantite(Math.max(0, article.getQuantite() + netChange));
            }
            articleRepository.save(article);
        }

        return "Seed terminé: " + totalMovements + " mouvements générés pour " + articles.size() + " articles sur 3 mois.";
    }

    /**
     * Clear all seeded movements
     */
    public String clearSeededData() {
        List<StockMovement> seeded = stockMovementRepository.findAll();
        List<StockMovement> toDelete = new ArrayList<>();
        for (StockMovement m : seeded) {
            if (m.getReference() != null && (m.getReference().startsWith("AUTO-SEED-") || m.getReference().startsWith("RESTOCK-"))) {
                toDelete.add(m);
            }
        }
        stockMovementRepository.deleteAll(toDelete);
        return toDelete.size() + " mouvements seedés supprimés.";
    }
}
