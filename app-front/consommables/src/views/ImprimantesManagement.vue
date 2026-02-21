<template>
  <div class="imprimantes-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Imprimantes"
        singularTitle="Imprimante"
        :items="imprimantes"
        :headers="headers"
        :loading="loading"
        @add-item="addImprimante"
        @update-item="updateImprimante"
        @delete-item="deleteImprimante"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.marque"
                label="Marque"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.typeImprimante"
                label="Type d'Imprimante"
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
      imprimantes: [],
      loading: false,
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Marque', key: 'marque' },
        { title: 'Type', key: 'typeImprimante' },
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
  mounted() { this.fetchImprimantes() },
  methods: {
    async fetchImprimantes() {
      this.loading = true
      try {
        const response = await ArticleService.getAllImprimantes()
        this.imprimantes = response.data || []
      } finally { this.loading = false }
    },
    async addImprimante(formData) {
      try {
        const payload = { ...formData, type: 'IMPRIMANTE' }
        await ArticleService.createArticle(payload)
        this.showNotification('Success', 'success')
        this.fetchImprimantes()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async updateImprimante({ id, data }) {
      try {
        const payload = { ...data, type: 'IMPRIMANTE' }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Success', 'success')
        this.fetchImprimantes()
      } catch (error) {
        this.showNotification('Error', 'error')
      }
    },
    async deleteImprimante(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Success', 'success')
        this.fetchImprimantes()
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
.imprimantes-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>


