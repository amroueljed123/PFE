package com.esb.appback.repository;

import com.esb.appback.model.DemandePrelevement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandePrelevementRepository extends JpaRepository<DemandePrelevement, Long> {
    List<DemandePrelevement> findByUserId(Long userId);
}
