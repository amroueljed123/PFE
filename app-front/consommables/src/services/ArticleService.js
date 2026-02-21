import api from './api'

const ArticleService = {
  // Generic Article operations
  getAllArticles() {
    return api.get('/articles')
  },

  getArticleById(id) {
    return api.get(`/articles/${id}`)
  },

  getArticleByCodeBarre(codeBarre) {
    return api.get(`/articles/code/${codeBarre}`)
  },

  getArticlesByType(type) {
    return api.get(`/articles/type/${type}`)
  },

  getArticlesByFamille(famille) {
    return api.get(`/articles/famille/${famille}`)
  },

  getArticlesByFournisseur(fournisseur) {
    return api.get(`/articles/fournisseur/${fournisseur}`)
  },

  getAllTypes() {
    return api.get('/articles/types/all')
  },

  getAllFamilles() {
    return api.get('/articles/familles/all')
  },

  createArticle(article) {
    return api.post('/articles', article)
  },

  updateArticle(id, article) {
    return api.put(`/articles/${id}`, article)
  },

  deleteArticle(id) {
    return api.delete(`/articles/${id}`)
  },

  // Category-specific operations
  getAllConsommables() {
    return api.get('/articles/consommables/all')
  },

  getAllCartouches() {
    return api.get('/articles/cartouches/all')
  },

  getCartouchesByColor(couleur) {
    return api.get(`/articles/cartouches/couleur/${couleur}`)
  },

  getAllMeubles() {
    return api.get('/articles/meubles/all')
  },

  getArticleStats() {
    return api.get('/articles/stats')
  },

  getMeublesByMaterial(materiau) {
    return api.get(`/articles/meubles/materiau/${materiau}`)
  },

  getAllBureaux() {
    return api.get('/articles/bureaux/all')
  },

  getAllChaises() {
    return api.get('/articles/chaises/all')
  },

  getAllEtageres() {
    return api.get('/articles/etageres/all')
  },

  getAllImprimantes() {
    return api.get('/articles/imprimantes/all')
  },

  getImprimantesByMarque(marque) {
    return api.get(`/articles/imprimantes/marque/${marque}`)
  }
}

export default ArticleService
