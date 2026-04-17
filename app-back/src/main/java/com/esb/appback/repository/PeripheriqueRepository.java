package com.esb.appback.repository;

import com.esb.appback.model.Peripherique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeripheriqueRepository extends JpaRepository<Peripherique, Long> {
    List<Peripherique> findByMarque(String marque);
    List<Peripherique> findByTypePeripherique(String typePeripherique);
}
