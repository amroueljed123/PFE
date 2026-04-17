<template>
  <div class="peripheriques-page">
    <v-container fluid>
      <ArticleTable
        title="Gestion des Périphériques"
        singularTitle="Périphérique"
        :items="peripheriques"
        :headers="headers"
        :loading="loading"
        :hasCategoryFields="true"
        familyFilter="Peripheriques"
        @add-item="addPeripherique"
        @update-item="updatePeripherique"
        @delete-item="deletePeripherique"
      >
        <template v-slot:categoryFields="{ formData }">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="formData.marque"
                label="Marque"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-keyboard"
                placeholder="Logitech, HP, Dell..."
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
                v-model="formData.typePeripherique"
                :items="['Clavier', 'Souris', 'Webcam', 'Scanner', 'USB Hub', 'Casque', 'Microphone', 'Disque externe', 'Clé USB', 'Lecteur de code-barres', 'Autre']"
                label="Type de périphérique"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-usb"
              ></v-select>
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="formData.interfaceConnexion"
                :items="['USB', 'USB-C', 'Bluetooth', 'Sans fil (2.4GHz)', 'PS/2', 'Jack 3.5mm', 'Thunderbolt']"
                label="Interface de connexion"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-connection"
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
      peripheriques: [],
      loading: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { title: 'Code Barre', key: 'codeBarre' },
        { title: 'Nom', key: 'nom' },
        { title: 'Marque', key: 'marque' },
        { title: 'Type', key: 'typePeripherique' },
        { title: 'Interface', key: 'interfaceConnexion' },
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
    this.fetchPeripheriques()
  },
  methods: {
    async fetchPeripheriques() {
      this.loading = true
      try {
        const response = await ArticleService.getAllPeripheriques()
        this.peripheriques = response.data || []
      } catch (error) {
        this.showNotification('Erreur lors du chargement des périphériques', 'error')
      } finally {
        this.loading = false
      }
    },
    async addPeripherique(formData) {
      try {
        await ArticleService.createArticle({ ...formData, type: 'PERIPHERIQUE' })
        this.showNotification('Périphérique ajouté avec succès', 'success')
        this.fetchPeripheriques()
      } catch (error) {
        this.showNotification('Erreur lors de l\'ajout', 'error')
      }
    },
    async updatePeripherique({ id, data }) {
      try {
        await ArticleService.updateArticle(id, { ...data, type: 'PERIPHERIQUE' })
        this.showNotification('Périphérique mis à jour', 'success')
        this.fetchPeripheriques()
      } catch (error) {
        this.showNotification('Erreur lors de la mise à jour', 'error')
      }
    },
    async deletePeripherique(id) {
      try {
        await ArticleService.deleteArticle(id)
        this.showNotification('Périphérique supprimé', 'success')
        this.fetchPeripheriques()
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
.peripheriques-page {
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f4ff 100%);
  min-height: 100vh;
  padding: 20px 0;
}
</style>
