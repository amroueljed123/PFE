<template>
  <div class="demande-prelevement">
    <v-card class="mb-6" elevation="0">
      <v-card-title class="d-flex justify-space-between align-center">
        <div>
          <h2 class="text-h5 mb-2">Demandes de Prélèvement</h2>
          <p class="text-subtitle-2 text-medium-emphasis">Gérez vos demandes d'articles</p>
        </div>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          Nouvelle Demande
        </v-btn>
      </v-card-title>
    </v-card>

    <v-card elevation="0">
      <v-data-table
        :headers="headers"
        :items="demandes"
        :loading="loading"
        class="elevation-0"
      >
        <template v-slot:item.article="{ item }">
          <div class="d-flex align-center">
            <v-avatar size="32" class="mr-3" color="grey-lighten-2">
              <v-img v-if="item.article.image" :src="item.article.image"></v-img>
              <v-icon v-else>mdi-package-variant-closed</v-icon>
            </v-avatar>
            <div>
              <div class="font-weight-medium">{{ item.article.nom }}</div>
              <div class="text-caption text-grey">{{ item.article.type }}</div>
            </div>
          </div>
        </template>
        
        <template v-slot:item.statut="{ item }">
          <v-chip :color="getStatusColor(item.statut)" dark small>
            {{ item.statut }}
          </v-chip>
        </template>

        <template v-slot:item.dateDemande="{ item }">
          {{ formatDate(item.dateDemande) }}
        </template>

        <template v-slot:no-data>
          <div class="py-10 text-center">
            <v-icon size="60" color="grey-lighten-2" class="mb-4">mdi-clipboard-text-off-outline</v-icon>
            <p class="text-subtitle-2 text-medium-emphasis">Aucune demande trouvée</p>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="showAddDialog" max-width="600px">
      <v-card>
        <v-card-title>Nouvelle Demande de Prélèvement</v-card-title>
        <v-card-text class="pa-6">
          <v-form ref="demandeForm" v-model="formValid">
            <v-autocomplete
              v-model="selectedArticleId"
              :items="articles"
              item-title="nom"
              item-value="id"
              label="Sélectionner un Article"
              variant="outlined"
              class="mb-4"
              :rules="[v => !!v || 'Veuillez sélectionner un article']"
              clearable
            >
              <template v-slot:item="{ props, item }">
                <v-list-item v-bind="props" :subtitle="'Type: ' + item.raw.type + ' | Stock: ' + item.raw.quantite">
                  <template v-slot:prepend>
                    <v-avatar size="32" class="mr-3" color="grey-lighten-2">
                      <v-img v-if="item.raw.image" :src="item.raw.image"></v-img>
                      <v-icon v-else>mdi-package</v-icon>
                    </v-avatar>
                  </template>
                </v-list-item>
              </template>
            </v-autocomplete>

            <v-text-field
              v-model.number="quantite"
              label="Quantité"
              type="number"
              min="1"
              variant="outlined"
              :rules="[
                v => !!v || 'La quantité est requise',
                v => v > 0 || 'La quantité doit être supérieure à 0',
                v => checkStock(v) || 'Quantité indisponible en stock'
              ]"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn text @click="closeDialog">Annuler</v-btn>
          <v-btn color="primary" @click="submitDemande" :loading="saving" :disabled="!formValid">Soumettre</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="showSnackbar" :color="snackbarColor" timeout="3000">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
import DemandePrelevementService from '@/services/DemandePrelevementService';
import ArticleService from '@/services/ArticleService';

export default {
  data() {
    return {
      demandes: [],
      articles: [],
      loading: true,
      showAddDialog: false,
      formValid: false,
      saving: false,
      selectedArticleId: null,
      quantite: 1,
      userId: null,
      showSnackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      headers: [
        { text: 'Article', value: 'article' },
        { text: 'Quantité', value: 'quantite' },
        { text: 'Statut', value: 'statut' },
        { text: 'Date de Demande', value: 'dateDemande' }
      ]
    };
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.id) {
      this.userId = user.id;
      this.loadDemandes();
      this.loadArticles();
    }
  },
  methods: {
    async loadDemandes() {
      this.loading = true;
      try {
        const response = await DemandePrelevementService.getDemandesByUser(this.userId);
        this.demandes = response.data.sort((a, b) => new Date(b.dateDemande) - new Date(a.dateDemande));
      } catch (error) {
        console.error('Error loading demandes:', error);
        this.notify('Erreur lors du chargement des demandes', 'error');
      } finally {
        this.loading = false;
      }
    },
    async loadArticles() {
      try {
        const response = await ArticleService.getAllArticles();
        // filter out articles with no stock
        this.articles = response.data.filter(a => a.quantite > 0);
      } catch (error) {
        console.error('Error loading articles:', error);
      }
    },
    checkStock(val) {
      if (!this.selectedArticleId) return true;
      const article = this.articles.find(a => a.id === this.selectedArticleId);
      if (article && val > article.quantite) return false;
      return true;
    },
    async submitDemande() {
      const { valid } = await this.$refs.demandeForm.validate();
      if (!valid) return;

      this.saving = true;
      try {
        await DemandePrelevementService.createDemande({
          userId: this.userId,
          articleId: this.selectedArticleId,
          quantite: this.quantite
        });
        this.notify('Demande envoyée avec succès', 'success');
        this.closeDialog();
        this.loadDemandes();
      } catch (error) {
        console.error('Error creating demande:', error);
        const msg = error.response && error.response.data && error.response.data.message 
          ? error.response.data.message 
          : 'Erreur lors de la création de la demande';
        this.notify(msg, 'error');
      } finally {
        this.saving = false;
      }
    },
    closeDialog() {
      this.showAddDialog = false;
      this.selectedArticleId = null;
      this.quantite = 1;
      if (this.$refs.demandeForm) {
        this.$refs.demandeForm.resetValidation();
      }
    },
    getStatusColor(status) {
      if (status === 'Approuvée') return 'green';
      if (status === 'Validée') return 'info';
      if (status === 'Rejetée') return 'red';
      return 'orange'; // En attente
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    },
    notify(msg, color) {
      this.snackbarMessage = msg;
      this.snackbarColor = color;
      this.showSnackbar = true;
    }
  }
}
</script>

<style scoped>
.demande-prelevement {
  width: 100%;
}
</style>
