package com.esb.appback.service;

import com.esb.appback.model.Invoice;
import com.esb.appback.model.InvoiceItem;
import com.esb.appback.model.StockMovementDTO;
import com.esb.appback.model.User;
import com.esb.appback.repository.InvoiceRepository;
import com.esb.appback.repository.InvoiceItemRepository;
import com.esb.appback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceItemRepository invoiceItemRepository;
    @Autowired
    private com.esb.appback.repository.ArticleRepository articleRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Invoice ingestOcrResult(com.esb.appback.dto.OcrResultDTO dto) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : "UNK-" + System.currentTimeMillis());
        invoice.setSupplierId(dto.getSupplierId() != null ? dto.getSupplierId() : 1L);
        invoice.setDate(LocalDateTime.now());
        invoice.setAmountHT(dto.getAmountHT() != null ? dto.getAmountHT() : 0.0);
        invoice.setTva(dto.getTva() != null ? dto.getTva() : 0.0);
        invoice.setAmountTTC(dto.getAmountTTC() != null ? dto.getAmountTTC() : 0.0);
        invoice.setStatus("VERIFIED");
        
        Invoice savedInv = invoiceRepository.save(invoice);
        
        if (dto.getArticles() != null) {
            for(com.esb.appback.dto.ParsedArticleDTO pdto : dto.getArticles()) {
                // Instanciate Sub-Article Database Model using our Factory
                com.esb.appback.model.Article newArticle = ArticleFactory.createFromOcr(pdto);
                
                // Reset to 0 before saving so InventoryService can handle the addition correctly
                newArticle.setQuantite(0);
                newArticle = articleRepository.save(newArticle);
                
                // Add Invoice Link & Record Stock Movement (centralized in addInvoiceItem)
                addInvoiceItem(savedInv.getId(), newArticle.getId(), pdto.getDescription(), pdto.getQuantity(), pdto.getUnitPrice());
            }
        }
        return savedInv;
    }

    private Long getCurrentUserId() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).map(User::getId).orElse(1L);
        } catch (Exception e) {
            return 1L; // Fallback to system/admin user
        }
    }

    public Invoice createInvoice(String invoiceNumber, Long supplierId, Double amountHT, Double tva) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setSupplierId(supplierId);
        invoice.setDate(LocalDateTime.now());
        invoice.setAmountHT(amountHT);
        invoice.setTva(tva);
        invoice.setAmountTTC(amountHT + tva);
        invoice.setStatus("PENDING");
        return invoiceRepository.save(invoice);
    }
    
    public Invoice createInvoiceObj(Invoice invoice) {
        if (invoice.getDate() == null) {
            invoice.setDate(LocalDateTime.now());
        }
        invoice.setCreatedAt(LocalDateTime.now());
        if (invoice.getStatus() == null) {
            invoice.setStatus("PENDING");
        }
        if (invoice.getAmountTTC() == null) {
            invoice.setAmountTTC((invoice.getAmountHT() != null ? invoice.getAmountHT() : 0) + 
                                 (invoice.getTva() != null ? invoice.getTva() : 0));
        }
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> getInvoice(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getInvoicesBySupplier(Long supplierId) {
        return invoiceRepository.findBySupplierId(supplierId);
    }

    public Invoice updateInvoice(Invoice invoice) {
        // Preserve createdAt to avoid null constraint violation
        invoiceRepository.findById(invoice.getId()).ifPresent(existing ->
            invoice.setCreatedAt(existing.getCreatedAt())
        );
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(id);
        invoiceItemRepository.deleteAll(items);
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public void addInvoiceItem(Long invoiceId, Long consumableId, String description, Integer quantity, Double unitPrice) {
        InvoiceItem item = new InvoiceItem();
        item.setInvoiceId(invoiceId);
        item.setConsumableId(consumableId);
        item.setDescription(description);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(quantity * unitPrice);
        invoiceItemRepository.save(item);

        // Auto-record stock movement (IN)
        Optional<Invoice> invOpt = invoiceRepository.findById(invoiceId);
        String ref = invOpt.map(inv -> "Facture #" + inv.getInvoiceNumber()).orElse("Facture #" + invoiceId);
        
        StockMovementDTO smDto = new StockMovementDTO();
        smDto.setArticleId(consumableId);
        smDto.setQuantity(quantity);
        smDto.setType("IN");
        smDto.setReference(ref);
        smDto.setDescription("Entrée automatique via Facture: " + description);
        smDto.setCost(unitPrice);
        
        inventoryService.recordStockMovement(smDto, getCurrentUserId());
    }

    public List<InvoiceItem> getInvoiceItems(Long invoiceId) {
        return invoiceItemRepository.findByInvoiceId(invoiceId);
    }
}
