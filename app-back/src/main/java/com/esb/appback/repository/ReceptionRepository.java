package com.esb.appback.repository;

import com.esb.appback.model.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReceptionRepository extends JpaRepository<Reception, Long> {
    List<Reception> findByOrderId(Long orderId);
    List<Reception> findByReceivedBy(Long receivedBy);
}
