import api from './api';

const SupplierService = {
  getAllSuppliers: () => api.get('/supply-chain/suppliers').then(res => res.data),
  getSupplier: (id) => api.get(`/supply-chain/suppliers/${id}`).then(res => res.data),
  createSupplier: (supplier) => api.post('/supply-chain/suppliers', supplier).then(res => res.data),
  updateSupplier: (supplier) => api.put(`/supply-chain/suppliers/${supplier.id}`, supplier).then(res => res.data),
  deleteSupplier: (id) => api.delete(`/supply-chain/suppliers/${id}`),
};

export default SupplierService;
