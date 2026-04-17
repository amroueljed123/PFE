<template>
  <div class="telephones-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Téléphones"
        singularTitle="Téléphone"
        :items="telephones"
        :headers="headers"
        :loading="loading"
        :hasCategoryFields="true"
        familyFilter="Telephones"
        @add-item="addTelephone"
        @update-item="updateTelephone"
        @delete-item="deleteTelephone"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.marque"
                label="Marque"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-phone"
                placeholder="Cisco, Yealink, Samsung..."
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.modele"
                label="Modèle"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-card-text"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.typeTelephone"
                :items="['Fixe', 'Mobile', 'IP / VoIP', 'DECT', 'Conférence']"
                label="Type de téléphone"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-phone-settings"
              ></v-select>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.numeroTelephone"
                label="Numéro de téléphone"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-dialpad"
                placeholder="0XX XX XX XX"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.systemeExploitation"
                label="Système d'exploitation (mobile)"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-android"
                placeholder="Android 13, iOS 17..."
              ></v-text-field>
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
      telephones: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Marque', key: 'marque' },
        { title: 'Modèle', key: 'modele' },
        { title: 'Type', key: 'typeTelephone' },
        { title: 'Numéro', key: 'numeroTelephone' },
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
    this.fetchTelephones()
  },
  methods: {
    async fetchTelephones() {
      this.loading = true
      try {
        const response = await ArticleService.getAllTelephones()
        this.telephones = response.data || []
      } catch (error) {
        this.showNotification('Erreur lors du chargement des téléphones', 'error')
      } finally {
        this.loading = false
      }
    },
    async addTelephone(formData) {
      try {
        await ArticleService.createArticle({ ...formData, type: 'TELEPHONE' })
        this.showNotification('Téléphone ajouté avec succès', 'success')
        this.fetchTelephones()
      } catch (error) {
        this.showNotification('Erreur lors de l\'ajout', 'error')
      }
    },
    async updateTelephone({ id, data }) {
      try {
        await ArticleService.updateArticle(id, { ...data, type: 'TELEPHONE' })
        this.showNotification('Téléphone mis à jour', 'success')
        this.fetchTelephones()
      } catch (error) {
        this.showNotification('Erreur lors de la mise à jour', 'error')
      }
    },
    async deleteTelephone(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Téléphone supprimé', 'success')
        this.fetchTelephones()
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
.telephones-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
