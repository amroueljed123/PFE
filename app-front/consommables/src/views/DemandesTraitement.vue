<template>
  <div class="demandes-traitement">
    <v-row class="mb-4">
      <v-col cols="12" class="d-flex align-center justify-space-between">
        <div>
          <h2 class="text-h5 font-weight-bold mb-1">Traitement Logistique des Demandes (Niveau 2)</h2>
          <p class="text-body-2 text-medium-emphasis">Ces demandes ont déjà été validées par le management. Approuvez-les pour acter la sortie de stock.</p>
        </div>
      </v-col>
    </v-row>

    <v-card elevation="0" class="border rounded-lg">
      <v-data-table
        :headers="headers"
        :items="validatedDemandes"
        :loading="loading"
        class="custom-table"
        hover
      >
        <template v-slot:item.user="{ item }">
          <div class="d-flex align-center py-2">
            <v-avatar color="green-darken-1" size="36" class="mr-3">
              <span class="text-white font-weight-bold">{{ (item.user?.name || 'U').charAt(0).toUpperCase() }}</span>
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.user?.name || 'Inconnu' }}</div>
              <div class="text-caption text-grey">{{ item.user?.email || 'N/A' }}</div>
            </div>
          </div>
        </template>

        <template v-slot:item.article="{ item }">
          <div class="d-flex align-center">
            <v-avatar size="32" class="mr-3 rounded" color="grey-lighten-3">
              <v-img v-if="item.article?.image" :src="item.article.image" cover></v-img>
              <v-icon v-else size="20" color="grey">mdi-package-variant-closed</v-icon>
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.article?.nom || 'Article inconnu' }}</div>
              <div class="text-caption text-grey">Dispo: {{ item.article?.quantite || 0 }} en stock</div>
            </div>
          </div>
        </template>

        <template v-slot:item.quantite="{ item }">
          <v-chip size="small" color="green" variant="tonal" class="font-weight-bold">
            {{ item.quantite }}
          </v-chip>
        </template>

        <template v-slot:item.dateDemande="{ item }">
          <div class="text-body-2">
            <v-icon size="small" color="grey" class="mr-1">mdi-calendar</v-icon>
            {{ formatDate(item.dateDemande) }}
          </div>
        </template>

        <template v-slot:item.actions="{ item }">
          <div class="d-flex align-center">
            <v-btn
              color="success"
              variant="flat"
              size="small"
              prepend-icon="mdi-dolly"
              class="mr-2 text-none"
              @click="confirmAction(item, 'Approuvée')"
              :loading="processingId === item.id"
              :disabled="item.article?.quantite < item.quantite"
            >
              Approuver (Retirer du stock)
            </v-btn>
            <v-btn
              color="error"
              variant="flat"
              size="small"
              prepend-icon="mdi-cancel"
              class="text-none"
              @click="confirmAction(item, 'Rejetée')"
              :loading="processingId === item.id && false"
            >
              Rejeter
            </v-btn>
          </div>
          <div v-if="item.article?.quantite < item.quantite" class="text-caption text-error mt-1">
            *Stock insuffisant
          </div>
        </template>

        <template v-slot:no-data>
          <div class="py-12 text-center">
            <v-icon size="64" color="grey-lighten-2" class="mb-4">mdi-package-check</v-icon>
            <h3 class="text-h6 text-grey-darken-1 mb-2">Aucune demande validée en attente</h3>
            <p class="text-body-2 text-grey">Le flux de traitement est à jour.</p>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn variant="text" @click="snackbar.show = false">Fermer</v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
import DemandePrelevementService from '@/services/DemandePrelevementService'

export default {
  name: 'DemandesTraitement',
  data() {
    return {
      demandes: [],
      loading: true,
      processingId: null,
      headers: [
        { title: 'Collaborateur', key: 'user', align: 'start' },
        { title: 'Article demandé', key: 'article' },
        { title: 'Qté demandée', key: 'quantite', align: 'center' },
        { title: 'Date', key: 'dateDemande' },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false }
      ],
      snackbar: {
        show: false,
        text: '',
        color: 'success'
      }
    }
  },
  computed: {
    validatedDemandes() {
      // Pour le gestionnaire, on n'affiche que celles validées par le niveau 1
      return this.demandes.filter(d => d.statut === 'Validée')
    }
  },
  mounted() {
    this.fetchDemandes()
  },
  methods: {
    async fetchDemandes() {
      this.loading = true
      try {
        const response = await DemandePrelevementService.getAllDemandes()
        // Sort by date newest first
        this.demandes = response.data.sort((a, b) => new Date(b.dateDemande) - new Date(a.dateDemande))
      } catch (error) {
        console.error('Error fetching demandes:', error)
        this.showSnackbar('Erreur lors du chargement des demandes.', 'error')
      } finally {
        this.loading = false
      }
    },
    async confirmAction(demande, nouveauStatut) {
      if (!confirm(`Êtes-vous sûr de vouloir marquer cette demande comme ${nouveauStatut} ? ${nouveauStatut === 'Approuvée' ? 'Le stock sera déduit.' : ''}`)) {
        return
      }
      
      this.processingId = demande.id
      try {
        await DemandePrelevementService.updateStatut(demande.id, nouveauStatut)
        this.showSnackbar(`La demande a été traitée avec succès.`, 'success')
        this.fetchDemandes()
      } catch (error) {
        console.error('Error updating status:', error)
        const errMsg = error.response?.data?.message || 'Erreur lors de la mise à jour.'
        this.showSnackbar(errMsg, 'error')
      } finally {
        this.processingId = null
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('fr-FR', {
        day: '2-digit', month: 'short', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      })
    },
    showSnackbar(text, color) {
      this.snackbar.text = text
      this.snackbar.color = color
      this.snackbar.show = true
    }
  }
}
</script>

<style scoped>
.custom-table {
  background: white !important;
}
</style>
