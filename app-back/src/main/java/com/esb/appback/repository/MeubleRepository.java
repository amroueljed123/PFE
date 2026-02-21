package com.esb.appback.repository;

import com.esb.appback.model.Meuble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeubleRepository extends JpaRepository<Meuble, Long> {
    List<Meuble> findByMateriau(String materiau);
}
