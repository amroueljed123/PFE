package com.esb.appback.controller;

import com.esb.appback.dto.OcrResultDTO;
import com.esb.appback.model.Invoice;
import com.esb.appback.model.InvoiceItem;
import com.esb.appback.service.InvoiceService;
import com.esb.appback.service.SmartOcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private SmartOcrService smartOcrService;

    @PostMapping("/extract")
    public ResponseEntity<?> extractDataFromInvoice(@RequestParam("file") MultipartFile file) {
        try {
            OcrResultDTO result = smartOcrService.extractData(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error extracting data: " + e.getMessage());
        }
    }

    @PostMapping("/ingest")
    public ResponseEntity<Invoice> ingestOcrInvoice(@RequestBody OcrResultDTO parsedData) {
        return ResponseEntity.ok(invoiceService.ingestOcrResult(parsedData));
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createInvoiceObj(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        Optional<Invoice> invoice = invoiceService.getInvoice(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getInvoicesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Invoice>> getInvoicesBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(invoiceService.getInvoicesBySupplier(supplierId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        invoice.setId(id);
        return ResponseEntity.ok(invoiceService.updateInvoice(invoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invoiceId}/items")
    public ResponseEntity<Void> addInvoiceItem(@PathVariable Long invoiceId, @RequestParam Long consumableId, @RequestParam String description, @RequestParam Integer quantity, @RequestParam Double unitPrice) {
        invoiceService.addInvoiceItem(invoiceId, consumableId, description, quantity, unitPrice);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{invoiceId}/items")
    public ResponseEntity<List<InvoiceItem>> getInvoiceItems(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceItems(invoiceId));
    }
}
