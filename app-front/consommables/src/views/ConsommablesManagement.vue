<template>
  <div class="consommables-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Consommables"
        singularTitle="Consommable"
        :items="consommables"
        :headers="headers"
        :loading="loading"
        @add-item="addConsommable"
        @update-item="updateConsommable"
        @delete-item="deleteConsommable"
      />
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
  components: {
    ArticleTable
  },
  data() {
    return {
      consommables: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Description', key: 'description' },
        { title: 'Prix', key: 'prix' },
        { title: 'Quantité', key: 'quantite' },
        { title: 'Unité', key: 'unite' },
        { title: 'Famille', key: 'famille' },
        { title: 'Fournisseur', key: 'fournisseur' },
        { title: 'QR Code', key: 'qrCode' },
        { title: 'Image', key: 'image' },
        { title: 'Actions', key: 'actions', sortable: false }
      ]
    }
  },
  mounted() {
    this.fetchConsommables()
  },
  methods: {
    async fetchConsommables() {
      this.loading = true
      try {
        const response = await ArticleService.getAllConsommables()
        this.consommables = response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des consommables:', error)
        this.showNotification('Erreur lors du chargement des consommables', 'error')
      } finally {
        this.loading = false
      }
    },
    async addConsommable(formData) {
      try {
        const payload = {
          ...formData,
          type: 'CONSOMMABLE'
        }
        await ArticleService.createArticle(payload)
        this.showNotification('Consommable ajouté avec succès', 'success')
        this.fetchConsommables()
      } catch (error) {
        console.error('Erreur lors de l\'ajout:', error)
        this.showNotification('Erreur lors de l\'ajout du consommable', 'error')
      }
    },
    async updateConsommable({ id, data }) {
      try {
        const payload = {
          ...data,
          type: 'CONSOMMABLE'
        }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Consommable mis à jour avec succès', 'success')
        this.fetchConsommables()
      } catch (error) {
        console.error('Erreur lors de la mise à jour:', error)
        this.showNotification('Erreur lors de la mise à jour du consommable', 'error')
      }
    },
    async deleteConsommable(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Consommable supprimé avec succès', 'success')
        this.fetchConsommables()
      } catch (error) {
        console.error('Erreur lors de la suppression:', error)
        this.showNotification('Erreur lors de la suppression du consommable', 'error')
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
.consommables-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
