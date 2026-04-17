import api from './api';

const InvoiceService = {
  getAllInvoices: () => api.get('/invoices').then(res => res.data),

  getInvoice: (id) => api.get(`/invoices/${id}`).then(res => res.data),

  getInvoicesByStatus: (status) => api.get(`/invoices/status/${status}`).then(res => res.data),

  getInvoicesBySupplier: (supplierId) => api.get(`/invoices/supplier/${supplierId}`).then(res => res.data),

  createInvoice: (invoice) => {
    // Format date properly string to LocalDateTime expectation or just pass it 
    // Spring Boot can parse "2026-04-07" to LocalDateTime if formatted perfectly, but actually Invoice date is LocalDateTime.
    // If frontend sets date as 'yyyy-MM-dd', Spring might crash if it expects time. We should append time.
    const payload = { ...invoice };
    delete payload.file;
    delete payload.ocrConfidence;
    if (payload.date && payload.date.length === 10) {
      payload.date = payload.date + 'T00:00:00';
    }
    return api.post('/invoices', payload).then(res => res.data);
  },

  updateInvoice: (id, invoice) => {
    const payload = { ...invoice };
    delete payload.file;
    delete payload.ocrConfidence;
    if (payload.date && payload.date.length === 10) {
      payload.date = payload.date + 'T00:00:00';
    }
    return api.put(`/invoices/${id}`, payload).then(res => res.data);
  },

  extractDataFromInvoice: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/invoices/extract', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => res.data);
  },

  ingestOcrResult: (ocrData) => api.post('/invoices/ingest', ocrData).then(res => res.data),

  deleteInvoice: (id) => api.delete(`/invoices/${id}`),

  getInvoiceItems: (invoiceId) => api.get(`/invoices/${invoiceId}/items`).then(res => res.data),

  addInvoiceItem: (invoiceId, consumableId, description, quantity, unitPrice) => {
    const params = new URLSearchParams({ consumableId, description, quantity, unitPrice });
    return api.post(`/invoices/${invoiceId}/items`, params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
  }
};

export default InvoiceService;
