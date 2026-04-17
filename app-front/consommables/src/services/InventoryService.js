import api from './api';

const InventoryService = {
  // Stock Movements
  recordMovement: (movementData) =>
    api.post('/inventory/movements/create', movementData),
  recordStockMovement: (consumableId, type, quantity, userId) =>
    api.post(`/inventory/movements/record/${consumableId}/${type}/${quantity}/${userId}`),
  getStockMovementsByConsumable: (consumableId) =>
    api.get(`/inventory/movements/consumable/${consumableId}`).then(res => res.data),

  // Stock Alerts
  createStockAlert: (consumableId, type, level) =>
    api.post(`/inventory/alerts/create/${consumableId}/${type}/${level}`),
  getActiveStockAlerts: () => api.get('/inventory/alerts/active').then(res => res.data),
  getStockAlertsByConsumable: (consumableId) =>
    api.get(`/inventory/alerts/consumable/${consumableId}`).then(res => res.data),
  resolveStockAlert: (alertId) => api.post(`/inventory/alerts/${alertId}/resolve`),

  // Inventories
  createInventory: (createdBy) => api.post(`/inventory/inventories/create/${createdBy}`).then(res => res.data),
  getInventories: () => api.get('/inventory/inventories').then(res => res.data),
  getInventoryItems: (inventoryId) => api.get(`/inventory/inventories/${inventoryId}/items`).then(res => res.data),
  startInventory: (inventoryId) => api.post(`/inventory/inventories/${inventoryId}/start`),
  completeInventory: (inventoryId, validatedBy) =>
    api.post(`/inventory/inventories/${inventoryId}/complete/${validatedBy}`),
  addInventoryItem: (inventoryId, consumableId, theoretical, physical) =>
    api.post(`/inventory/inventories/${inventoryId}/items/${consumableId}/${theoretical}/${physical}`),

  // Categories
  createCategory: (name) => api.post(`/inventory/categories/create/${name}`),
  getAllCategories: () => api.get('/inventory/categories').then(res => res.data)
};

export default InventoryService;

