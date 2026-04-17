<template>
  <div class="ordinateurs-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Ordinateurs"
        singularTitle="Ordinateur"
        :items="ordinateurs"
        :headers="headers"
        :loading="loading"
        :hasCategoryFields="true"
        familyFilter="Ordinateurs"
        @add-item="addOrdinateur"
        @update-item="updateOrdinateur"
        @delete-item="deleteOrdinateur"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.marque"
                label="Marque"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-laptop"
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
                v-model="formData.typeOrdinateur"
                :items="['Fixe', 'Portable', 'Serveur', 'Workstation', 'Tablette']"
                label="Type d'ordinateur"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-monitor"
              ></v-select>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.systemeExploitation"
                label="Système d'exploitation"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-microsoft-windows"
                placeholder="Windows 11, Ubuntu 22.04..."
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="formData.processeur"
                label="Processeur"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-chip"
                placeholder="Intel Core i7-12700..."
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="formData.ram"
                label="RAM"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-memory"
                placeholder="16 Go DDR4..."
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="4">
              <v-text-field
                v-model="formData.stockage"
                label="Stockage"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-harddisk"
                placeholder="512 Go SSD..."
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
      ordinateurs: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Marque', key: 'marque' },
        { title: 'Modèle', key: 'modele' },
        { title: 'Type', key: 'typeOrdinateur' },
        { title: 'Processeur', key: 'processeur' },
        { title: 'RAM', key: 'ram' },
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
    this.fetchOrdinateurs()
  },
  methods: {
    async fetchOrdinateurs() {
      this.loading = true
      try {
        const response = await ArticleService.getAllOrdinateurs()
        this.ordinateurs = response.data || []
      } catch (error) {
        this.showNotification('Erreur lors du chargement des ordinateurs', 'error')
      } finally {
        this.loading = false
      }
    },
    async addOrdinateur(formData) {
      try {
        await ArticleService.createArticle({ ...formData, type: 'ORDINATEUR' })
        this.showNotification('Ordinateur ajouté avec succès', 'success')
        this.fetchOrdinateurs()
      } catch (error) {
        this.showNotification('Erreur lors de l\'ajout', 'error')
      }
    },
    async updateOrdinateur({ id, data }) {
      try {
        await ArticleService.updateArticle(id, { ...data, type: 'ORDINATEUR' })
        this.showNotification('Ordinateur mis à jour', 'success')
        this.fetchOrdinateurs()
      } catch (error) {
        this.showNotification('Erreur lors de la mise à jour', 'error')
      }
    },
    async deleteOrdinateur(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Ordinateur supprimé', 'success')
        this.fetchOrdinateurs()
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
.ordinateurs-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
