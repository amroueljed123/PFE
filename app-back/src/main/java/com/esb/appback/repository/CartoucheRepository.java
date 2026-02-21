package com.esb.appback.repository;

import com.esb.appback.model.Cartouche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartoucheRepository extends JpaRepository<Cartouche, Long> {
    List<Cartouche> findByCouleur(String couleur);
}
