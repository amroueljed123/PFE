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

  getArticleStats() {
    return api.get('/articles/stats')
  },

  // Consommables
  getAllConsommables() {
    return api.get('/articles/consommables/all')
  },

  // Cartouches
  getAllCartouches() {
    return api.get('/articles/cartouches/all')
  },

  getCartouchesByColor(couleur) {
    return api.get(`/articles/cartouches/couleur/${couleur}`)
  },

  // Meubles
  getAllMeubles() {
    return api.get('/articles/meubles/all')
  },

  getMeublesByMaterial(materiau) {
    return api.get(`/articles/meubles/materiau/${materiau}`)
  },

  // Bureaux
  getAllBureaux() {
    return api.get('/articles/bureaux/all')
  },

  // Chaises
  getAllChaises() {
    return api.get('/articles/chaises/all')
  },

  // Etageres
  getAllEtageres() {
    return api.get('/articles/etageres/all')
  },

  // Imprimantes
  getAllImprimantes() {
    return api.get('/articles/imprimantes/all')
  },

  getImprimantesByMarque(marque) {
    return api.get(`/articles/imprimantes/marque/${marque}`)
  },

  // Ordinateurs
  getAllOrdinateurs() {
    return api.get('/articles/ordinateurs/all')
  },

  getOrdinateursByMarque(marque) {
    return api.get(`/articles/ordinateurs/marque/${marque}`)
  },

  getOrdinateursByType(typeOrdinateur) {
    return api.get(`/articles/ordinateurs/type/${typeOrdinateur}`)
  },

  // Moniteurs
  getAllMoniteurs() {
    return api.get('/articles/moniteurs/all')
  },

  getMoniteursByMarque(marque) {
    return api.get(`/articles/moniteurs/marque/${marque}`)
  },

  // Peripheriques
  getAllPeripheriques() {
    return api.get('/articles/peripheriques/all')
  },

  getPeripheriquesByType(typePeripherique) {
    return api.get(`/articles/peripheriques/type/${typePeripherique}`)
  },

  // Telephones
  getAllTelephones() {
    return api.get('/articles/telephones/all')
  },

  getTelephonesByMarque(marque) {
    return api.get(`/articles/telephones/marque/${marque}`)
  },

  getArticlesByCustomCategory(categoryName) {
    return api.get(`/articles/custom/${categoryName}`)
  }
}

export default ArticleService
