package com.esb.appback.service;

import com.esb.appback.model.*;
import com.esb.appback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private StockAlertRepository stockAlertRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ConsumableCategoryRepository categoryRepository;
    @Autowired
    private ArticleRepository articleRepository;

    // Stock Movement Methods
    @org.springframework.transaction.annotation.Transactional
    public StockMovement recordStockMovement(StockMovementDTO dto, Long userId) {
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new RuntimeException("Article not found"));

        StockMovement movement = new StockMovement();
        movement.setArticleId(dto.getArticleId());
        movement.setType(dto.getType()); // IN or OUT
        movement.setQuantity(dto.getQuantity());
        movement.setReference(dto.getReference());
        movement.setFournisseur(dto.getFournisseur());
        movement.setDocumentInfo(dto.getDocumentInfo());
        movement.setCost(dto.getCost());
        movement.setDescription(dto.getDescription());
        movement.setDate(dto.getDate() != null ? dto.getDate() : LocalDateTime.now());
        movement.setUserId(userId);
        movement.setConsumableId(0L); // Legacy field, keeping for schema compatibility

        // Update Article Quantity
        if ("IN".equalsIgnoreCase(dto.getType())) {
            article.setQuantite(article.getQuantite() + dto.getQuantity());
        } else if ("OUT".equalsIgnoreCase(dto.getType())) {
            if (article.getQuantite() < dto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + article.getNom());
            }
            article.setQuantite(article.getQuantite() - dto.getQuantity());
        }

        articleRepository.save(article);
        return stockMovementRepository.save(movement);
    }

    public List<StockMovement> getStockMovementsByConsumable(Long consumableId) {
        return stockMovementRepository.findByConsumableId(consumableId);
    }

    public List<StockMovement> getStockMovementsBetween(LocalDateTime start, LocalDateTime end) {
        return stockMovementRepository.findByDateBetween(start, end);
    }

    // Stock Alert Methods
    public StockAlert createStockAlert(Long consumableId, String type, Integer level) {
        StockAlert alert = new StockAlert();
        alert.setConsumableId(consumableId);
        alert.setType(type);
        alert.setLevel(level);
        alert.setDateCreated(LocalDateTime.now());
        alert.setStatus("ACTIVE");
        return stockAlertRepository.save(alert);
    }

    public List<StockAlert> getActiveStockAlerts() {
        return stockAlertRepository.findByStatus("ACTIVE");
    }

    public List<StockAlert> getStockAlertsByConsumable(Long consumableId) {
        return stockAlertRepository.findByConsumableId(consumableId);
    }

    public void resolveStockAlert(Long alertId) {
        Optional<StockAlert> opt = stockAlertRepository.findById(alertId);
        if (opt.isPresent()) {
            StockAlert alert = opt.get();
            alert.setStatus("RESOLVED");
            stockAlertRepository.save(alert);
        }
    }

    // Inventory Methods
    public Inventory createInventory(Long createdBy) {
        Inventory inventory = new Inventory();
        inventory.setDate(LocalDateTime.now());
        inventory.setStatus("PLANNED");
        inventory.setCreatedBy(createdBy);
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public List<InventoryItem> getInventoryItems(Long inventoryId) {
        return inventoryItemRepository.findByInventoryId(inventoryId);
    }

    public Inventory startInventory(Long inventoryId) {
        Optional<Inventory> opt = inventoryRepository.findById(inventoryId);
        if (opt.isPresent()) {
            Inventory inv = opt.get();
            inv.setStatus("IN_PROGRESS");
            return inventoryRepository.save(inv);
        }
        return null;
    }

    public Inventory completeInventory(Long inventoryId, Long validatedBy) {
        Optional<Inventory> opt = inventoryRepository.findById(inventoryId);
        if (opt.isPresent()) {
            Inventory inv = opt.get();
            inv.setStatus("COMPLETED");
            inv.setValidatedBy(validatedBy);
            return inventoryRepository.save(inv);
        }
        return null;
    }

    public void addInventoryItem(Long inventoryId, Long consumableId, Integer theoreticalQty, Integer physicalQty) {
        InventoryItem item = new InventoryItem();
        item.setInventoryId(inventoryId);
        item.setConsumableId(consumableId);
        item.setTheoreticalQty(theoreticalQty);
        item.setPhysicalQty(physicalQty);
        item.setVariance(physicalQty - theoreticalQty);
        inventoryItemRepository.save(item);
    }

    // Category Methods
    public ConsumableCategory createCategory(String name) {
        ConsumableCategory cat = new ConsumableCategory();
        cat.setName(name);
        return categoryRepository.save(cat);
    }

    public List<ConsumableCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
