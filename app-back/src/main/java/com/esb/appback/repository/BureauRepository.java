package com.esb.appback.repository;

import com.esb.appback.model.Bureau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BureauRepository extends JpaRepository<Bureau, Long> {
}
