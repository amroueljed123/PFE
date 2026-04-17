package com.esb.appback.repository;

import com.esb.appback.model.Telephone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelephoneRepository extends JpaRepository<Telephone, Long> {
    List<Telephone> findByMarque(String marque);
    List<Telephone> findByTypeTelephone(String typeTelephone);
}
