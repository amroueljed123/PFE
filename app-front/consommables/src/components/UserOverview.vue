<template>
  <div class="user-overview">
    <h2 class="text-h4 mb-6">Bienvenue, {{ userName }}</h2>
    
    <v-row>
      <v-col cols="12" md="4">
        <v-card elevation="2" class="pa-4 text-center">
          <v-icon size="48" color="primary" class="mb-2">mdi-history</v-icon>
          <div class="text-h5">{{ totalDemandes }}</div>
          <div class="text-subtitle-1 text-grey">Demandes Totales</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card elevation="2" class="pa-4 text-center">
          <v-icon size="48" color="orange" class="mb-2">mdi-clock-outline</v-icon>
          <div class="text-h5">{{ pendingDemandes }}</div>
          <div class="text-subtitle-1 text-grey">En Attente</div>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-card elevation="2" class="pa-4 text-center" color="primary" dark @click="$router.push('/user/demandes')" style="cursor: pointer;">
          <v-icon size="48" color="white" class="mb-2">mdi-plus-circle</v-icon>
          <div class="text-h5 text-white">Nouvelle Demande</div>
          <div class="text-subtitle-1 text-white">Créer un prélèvement</div>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-6">
      <v-col cols="12">
        <v-card>
          <v-card-title>Mes Dernières Demandes</v-card-title>
          <v-data-table
            :headers="headers"
            :items="demandes"
            :loading="loading"
            class="elevation-0"
            hide-default-footer
          >
            <template v-slot:item.statut="{ item }">
              <v-chip :color="getStatusColor(item.statut)" dark small>
                {{ item.statut }}
              </v-chip>
            </template>
            <template v-slot:item.dateDemande="{ item }">
              {{ formatDate(item.dateDemande) }}
            </template>
            <template v-slot:no-data>
              <div class="pa-4 text-center text-grey">Aucune demande récente.</div>
            </template>
          </v-data-table>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import DemandePrelevementService from '@/services/DemandePrelevementService';

export default {
  data() {
    return {
      userName: '',
      userId: null,
      demandes: [],
      loading: true,
      headers: [
        { text: 'Article', value: 'article.nom' },
        { text: 'Quantité', value: 'quantite' },
        { text: 'Statut', value: 'statut' },
        { text: 'Date', value: 'dateDemande' }
      ]
    };
  },
  computed: {
    totalDemandes() {
      return this.demandes.length;
    },
    pendingDemandes() {
      return this.demandes.filter(d => d.statut === 'En attente').length;
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.id) {
      this.userName = user.name;
      this.userId = user.id;
      this.fetchDemandes();
    }
  },
  methods: {
    async fetchDemandes() {
      try {
        const response = await DemandePrelevementService.getDemandesByUser(this.userId);
        // sort by most recent 
        this.demandes = response.data.sort((a, b) => new Date(b.dateDemande) - new Date(a.dateDemande)).slice(0, 5);
      } catch (error) {
        console.error("Error fetching demandes:", error);
      } finally {
        this.loading = false;
      }
    },
    getStatusColor(status) {
      if (status === 'Approuvée') return 'green';
      if (status === 'Rejetée') return 'red';
      return 'orange'; // En attente
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    }
  }
}
</script>

<style scoped>
.user-overview {
  width: 100%;
}
</style>
