package com.esb.appback.controller;

import com.esb.appback.model.*;
import com.esb.appback.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Stock Movement Endpoints
    @PostMapping("/movements/create")
    public ResponseEntity<StockMovement> recordStockMovement(@RequestBody StockMovementDTO dto, @RequestParam(required = false) Long userId) {
        // Fallback userId if not provided in request param but available in context or DTO
        Long finalUserId = userId != null ? userId : 1L; // Default to Admin if unknown for now
        return ResponseEntity.ok(inventoryService.recordStockMovement(dto, finalUserId));
    }

    @PostMapping("/movements/record/{consumableId}/{type}/{quantity}/{userId}")
    public ResponseEntity<StockMovement> recordStockMovementLegacy(@PathVariable Long consumableId, @PathVariable String type, @PathVariable Integer quantity, @PathVariable Long userId) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setArticleId(consumableId);
        dto.setType(type);
        dto.setQuantity(quantity);
        return ResponseEntity.ok(inventoryService.recordStockMovement(dto, userId));
    }

    @GetMapping("/movements/consumable/{consumableId}")
    public ResponseEntity<List<StockMovement>> getStockMovementsByConsumable(@PathVariable Long consumableId) {
        return ResponseEntity.ok(inventoryService.getStockMovementsByConsumable(consumableId));
    }

    // Stock Alert Endpoints
    @PostMapping("/alerts/create/{consumableId}/{type}/{level}")
    public ResponseEntity<StockAlert> createStockAlert(@PathVariable Long consumableId, @PathVariable String type, @PathVariable Integer level) {
        return ResponseEntity.ok(inventoryService.createStockAlert(consumableId, type, level));
    }

    @GetMapping("/alerts/active")
    public ResponseEntity<List<StockAlert>> getActiveStockAlerts() {
        return ResponseEntity.ok(inventoryService.getActiveStockAlerts());
    }

    @GetMapping("/alerts/consumable/{consumableId}")
    public ResponseEntity<List<StockAlert>> getStockAlertsByConsumable(@PathVariable Long consumableId) {
        return ResponseEntity.ok(inventoryService.getStockAlertsByConsumable(consumableId));
    }

    @PostMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<Void> resolveStockAlert(@PathVariable Long alertId) {
        inventoryService.resolveStockAlert(alertId);
        return ResponseEntity.ok().build();
    }

    // Inventory Endpoints
    @PostMapping("/inventories/create/{createdBy}")
    public ResponseEntity<Inventory> createInventory(@PathVariable Long createdBy) {
        return ResponseEntity.ok(inventoryService.createInventory(createdBy));
    }

    @GetMapping("/inventories")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/inventories/{inventoryId}/items")
    public ResponseEntity<List<InventoryItem>> getInventoryItems(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.getInventoryItems(inventoryId));
    }

    @PostMapping("/inventories/{inventoryId}/start")
    public ResponseEntity<Inventory> startInventory(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.startInventory(inventoryId));
    }

    @PostMapping("/inventories/{inventoryId}/complete/{validatedBy}")
    public ResponseEntity<Inventory> completeInventory(@PathVariable Long inventoryId, @PathVariable Long validatedBy) {
        return ResponseEntity.ok(inventoryService.completeInventory(inventoryId, validatedBy));
    }

    @PostMapping("/inventories/{inventoryId}/items/{consumableId}/{theoretical}/{physical}")
    public ResponseEntity<Void> addInventoryItem(@PathVariable Long inventoryId, @PathVariable Long consumableId, @PathVariable Integer theoretical, @PathVariable Integer physical) {
        inventoryService.addInventoryItem(inventoryId, consumableId, theoretical, physical);
        return ResponseEntity.ok().build();
    }

    // Category Endpoints
    @PostMapping("/categories/create/{name}")
    public ResponseEntity<ConsumableCategory> createCategory(@PathVariable String name) {
        return ResponseEntity.ok(inventoryService.createCategory(name));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ConsumableCategory>> getAllCategories() {
        return ResponseEntity.ok(inventoryService.getAllCategories());
    }
}
