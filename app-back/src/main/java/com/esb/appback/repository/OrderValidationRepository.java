package com.esb.appback.repository;

import com.esb.appback.model.OrderValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderValidationRepository extends JpaRepository<OrderValidation, Long> {
    List<OrderValidation> findByOrderId(Long orderId);
    List<OrderValidation> findByValidatorId(Long validatorId);
    List<OrderValidation> findByStatus(String status);
    List<OrderValidation> findByLevel(Integer level);
}
