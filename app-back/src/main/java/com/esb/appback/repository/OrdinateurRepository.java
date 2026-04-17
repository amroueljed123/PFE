package com.esb.appback.repository;

import com.esb.appback.model.Ordinateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdinateurRepository extends JpaRepository<Ordinateur, Long> {
    List<Ordinateur> findByMarque(String marque);
    List<Ordinateur> findByTypeOrdinateur(String typeOrdinateur);
}
