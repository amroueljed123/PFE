package com.esb.appback.service;

import com.esb.appback.model.Article;
import com.esb.appback.model.DemandePrelevement;
import com.esb.appback.model.StockMovementDTO;
import com.esb.appback.model.User;
import com.esb.appback.repository.ArticleRepository;
import com.esb.appback.repository.DemandePrelevementRepository;
import com.esb.appback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DemandePrelevementService {

    @Autowired
    private DemandePrelevementRepository demandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private InventoryService inventoryService;

    public List<DemandePrelevement> getAllDemandes() {
        return demandeRepository.findAll();
    }

    public List<DemandePrelevement> getDemandesByUserId(Long userId) {
        return demandeRepository.findByUserId(userId);
    }

    @Transactional
    public DemandePrelevement createDemande(Long userId, Long articleId, Integer quantite) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + articleId));

        if (article.getQuantite() == null || article.getQuantite() < quantite) {
            throw new RuntimeException("Quantité demandée indisponible en stock.");
        }

        DemandePrelevement demande = new DemandePrelevement();
        demande.setUser(user);
        demande.setArticle(article);
        demande.setQuantite(quantite);
        demande.setStatut("En attente");

        return demandeRepository.save(demande);
    }

    @Transactional
    public DemandePrelevement updateStatut(Long id, String nouveauStatut) {
        DemandePrelevement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande not found with id: " + id));

        String ancienStatut = demande.getStatut();
        Long actorId = getCurrentUserId();

        // Record stock movement (OUT) if transitioning to Approuvée
        boolean isEligibleForApproval = "En attente".equals(ancienStatut) || "Validée".equals(ancienStatut);
        if (isEligibleForApproval && "Approuvée".equals(nouveauStatut)) {
            StockMovementDTO smDto = new StockMovementDTO();
            smDto.setArticleId(demande.getArticle().getId());
            smDto.setQuantity(demande.getQuantite());
            smDto.setType("OUT");
            smDto.setReference("DEM-" + demande.getId());
            smDto.setDescription("Sortie automatique suite à l'approbation de la demande #" + demande.getId() + " par " + demande.getUser().getName());
            
            inventoryService.recordStockMovement(smDto, actorId);
        }
        
        // Record stock movement (IN) if rejecting an already approved request (putting stock back)
        if ("Approuvée".equals(ancienStatut) && "Rejetée".equals(nouveauStatut)) {
            StockMovementDTO smDto = new StockMovementDTO();
            smDto.setArticleId(demande.getArticle().getId());
            smDto.setQuantity(demande.getQuantite());
            smDto.setType("IN");
            smDto.setReference("DEM-" + demande.getId() + "-REV");
            smDto.setDescription("Réintégration automatique suite au rejet de la demande #" + demande.getId());
            
            inventoryService.recordStockMovement(smDto, actorId);
        }

        demande.setStatut(nouveauStatut);
        return demandeRepository.save(demande);
    }

    private Long getCurrentUserId() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).map(User::getId).orElse(1L);
        } catch (Exception e) {
            return 1L; // Fallback to system/admin user
        }
    }
}
