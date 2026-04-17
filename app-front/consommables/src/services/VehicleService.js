import api from './api';

const VehicleService = {
  // Vehicles
  createVehicle: (vehicle) => api.post('/vehicles', vehicle),
  getAllVehicles: () => api.get('/vehicles').then(res => res.data),
  getVehicle: (id) => api.get(`/vehicles/${id}`).then(res => res.data),
  getVehiclesByStatus: (status) => api.get(`/vehicles/status/${status}`).then(res => res.data),
  updateVehicle: (vehicle) => api.put(`/vehicles/${vehicle.id}`, vehicle),
  deleteVehicle: (id) => api.delete(`/vehicles/${id}`),

  // Equipment
  createEquipment: (equipment) => api.post('/vehicles/equipment', equipment),
  getAllEquipment: () => api.get('/vehicles/equipment').then(res => res.data),
  getEquipment: (id) => api.get(`/vehicles/equipment/${id}`).then(res => res.data),
  getEquipmentByType: (type) => api.get(`/vehicles/equipment/type/${type}`).then(res => res.data),
  getEquipmentByService: (serviceId) => api.get(`/vehicles/equipment/service/${serviceId}`).then(res => res.data),
  updateEquipment: (equipment) => api.put(`/vehicles/equipment/${equipment.id}`, equipment),

  // Maintenance
  scheduleMaintenance: (articleId, type, cost, nextDate) => 
    api.post(`/vehicles/maintenance/schedule/${articleId}/${type}/${cost}/${nextDate}`),
  recordMaintenance: (articleId, type, description, cost, mileage) => 
    api.post(`/vehicles/maintenance/record/${articleId}/${type}/${description}/${cost}/${mileage}`),
  getMaintenanceHistory: (articleId) => api.get(`/vehicles/maintenance/history/${articleId}`).then(res => res.data),
  getAllMaintenance: () => api.get('/vehicles/maintenance/all').then(res => res.data),
  getMaintenanceByType: (type) => api.get(`/vehicles/maintenance/type/${type}`).then(res => res.data),

  // Services (Departments)
  createService: (service) => api.post('/vehicles/services', service),
  getAllServices: () => api.get('/vehicles/services').then(res => res.data),
  getService: (id) => api.get(`/vehicles/services/${id}`).then(res => res.data)
};

export default VehicleService;
