package com.esb.appback.controller;

import com.esb.appback.model.DemandePrelevement;
import com.esb.appback.service.DemandePrelevementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandePrelevementController {

    @Autowired
    private DemandePrelevementService demandeService;

    @GetMapping
    public ResponseEntity<List<DemandePrelevement>> getAllDemandes() {
        return ResponseEntity.ok(demandeService.getAllDemandes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DemandePrelevement>> getDemandesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(demandeService.getDemandesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long articleId = Long.valueOf(payload.get("articleId").toString());
            Integer quantite = Integer.valueOf(payload.get("quantite").toString());
            
            DemandePrelevement demande = demandeService.createDemande(userId, articleId, quantite);
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatut(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String statut = payload.get("statut");
            DemandePrelevement demande = demandeService.updateStatut(id, statut);
            return ResponseEntity.ok(demande);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
