import api from './api';

class CategoryService {
  getAllCategories() {
    return api.get('/categories');
  }

  getRootCategories() {
    return api.get('/categories/roots');
  }

  getSubcategories(parentId) {
    return api.get(`/categories/${parentId}/subcategories`);
  }

  createCategory(category) {
    return api.post('/categories', category);
  }

  updateCategory(id, category) {
    return api.put(`/categories/${id}`, category);
  }

  deleteCategory(id) {
    return api.delete(`/categories/${id}`);
  }
}

export default new CategoryService();
