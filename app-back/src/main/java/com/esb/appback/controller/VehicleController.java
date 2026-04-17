package com.esb.appback.controller;

import com.esb.appback.model.*;
import com.esb.appback.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // Vehicle Endpoints
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.createVehicle(vehicle));
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicle(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok().build();
    }

    // Equipment Endpoints
    @PostMapping("/equipment")
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.ok(vehicleService.createEquipment(equipment));
    }

    @GetMapping("/equipment")
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        return ResponseEntity.ok(vehicleService.getAllEquipment());
    }

    @GetMapping("/equipment/{id}")
    public ResponseEntity<Equipment> getEquipment(@PathVariable Long id) {
        Optional<Equipment> equipment = vehicleService.getEquipment(id);
        return equipment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/equipment/type/{type}")
    public ResponseEntity<List<Equipment>> getEquipmentByType(@PathVariable String type) {
        return ResponseEntity.ok(vehicleService.getEquipmentByType(type));
    }

    @GetMapping("/equipment/service/{serviceId}")
    public ResponseEntity<List<Equipment>> getEquipmentByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(vehicleService.getEquipmentByService(serviceId));
    }

    @PutMapping("/equipment/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Long id, @RequestBody Equipment equipment) {
        equipment.setId(id);
        return ResponseEntity.ok(vehicleService.updateEquipment(equipment));
    }

    // Maintenance Endpoints
    @PostMapping("/maintenance/schedule/{articleId}/{type}/{cost}/{nextDate}")
    public ResponseEntity<Maintenance> scheduleMaintenance(@PathVariable Long articleId, @PathVariable String type, @PathVariable Double cost, @PathVariable String nextDate) {
        return ResponseEntity.ok(vehicleService.scheduleMaintenance(articleId, type, cost, LocalDateTime.parse(nextDate)));
    }

    @PostMapping("/maintenance/record/{articleId}/{type}/{description}/{cost}/{mileage}")
    public ResponseEntity<Maintenance> recordMaintenance(@PathVariable Long articleId, @PathVariable String type, @PathVariable String description, @PathVariable Double cost, @PathVariable Double mileage) {
        return ResponseEntity.ok(vehicleService.recordMaintenance(articleId, type, description, cost, mileage));
    }

    @GetMapping("/maintenance/history/{articleId}")
    public ResponseEntity<List<Maintenance>> getMaintenanceHistory(@PathVariable Long articleId) {
        return ResponseEntity.ok(vehicleService.getMaintenanceHistory(articleId));
    }

    @GetMapping("/maintenance/all")
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        return ResponseEntity.ok(vehicleService.getAllMaintenance());
    }

    @GetMapping("/maintenance/type/{type}")
    public ResponseEntity<List<Maintenance>> getMaintenanceByType(@PathVariable String type) {
        return ResponseEntity.ok(vehicleService.getMaintenanceByType(type));
    }

    // Service Endpoints
    @PostMapping("/services")
    public ResponseEntity<com.esb.appback.model.Service> createService(@RequestBody com.esb.appback.model.Service service) {
        return ResponseEntity.ok(vehicleService.createService(service));
    }

    @GetMapping("/services")
    public ResponseEntity<List<com.esb.appback.model.Service>> getAllServices() {
        return ResponseEntity.ok(vehicleService.getAllServices());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<com.esb.appback.model.Service> getService(@PathVariable Long id) {
        Optional<com.esb.appback.model.Service> service = vehicleService.getService(id);
        return service.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
