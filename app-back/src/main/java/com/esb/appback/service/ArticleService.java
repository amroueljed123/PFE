package com.esb.appback.service;

import com.esb.appback.model.*;
import com.esb.appback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;
import com.esb.appback.dto.ArticleStatsDTO;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ConsommableRepository consommableRepository;

    @Autowired
    private CartoucheRepository cartoucheRepository;

    @Autowired
    private MeubleRepository meubleRepository;

    @Autowired
    private BureauRepository bureauRepository;

    @Autowired
    private ChaiseRepository chaiseRepository;

    @Autowired
    private EtagereIndustrielleRepository etagereRepository;

    @Autowired
    private ModeleImprimanteRepository imprimanteRepository;

    // Generic Article operations
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public Optional<Article> getArticleByCodeBarre(String codeBarre) {
        return articleRepository.findByCodeBarre(codeBarre);
    }

    public List<Article> getArticlesByType(String type) {
        return articleRepository.findByType(type);
    }

    public List<Article> getArticlesByFamille(String famille) {
        return articleRepository.findByFamille(famille);
    }

    public List<Article> getArticlesByFournisseur(String fournisseur) {
        return articleRepository.findByFournisseur(fournisseur);
    }

    public List<String> getAllTypes() {
        return articleRepository.findAllTypes();
    }

    public List<String> getAllFamilles() {
        return articleRepository.findAllFamilles();
    }

    public Article saveArticle(Article article) {
        // Auto-generate QR code if not already set
        if (article.getQrCode() == null || article.getQrCode().isEmpty()) {
            String code = article.getCodeBarre();
            if (code != null && !code.isEmpty()) {
                article.setQrCode(generateQRCode(code));
            } else {
                article.setQrCode(""); // or skip
            }
        }
        return articleRepository.save(article);
    }

    private String generateQRCode(String data) {
        // Using goqr.me API to generate QR codes
        // Format: https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=<data>
        return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodeData(data);
    }

    private String encodeData(String data) {
        try {
            return java.net.URLEncoder.encode(data, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return data;
        }
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // Category-specific operations
    public List<Consommable> getAllConsommables() {
        return consommableRepository.findAll();
    }

    public List<Cartouche> getAllCartouches() {
        return cartoucheRepository.findAll();
    }

    public List<Cartouche> getCartouchesByColor(String couleur) {
        return cartoucheRepository.findByCouleur(couleur);
    }

    public List<Meuble> getAllMeubles() {
        return meubleRepository.findAll();
    }

    public List<Meuble> getMeublesByMaterial(String materiau) {
        return meubleRepository.findByMateriau(materiau);
    }

    public List<Bureau> getAllBureaux() {
        return bureauRepository.findAll();
    }

    public List<Chaise> getAllChaises() {
        return chaiseRepository.findAll();
    }

    public List<EtagereIndustrielle> getAllEtageres() {
        return etagereRepository.findAll();
    }

    public List<ModeleImprimante> getAllImprimantes() {
        return imprimanteRepository.findAll();
    }

    public List<ModeleImprimante> getImprimantesByMarque(String marque) {
        return imprimanteRepository.findByMarque(marque);
    }

    public ArticleStatsDTO getArticleStats() {
        List<Article> articles = articleRepository.findAll();

        long totalArticles = articles.size();
        double totalValue = articles.stream()
                .filter(a -> a.getPrix() != null && a.getQuantite() != null)
                .mapToDouble(a -> a.getPrix() * a.getQuantite())
                .sum();

        Map<String, Long> countByType = articles.stream()
                .filter(a -> a.getType() != null)
                .collect(Collectors.groupingBy(Article::getType, Collectors.counting()));

        Map<String, Long> countByFamille = articles.stream()
                .filter(a -> a.getFamille() != null)
                .collect(Collectors.groupingBy(Article::getFamille, Collectors.counting()));

        long lowStockCount = articles.stream()
                .filter(a -> a.getQuantite() != null && a.getQuantite() < 5)
                .count();

        return ArticleStatsDTO.builder()
                .totalArticles(totalArticles)
                .totalValue(totalValue)
                .countByType(countByType)
                .countByFamille(countByFamille)
                .lowStockCount(lowStockCount)
                .build();
    }
}
