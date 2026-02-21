package com.esb.appback.repository;

import com.esb.appback.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Optional<Article> findByCodeBarre(String codeBarre);
    
    List<Article> findByType(String type);
    
    List<Article> findByFamille(String famille);
    
    List<Article> findByFournisseur(String fournisseur);
    
    @Query("SELECT DISTINCT a.type FROM Article a")
    List<String> findAllTypes();
    
    @Query("SELECT DISTINCT a.famille FROM Article a")
    List<String> findAllFamilles();
}
