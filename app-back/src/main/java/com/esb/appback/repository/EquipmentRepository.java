package com.esb.appback.repository;

import com.esb.appback.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByType(String type);
    List<Equipment> findByServiceId(Long serviceId);
    List<Equipment> findByStatus(String status);
}
