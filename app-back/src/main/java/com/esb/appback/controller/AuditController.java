package com.esb.appback.controller;

import com.esb.appback.model.AuditLog;
import com.esb.appback.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/log/{userId}/{action}/{entity}")
    public ResponseEntity<AuditLog> logAction(@PathVariable Long userId, @PathVariable String action, @PathVariable String entity, @RequestParam(required = false) String oldValue, @RequestParam(required = false) String newValue, @RequestParam(required = false) String ipAddress) {
        return ResponseEntity.ok(auditService.logAction(userId, action, entity, oldValue, newValue, ipAddress));
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getAuditLogsForUser(userId));
    }

    @GetMapping("/logs/entity/{entity}")
    public ResponseEntity<List<AuditLog>> getAuditLogsForEntity(@PathVariable String entity) {
        return ResponseEntity.ok(auditService.getAuditLogsForEntity(entity));
    }

    @GetMapping("/logs/between")
    public ResponseEntity<List<AuditLog>> getAuditLogsBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getAuditLogsBetween(start, end));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs());
    }
}
