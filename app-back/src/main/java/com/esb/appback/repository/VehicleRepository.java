package com.esb.appback.repository;

import com.esb.appback.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Vehicle findByRegistration(String registration);
    List<Vehicle> findByStatus(String status);
    List<Vehicle> findByServiceId(Long serviceId);
    List<Vehicle> findByBrand(String brand);
}
