package com.esb.appback.service;

import com.esb.appback.model.*;
import com.esb.appback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private MaintenanceRepository maintenanceRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    // Vehicle Methods
    public Vehicle createVehicle(Vehicle vehicle) {
        vehicle.setCreatedAt(LocalDateTime.now());
        if (vehicle.getType() == null || vehicle.getType().isBlank()) {
            vehicle.setType("Voiture");
        }
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> getVehicle(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getVehiclesByStatus(String status) {
        return vehicleRepository.findByStatus(status);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        vehicleRepository.findById(vehicle.getId()).ifPresent(existing ->
            vehicle.setCreatedAt(existing.getCreatedAt())
        );
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    // Equipment Methods
    public Equipment createEquipment(Equipment equipment) {
        equipment.setCreatedAt(LocalDateTime.now());
        return equipmentRepository.save(equipment);
    }

    public Optional<Equipment> getEquipment(Long id) {
        return equipmentRepository.findById(id);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getEquipmentByType(String type) {
        return equipmentRepository.findByType(type);
    }

    public List<Equipment> getEquipmentByService(Long serviceId) {
        return equipmentRepository.findByServiceId(serviceId);
    }

    public Equipment updateEquipment(Equipment equipment) {
        equipmentRepository.findById(equipment.getId()).ifPresent(existing ->
            equipment.setCreatedAt(existing.getCreatedAt())
        );
        return equipmentRepository.save(equipment);
    }

    // Maintenance Methods
    public Maintenance scheduleMaintenance(Long articleId, String type, Double cost, LocalDateTime nextDate) {
        Maintenance maint = new Maintenance();
        maint.setArticleId(articleId);
        maint.setDate(LocalDateTime.now());
        maint.setType(type);
        maint.setCost(cost);
        maint.setNextDate(nextDate);
        return maintenanceRepository.save(maint);
    }

    public Maintenance recordMaintenance(Long articleId, String type, String description, Double cost, Double mileage) {
        Maintenance maint = new Maintenance();
        maint.setArticleId(articleId);
        maint.setDate(LocalDateTime.now());
        maint.setType(type);
        maint.setDescription(description);
        maint.setCost(cost);
        maint.setMileage(mileage);
        return maintenanceRepository.save(maint);
    }

    public List<Maintenance> getMaintenanceHistory(Long articleId) {
        return maintenanceRepository.findByArticleId(articleId);
    }

    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getMaintenanceByType(String type) {
        return maintenanceRepository.findByType(type);
    }

    // Service Methods (Departments)
    public com.esb.appback.model.Service createService(com.esb.appback.model.Service service) {
        return serviceRepository.save(service);
    }

    public Optional<com.esb.appback.model.Service> getService(Long id) {
        return serviceRepository.findById(id);
    }

    public List<com.esb.appback.model.Service> getAllServices() {
        return serviceRepository.findAll();
    }
}
