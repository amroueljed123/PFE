package com.esb.appback.repository;

import com.esb.appback.model.Moniteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MoniteurRepository extends JpaRepository<Moniteur, Long> {
    List<Moniteur> findByMarque(String marque);
    List<Moniteur> findByTaille(String taille);
}
