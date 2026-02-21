<template>
  <div class="chaises-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Chaises"
        singularTitle="Chaise"
        :items="chaises"
        :headers="headers"
        :loading="loading"
        @add-item="addChaise"
        @update-item="updateChaise"
        @delete-item="deleteChaise"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.typeDeDossier"
                label="Type de Dossier"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.materiau"
                label="Matériau"
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
      chaises: [],
      loading: false,
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Dossier', key: 'typeDeDossier' },
        { title: 'Matériau', key: 'materiau' },
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
  mounted() { this.fetchChaises() },
  methods: {
    async fetchChaises() {
      this.loading = true
      try {
        const response = await ArticleService.getAllChaises()
        this.chaises = response.data || []
      } finally { this.loading = false }
    },
    async addChaise(formData) {
      try {
        const payload = { ...formData, type: 'CHAISE' }
        await ArticleService.createArticle(payload)
        this.showNotification('Success', 'success')
        this.fetchChaises()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async updateChaise({ id, data }) {
      try {
        const payload = { ...data, type: 'CHAISE' }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Success', 'success')
        this.fetchChaises()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async deleteChaise(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Success', 'success')
        this.fetchChaises()
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
.chaises-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>


