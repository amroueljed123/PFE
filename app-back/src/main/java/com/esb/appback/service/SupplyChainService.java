package com.esb.appback.service;

import com.esb.appback.model.*;
import com.esb.appback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SupplyChainService {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderValidationRepository orderValidationRepository;
    @Autowired
    private ReceptionRepository receptionRepository;
    @Autowired
    private ReceptionItemRepository receptionItemRepository;

    // Supplier Methods
    public Supplier createSupplier(Supplier supplier) {
        supplier.setCreatedAt(LocalDateTime.now());
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> getSupplier(Long id) {
        return supplierRepository.findById(id);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByActive(true);
    }

    public Supplier updateSupplier(Supplier supplier) {
        supplierRepository.findById(supplier.getId()).ifPresent(existing ->
            supplier.setCreatedAt(existing.getCreatedAt())
        );
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    // Purchase Order Methods
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        order.setDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("DRAFT");
        }
        return purchaseOrderRepository.save(order);
    }

    public Optional<PurchaseOrder> getPurchaseOrder(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    public List<PurchaseOrder> getPurchaseOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    public PurchaseOrder updatePurchaseOrder(PurchaseOrder order) {
        return purchaseOrderRepository.save(order);
    }

    // Order Item Methods
    public OrderItem addOrderItem(OrderItem item) {
        return orderItemRepository.save(item);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // Validation Workflow Methods
    public OrderValidation submitForValidation(Long orderId, Integer level, Long validatorId) {
        OrderValidation validation = new OrderValidation();
        validation.setOrderId(orderId);
        validation.setLevel(level);
        validation.setValidatorId(validatorId);
        validation.setStatus("PENDING");
        validation.setDate(LocalDateTime.now());
        return orderValidationRepository.save(validation);
    }

    public OrderValidation approveOrder(Long validationId, String comments) {
        Optional<OrderValidation> opt = orderValidationRepository.findById(validationId);
        if (opt.isPresent()) {
            OrderValidation validation = opt.get();
            validation.setStatus("APPROVED");
            validation.setComments(comments);
            validation.setDate(LocalDateTime.now());
            return orderValidationRepository.save(validation);
        }
        return null;
    }

    public List<OrderValidation> getPendingValidations() {
        return orderValidationRepository.findByStatus("PENDING");
    }

    // Reception Methods
    public Reception recordReception(Long orderId, Long receivedBy, String notes) {
        Reception reception = new Reception();
        reception.setOrderId(orderId);
        reception.setDate(LocalDateTime.now());
        reception.setReceivedBy(receivedBy);
        reception.setNotes(notes);
        return receptionRepository.save(reception);
    }

    public void addReceptionItem(Long receptionId, Long orderItemId, Integer quantityReceived) {
        ReceptionItem item = new ReceptionItem();
        item.setReceptionId(receptionId);
        item.setOrderItemId(orderItemId);
        item.setQuantityReceived(quantityReceived);
        item.setQualityCheck("OK");
        receptionItemRepository.save(item);
    }
}
