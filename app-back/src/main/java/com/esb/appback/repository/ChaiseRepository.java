package com.esb.appback.repository;

import com.esb.appback.model.Chaise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChaiseRepository extends JpaRepository<Chaise, Long> {
}
