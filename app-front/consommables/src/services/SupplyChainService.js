import api from './api';

const SupplyChainService = {
  // Suppliers
  createSupplier: (supplier) => api.post('/supply-chain/suppliers', supplier),
  getAllSuppliers: () => api.get('/supply-chain/suppliers').then(res => res.data),
  getSupplier: (id) => api.get(`/supply-chain/suppliers/${id}`).then(res => res.data),
  updateSupplier: (supplier) => api.put(`/supply-chain/suppliers/${supplier.id}`, supplier),
  deleteSupplier: (id) => api.delete(`/supply-chain/suppliers/${id}`),

  // Purchase Orders
  createPurchaseOrder: (order) => api.post('/supply-chain/orders', order),
  getAllPurchaseOrders: () => api.get('/supply-chain/orders').then(res => res.data),
  getPurchaseOrder: (id) => api.get(`/supply-chain/orders/${id}`).then(res => res.data),
  getPurchaseOrdersByStatus: (status) => api.get(`/supply-chain/orders/status/${status}`).then(res => res.data),
  getPurchaseOrdersBySupplier: (supplierId) => api.get(`/supply-chain/orders/supplier/${supplierId}`).then(res => res.data),
  updatePurchaseOrder: (order) => api.put(`/supply-chain/orders/${order.id}`, order),

  // Order Items
  addOrderItem: (item) => api.post('/supply-chain/order-items', item),
  getOrderItems: (orderId) => api.get(`/supply-chain/order-items/order/${orderId}`).then(res => res.data),

  // Validations
  submitForValidation: (orderId, level, validatorId) => 
    api.post(`/supply-chain/validations/submit/${orderId}/${level}/${validatorId}`),
  approveOrder: (validationId, comments) => 
    api.post(`/supply-chain/validations/${validationId}/approve?comments=${comments}`),
  getPendingValidations: () => api.get('/supply-chain/validations/pending').then(res => res.data),

  // Receptions
  recordReception: (orderId, receivedBy, notes) => 
    api.post(`/supply-chain/receptions/record/${orderId}/${receivedBy}?notes=${notes}`),
  addReceptionItem: (receptionId, orderItemId, quantityReceived) => 
    api.post(`/supply-chain/receptions/${receptionId}/items/${orderItemId}/${quantityReceived}`)
};

export default SupplyChainService;
