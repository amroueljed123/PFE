import api from './api';

const DemandePrelevementService = {
  getAllDemandes() {
    return api.get('/demandes');
  },
  
  getDemandesByUser(userId) {
    return api.get(`/demandes/user/${userId}`);
  },

  createDemande(payload) {
    return api.post('/demandes', payload);
  },

  updateStatut(id, statut) {
    return api.put(`/demandes/${id}/status`, { statut });
  }
};

export default DemandePrelevementService;
