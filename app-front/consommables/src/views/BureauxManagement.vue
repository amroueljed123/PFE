<template>
  <div class="bureaux-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Bureaux"
        singularTitle="Bureau"
        :items="bureaux"
        :headers="headers"
        :loading="loading"
        @add-item="addBureau"
        @update-item="updateBureau"
        @delete-item="deleteBureau"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="6">
              <v-text-field
                v-model.number="formData.tiroirs"
                label="Nombre de Tiroirs"
                type="number"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.typeDeSurface"
                label="Type de Surface"
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
      bureaux: [],
      loading: false,
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Tiroirs', key: 'tiroirs' },
        { title: 'Surface', key: 'typeDeSurface' },
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
  mounted() { this.fetchBureaux() },
  methods: {
    async fetchBureaux() {
      this.loading = true
      try {
        const response = await ArticleService.getAllBureaux()
        this.bureaux = response.data || []
      } finally { this.loading = false }
    },
    async addBureau(formData) {
      try {
        const payload = { ...formData, type: 'BUREAU' }
        await ArticleService.createArticle(payload)
        this.showNotification('Success', 'success')
        this.fetchBureaux()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async updateBureau({ id, data }) {
      try {
        const payload = { ...data, type: 'BUREAU' }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Success', 'success')
        this.fetchBureaux()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async deleteBureau(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Success', 'success')
        this.fetchBureaux()
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
.bureaux-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>


