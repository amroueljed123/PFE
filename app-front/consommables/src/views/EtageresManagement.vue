<template>
  <div class="etageres-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Étagères Industrielles"
        singularTitle="Étagère"
        :items="etageres"
        :headers="headers"
        :loading="loading"
        @add-item="addEtagere"
        @update-item="updateEtagere"
        @delete-item="deleteEtagere"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="formData.chargeMaximale"
                label="Charge Maximale (kg)"
                type="number"
                step="0.1"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="formData.nombreEtageres"
                label="Nombre d'Étagères"
                type="number"
                variant="outlined"
              ></v-text-field>
            </v-col>
          </v-row>
        </template>
      </ArticleTable>
        </v-container>
    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000">
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
      etageres: [],
      loading: false,
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Charge Max', key: 'chargeMaximale' },
        { title: 'Nombre Étagères', key: 'nombreEtageres' },
        { title: 'Prix', key: 'prix' },
        { title: 'Quantité', key: 'quantite' },
        { title: 'Famille', key: 'famille' },
        { title: 'Fournisseur', key: 'fournisseur' },
        { title: 'QR Code', key: 'qrCode' },
        { title: 'Image', key: 'image' },
        { title: 'Actions', key: 'actions', sortable: false }
      ]
    }
  },
  mounted() { this.fetchEtageres() },
  methods: {
    async fetchEtageres() {
      this.loading = true
      try {
        const response = await ArticleService.getAllEtageres()
        this.etageres = response.data || []
      } finally { this.loading = false }
    },
    async addEtagere(formData) {
      try {
        const payload = { ...formData, type: 'ETAGERE' }
        await ArticleService.createArticle(payload)
        this.showNotification('Success', 'success')
        this.fetchEtageres()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async updateEtagere({ id, data }) {
      try {
        const payload = { ...data, type: 'ETAGERE' }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Success', 'success')
        this.fetchEtageres()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async deleteEtagere(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Success', 'success')
        this.fetchEtageres()
      } catch (error) {
        this.showNotification('Error', 'error')
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
.etageres-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>


