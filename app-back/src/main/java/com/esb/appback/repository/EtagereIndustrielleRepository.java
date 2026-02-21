package com.esb.appback.repository;

import com.esb.appback.model.EtagereIndustrielle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtagereIndustrielleRepository extends JpaRepository<EtagereIndustrielle, Long> {
}
