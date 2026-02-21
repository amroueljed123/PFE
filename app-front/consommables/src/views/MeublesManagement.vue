<template>
  <div class="meubles-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Meubles"
        singularTitle="Meuble"
        :items="meubles"
        :headers="headers"
        :loading="loading"
        @add-item="addMeuble"
        @update-item="updateMeuble"
        @delete-item="deleteMeuble"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="4">
              <v-text-field
                v-model="formData.materiau"
                label="Matériau"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="formData.dimension"
                label="Dimension"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model.number="formData.poids"
                label="Poids (kg)"
                type="number"
                step="0.1"
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
  components: {
    ArticleTable
  },
  data() {
    return {
      meubles: [],
      loading: false,
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Matériau', key: 'materiau' },
        { title: 'Dimension', key: 'dimension' },
        { title: 'Poids', key: 'poids' },
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
    this.fetchMeubles()
  },
  methods: {
    async fetchMeubles() {
      this.loading = true
      try {
        const response = await ArticleService.getAllMeubles()
        this.meubles = response.data || []
      } catch (error) {
        console.error('Erreur:', error)
      } finally {
        this.loading = false
      }
    },
    async addMeuble(formData) {
      console.log('Adding meuble with data:', formData);
      try {
        const payload = { 
          ...formData, 
          type: 'MEUBLE' 
        }
        console.log('Payload for Meuble:', payload);
        await ArticleService.createArticle(payload)
        this.showNotification('Success', 'success')
        this.fetchMeubles()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async updateMeuble({ id, data }) {
      try {
        const payload = { ...data, type: 'MEUBLE' }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Success', 'success')
        this.fetchMeubles()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async deleteMeuble(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Success', 'success')
        this.fetchMeubles()
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
.meubles-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>


