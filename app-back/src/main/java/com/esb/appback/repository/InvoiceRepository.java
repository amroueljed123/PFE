package com.esb.appback.repository;

import com.esb.appback.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Invoice findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatus(String status);
    List<Invoice> findBySupplierId(Long supplierId);
    List<Invoice> findByDateBetween(LocalDateTime start, LocalDateTime end);
}
