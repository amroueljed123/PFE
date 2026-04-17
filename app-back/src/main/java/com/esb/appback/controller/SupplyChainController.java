package com.esb.appback.controller;

import com.esb.appback.model.*;
import com.esb.appback.service.SupplyChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/supply-chain")
@CrossOrigin(origins = "*")
public class SupplyChainController {

    @Autowired
    private SupplyChainService supplyChainService;

    // Supplier Endpoints
    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplyChainService.createSupplier(supplier));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplyChainService.getAllSuppliers());
    }

    @GetMapping("/suppliers/active")
    public ResponseEntity<List<Supplier>> getActiveSuppliers() {
        return ResponseEntity.ok(supplyChainService.getActiveSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
        Optional<Supplier> supplier = supplyChainService.getSupplier(id);
        return supplier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        return ResponseEntity.ok(supplyChainService.updateSupplier(supplier));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplyChainService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }

    // Purchase Order Endpoints
    @PostMapping("/orders")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder order) {
        return ResponseEntity.ok(supplyChainService.createPurchaseOrder(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(supplyChainService.getAllPurchaseOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrder(@PathVariable Long id) {
        Optional<PurchaseOrder> order = supplyChainService.getPurchaseOrder(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(supplyChainService.getPurchaseOrdersByStatus(status));
    }

    @GetMapping("/orders/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplyChainService.getPurchaseOrdersBySupplier(supplierId));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrder order) {
        order.setId(id);
        return ResponseEntity.ok(supplyChainService.updatePurchaseOrder(order));
    }

    // Order Items Endpoints
    @PostMapping("/order-items")
    public ResponseEntity<OrderItem> addOrderItem(@RequestBody OrderItem item) {
        return ResponseEntity.ok(supplyChainService.addOrderItem(item));
    }

    @GetMapping("/order-items/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(supplyChainService.getOrderItems(orderId));
    }

    // Validation Flow Endpoints
    @PostMapping("/validations/submit/{orderId}/{level}/{validatorId}")
    public ResponseEntity<OrderValidation> submitForValidation(@PathVariable Long orderId, @PathVariable Integer level, @PathVariable Long validatorId) {
        return ResponseEntity.ok(supplyChainService.submitForValidation(orderId, level, validatorId));
    }

    @PostMapping("/validations/{id}/approve")
    public ResponseEntity<OrderValidation> approveOrder(@PathVariable Long id, @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(supplyChainService.approveOrder(id, comments));
    }

    @GetMapping("/validations/pending")
    public ResponseEntity<List<OrderValidation>> getPendingValidations() {
        return ResponseEntity.ok(supplyChainService.getPendingValidations());
    }

    // Reception Endpoints
    @PostMapping("/receptions/record/{orderId}/{receivedBy}")
    public ResponseEntity<Reception> recordReception(@PathVariable Long orderId, @PathVariable Long receivedBy, @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(supplyChainService.recordReception(orderId, receivedBy, notes));
    }

    @PostMapping("/receptions/{receptionId}/items/{orderItemId}/{quantityReceived}")
    public ResponseEntity<Void> addReceptionItem(@PathVariable Long receptionId, @PathVariable Long orderItemId, @PathVariable Integer quantityReceived) {
        supplyChainService.addReceptionItem(receptionId, orderItemId, quantityReceived);
        return ResponseEntity.ok().build();
    }
}
