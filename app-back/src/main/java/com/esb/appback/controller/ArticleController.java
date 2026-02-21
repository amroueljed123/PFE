package com.esb.appback.controller;

import com.esb.appback.dto.ArticleStatsDTO;
import com.esb.appback.model.*;
import com.esb.appback.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // Generic Article endpoints
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Article>> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/code/{codeBarre}")
    public ResponseEntity<Optional<Article>> getArticleByCodeBarre(@PathVariable String codeBarre) {
        return ResponseEntity.ok(articleService.getArticleByCodeBarre(codeBarre));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Article>> getArticlesByType(@PathVariable String type) {
        return ResponseEntity.ok(articleService.getArticlesByType(type));
    }

    @GetMapping("/famille/{famille}")
    public ResponseEntity<List<Article>> getArticlesByFamille(@PathVariable String famille) {
        return ResponseEntity.ok(articleService.getArticlesByFamille(famille));
    }

    @GetMapping("/fournisseur/{fournisseur}")
    public ResponseEntity<List<Article>> getArticlesByFournisseur(@PathVariable String fournisseur) {
        return ResponseEntity.ok(articleService.getArticlesByFournisseur(fournisseur));
    }

    @GetMapping("/types/all")
    public ResponseEntity<List<String>> getAllTypes() {
        return ResponseEntity.ok(articleService.getAllTypes());
    }

    @GetMapping("/familles/all")
    public ResponseEntity<List<String>> getAllFamilles() {
        return ResponseEntity.ok(articleService.getAllFamilles());
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        System.out.println("DEBUG: Received article of class: " + article.getClass().getName());
        Article saved = articleService.saveArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        System.out.println("DEBUG: Updating article of class: " + article.getClass().getName());
        Optional<Article> existing = articleService.getArticleById(id);
        if (existing.isPresent()) {
            article.setId(id);
            return ResponseEntity.ok(articleService.saveArticle(article));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // Category-specific endpoints
    @GetMapping("/consommables/all")
    public ResponseEntity<List<Consommable>> getAllConsommables() {
        return ResponseEntity.ok(articleService.getAllConsommables());
    }

    @GetMapping("/cartouches/all")
    public ResponseEntity<List<Cartouche>> getAllCartouches() {
        return ResponseEntity.ok(articleService.getAllCartouches());
    }

    @GetMapping("/cartouches/couleur/{couleur}")
    public ResponseEntity<List<Cartouche>> getCartouchesByColor(@PathVariable String couleur) {
        return ResponseEntity.ok(articleService.getCartouchesByColor(couleur));
    }

    @GetMapping("/meubles/all")
    public ResponseEntity<List<Meuble>> getAllMeubles() {
        return ResponseEntity.ok(articleService.getAllMeubles());
    }

    @GetMapping("/meubles/materiau/{materiau}")
    public ResponseEntity<List<Meuble>> getMeublesByMaterial(@PathVariable String materiau) {
        return ResponseEntity.ok(articleService.getMeublesByMaterial(materiau));
    }

    @GetMapping("/bureaux/all")
    public ResponseEntity<List<Bureau>> getAllBureaux() {
        return ResponseEntity.ok(articleService.getAllBureaux());
    }

    @GetMapping("/chaises/all")
    public ResponseEntity<List<Chaise>> getAllChaises() {
        return ResponseEntity.ok(articleService.getAllChaises());
    }

    @GetMapping("/etageres/all")
    public ResponseEntity<List<EtagereIndustrielle>> getAllEtageres() {
        return ResponseEntity.ok(articleService.getAllEtageres());
    }

    @GetMapping("/imprimantes/all")
    public ResponseEntity<List<ModeleImprimante>> getAllImprimantes() {
        return ResponseEntity.ok(articleService.getAllImprimantes());
    }

    @GetMapping("/stats")
    public ResponseEntity<ArticleStatsDTO> getArticleStats() {
        return ResponseEntity.ok(articleService.getArticleStats());
    }

    @GetMapping("/imprimantes/marque/{marque}")
    public ResponseEntity<List<ModeleImprimante>> getImprimantesByMarque(@PathVariable String marque) {
        return ResponseEntity.ok(articleService.getImprimantesByMarque(marque));
    }
}
