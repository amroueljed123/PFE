package com.esb.appback.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * OCR Service wrapper
 * Handles invoice document processing
 */
@Service
public class OCRService {
    
    @Autowired
    private PythonIntegration pythonIntegration;
    
    /**
     * Process invoice text from document
     */
    public OCRResult processInvoice(String documentText) {
        try {
            Map<String, Object> result = pythonIntegration.processInvoiceOCR(documentText);
            
            OCRResult ocrResult = new OCRResult();
            
            if (result.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                ocrResult.setInvoiceNumber((String) data.get("invoice_number"));
                ocrResult.setDate((String) data.get("date"));
                ocrResult.setSupplier((String) data.get("supplier"));
                ocrResult.setAmountHT(((Number) data.getOrDefault("amount_ht", 0)).doubleValue());
                ocrResult.setTVA(((Number) data.getOrDefault("tva", 0)).doubleValue());
                ocrResult.setAmountTTC(((Number) data.getOrDefault("amount_ttc", 0)).doubleValue());
            }
            
            if (result.containsKey("validation")) {
                Map<String, Object> validation = (Map<String, Object>) result.get("validation");
                ocrResult.setValid((Boolean) validation.getOrDefault("valid", false));
                ocrResult.setConfidence(((Number) validation.getOrDefault("confidence", 0)).doubleValue());
            }
            
            ocrResult.setStatus((String) result.getOrDefault("status", "error"));
            
            return ocrResult;
        } catch (Exception e) {
            OCRResult errorResult = new OCRResult();
            errorResult.setError("OCR Processing Failed: " + e.getMessage());
            errorResult.setConfidence(0.0);
            return errorResult;
        }
    }
    
    public static class OCRResult {
        private String invoiceNumber;
        private String date;
        private String supplier;
        private Double amountHT;
        private Double tva;
        private Double amountTTC;
        private Double confidence;
        private Boolean valid;
        private String status;
        private String error;
        
        // Getters and Setters
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        
        public Double getAmountHT() { return amountHT; }
        public void setAmountHT(Double amountHT) { this.amountHT = amountHT; }
        
        public Double getTVA() { return tva; }
        public void setTVA(Double tva) { this.tva = tva; }
        
        public Double getAmountTTC() { return amountTTC; }
        public void setAmountTTC(Double amountTTC) { this.amountTTC = amountTTC; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public Boolean getValid() { return valid; }
        public void setValid(Boolean valid) { this.valid = valid; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
