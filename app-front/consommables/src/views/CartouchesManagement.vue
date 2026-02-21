<template>
  <div class="cartouches-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Cartouches"
        singularTitle="Cartouche"
        :items="cartouches"
        :headers="headers"
        :loading="loading"
        @add-item="addCartouche"
        @update-item="updateCartouche"
        @delete-item="deleteCartouche"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row class="mb-4">
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.reference"
                label="Référence"
                variant="outlined"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.couleur"
                label="Couleur"
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
      cartouches: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Référence', key: 'reference' },
        { title: 'Couleur', key: 'couleur' },
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
    this.fetchCartouches()
  },
  methods: {
    async fetchCartouches() {
      this.loading = true
      try {
        const response = await ArticleService.getAllCartouches()
        this.cartouches = response.data || []
      } catch (error) {
        console.error('Erreur lors du chargement des cartouches:', error)
      } finally {
        this.loading = false
      }
    },
    async addCartouche(formData) {
      try {
        const payload = {
          ...formData,
          type: 'CARTOUCHE'
        }
        await ArticleService.createArticle(payload)
        this.showNotification('Cartouche ajoutée avec succès', 'success')
        this.fetchCartouches()
      } catch (error) {
        this.showNotification('Erreur lors de l\'ajout', 'error')
      }
    },
    async updateCartouche({ id, data }) {
      try {
        const payload = {
          ...data,
          type: 'CARTOUCHE'
        }
        await ArticleService.updateArticle(id, payload)
        this.showNotification('Cartouche mise à jour', 'success')
        this.fetchCartouches()
      } catch (error) {
        this.showNotification('Erreur lors de la mise à jour', 'error')
      }
    },
    async deleteCartouche(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Cartouche supprimée', 'success')
        this.fetchCartouches()
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
.cartouches-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
