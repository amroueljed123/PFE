<template>
  <div class="moniteurs-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Moniteurs"
        singularTitle="Moniteur"
        :items="moniteurs"
        :headers="headers"
        :loading="loading"
        :hasCategoryFields="true"
        familyFilter="Moniteurs"
        @add-item="addMoniteur"
        @update-item="updateMoniteur"
        @delete-item="deleteMoniteur"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.marque"
                label="Marque"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-monitor"
                placeholder="Samsung, Dell, LG..."
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.taille"
                label="Taille (pouces)"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-ruler"
                placeholder="24, 27, 32..."
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.resolution"
                label="Résolution"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-television"
                placeholder="1920x1080, 2560x1440..."
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.typeEcran"
                :items="['LCD', 'LED', 'IPS', 'OLED', 'TN', 'VA']"
                label="Type d'écran"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-monitor-eye"
              ></v-select>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.typeConnecteur"
                :items="['HDMI', 'DisplayPort', 'VGA', 'DVI', 'USB-C', 'HDMI + DisplayPort']"
                label="Connecteur(s)"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-cable-data"
              ></v-select>
            </v-col>
          </v-row>
        </template>
      </ArticleTable>
    </v-container>
    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" location="bottom right">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
import ArticleTable from '@/components/ArticleTable.vue'
import ArticleService from '@/services/ArticleService'

export default {
  components: { ArticleTable },
  data() {
    return {
      moniteurs: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Marque', key: 'marque' },
        { title: 'Taille', key: 'taille' },
        { title: 'Résolution', key: 'resolution' },
        { title: 'Type écran', key: 'typeEcran' },
        { title: 'Statut', key: 'statut' },
        { title: 'Lieu', key: 'lieu' },
        { title: 'Utilisateur', key: 'utilisateur' },
        { title: 'Prix', key: 'prix' },
        { title: 'Quantité', key: 'quantite' },
        { title: 'QR Code', key: 'qrCode' },
        { title: 'Image', key: 'image' },
        { title: 'Actions', key: 'actions', sortable: false }
      ]
    }
  },
  mounted() {
    this.fetchMoniteurs()
  },
  methods: {
    async fetchMoniteurs() {
      this.loading = true
      try {
        const response = await ArticleService.getAllMoniteurs()
        this.moniteurs = response.data || []
      } catch (error) {
        this.showNotification('Erreur lors du chargement des moniteurs', 'error')
      } finally {
        this.loading = false
      }
    },
    async addMoniteur(formData) {
      try {
        await ArticleService.createArticle({ ...formData, type: 'MONITEUR' })
        this.showNotification('Moniteur ajouté avec succès', 'success')
        this.fetchMoniteurs()
      } catch (error) {
        this.showNotification('Erreur lors de l\'ajout', 'error')
      }
    },
    async updateMoniteur({ id, data }) {
      try {
        await ArticleService.updateArticle(id, { ...data, type: 'MONITEUR' })
        this.showNotification('Moniteur mis à jour', 'success')
        this.fetchMoniteurs()
      } catch (error) {
        this.showNotification('Erreur lors de la mise à jour', 'error')
      }
    },
    async deleteMoniteur(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Moniteur supprimé', 'success')
        this.fetchMoniteurs()
      } catch (error) {
        this.showNotification('Erreur lors de la suppression', 'error')
      }
    },
    showNotification(message, color = 'success') {
      this.snackbarMessage = message
      this.snackbarColor = color
      this.snackbar = true
    }
  }
}
</script>

<style scoped>
.moniteurs-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
