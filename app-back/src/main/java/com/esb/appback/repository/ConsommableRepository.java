package com.esb.appback.repository;

import com.esb.appback.model.Consommable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsommableRepository extends JpaRepository<Consommable, Long> {
}
