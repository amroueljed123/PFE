package com.esb.appback.repository;

import com.esb.appback.model.ModeleImprimante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModeleImprimanteRepository extends JpaRepository<ModeleImprimante, Long> {
    List<ModeleImprimante> findByMarque(String marque);
}
