<template>
  <div class="mouvements-dashboard">
    <v-container fluid class="pa-8">
      
      <!-- ═══ PREMIUM HEADER ═══ -->
      <div class="d-flex justify-space-between align-end mb-10 flex-wrap gap-6">
        <div class="header-content">
          <div class="d-flex align-center mb-2">
            <v-avatar color="primary" size="48" class="mr-4 elevation-3">
              <v-icon color="white" size="28">mdi-swap-horizontal</v-icon>
            </v-avatar>
            <div>
              <h1 class="text-h3 font-weight-black tracking-tight text-blue-grey-darken-4">Flux de Stock</h1>
              <div class="d-flex align-center mt-1">
                <v-badge dot color="success" class="mr-2"></v-badge>
                <span class="text-caption font-weight-bold text-grey-darken-1 text-uppercase letter-spacing-1">Monitoring en temps réel</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="header-actions d-flex gap-3">
          <v-btn
            variant="flat"
            color="white"
            class="text-none font-weight-bold shadow-sm border px-6"
            prepend-icon="mdi-printer-outline"
            rounded="lg"
            @click="exportData"
          >
            Imprimer Rapport
          </v-btn>
          <v-btn
            variant="elevated"
            color="primary"
            class="text-none font-weight-bold px-6"
            prepend-icon="mdi-plus"
            rounded="lg"
            elevation="4"
            @click="openNewMovementDialog"
          >
            Nouvelle Opération
          </v-btn>
        </div>
      </div>

      <!-- ═══ KPI DASHBOARD ═══ -->
      <v-row class="mb-10">
        <v-col cols="12" sm="6" md="3" v-for="(stat, i) in statsCards" :key="i">
          <v-card class="stat-card rounded-2xl overflow-hidden shadow-hover" border="0" elevation="0">
            <div class="stat-card-glow" :style="{ background: `radial-gradient(circle at top right, ${stat.accentColor}22, transparent)` }"></div>
            <v-card-text class="pa-6 position-relative">
              <div class="d-flex justify-space-between align-start mb-4">
                <div class="stat-icon-box" :style="{ backgroundColor: stat.accentColor + '15' }">
                  <v-icon :color="stat.accentColor" size="26">{{ stat.icon }}</v-icon>
                </div>
                <div class="text-right">
                  <span class="text-h4 font-weight-black" :style="{ color: stat.accentColor }">{{ stat.value }}</span>
                  <div class="text-caption font-weight-bold text-grey-lighten-1">{{ stat.title }}</div>
                </div>
              </div>
              <v-progress-linear :model-value="85" :color="stat.accentColor" height="6" rounded class="mt-2 opacity-20"></v-progress-linear>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- ═══ INTELLIGENT FILTERS ═══ -->
      <div class="filter-toolbar pa-2 bg-white rounded-2xl border shadow-sm mb-8 d-flex align-center flex-wrap gap-4">
        <v-text-field
          v-model="search"
          placeholder="Filtrer l'historique..."
          variant="solo"
          density="comfortable"
          flat
          hide-details
          prepend-inner-icon="mdi-magnify"
          class="search-input-premium no-border-field"
          rounded="xl"
          bg-color="grey-lighten-5"
        ></v-text-field>

        <v-spacer class="d-none d-md-block"></v-spacer>

        <v-btn-toggle v-model="filterType" mandatory variant="text" class="type-toggle-premium">
          <v-btn value="ALL" class="text-none">Tous</v-btn>
          <v-btn value="IN" class="text-none" color="success">Entrées</v-btn>
          <v-btn value="OUT" class="text-none" color="error">Sorties</v-btn>
        </v-btn-toggle>

        <v-divider vertical inset class="mx-2 d-none d-sm-block"></v-divider>
        <v-btn variant="flat" icon="mdi-refresh" color="blue-grey-lighten-5" class="rounded-lg" :loading="loading" @click="fetchMovements"></v-btn>
      </div>

      <!-- ═══ DATA TABLE ═══ -->
      <v-card class="rounded-2xl border-0 shadow-lg overflow-hidden" elevation="0">
        <v-data-table
          :headers="headers"
          :items="filteredMovements"
          :loading="loading"
          :search="search"
          :items-per-page="10"
          hover
          class="premium-table"
          @click:row="(_, { item }) => openMovementDetail(item)"
        >
          <template v-slot:item.reference="{ item }">
            <div class="d-flex align-center">
              <div class="ref-dot mr-3" :class="item.type === 'IN' ? 'bg-success' : 'bg-error'"></div>
              <span class="font-weight-black text-blue-grey-darken-4 font-mono">{{ item.reference || 'REF-AUTO' }}</span>
            </div>
          </template>
          <template v-slot:item.articleName="{ item }">
            <div class="d-flex align-center py-4">
              <div class="category-avatar mr-4" :style="{ backgroundColor: getCategoryColor(item.articleCategory) + '15' }">
                <v-icon :color="getCategoryColor(item.articleCategory)" size="20">{{ getCategoryIcon(item.articleCategory) }}</v-icon>
              </div>
              <div>
                <div class="font-weight-bold text-body-1 text-blue-grey-darken-4">{{ item.articleName }}</div>
                <div class="text-caption font-weight-bold text-grey">{{ item.articleCategory }}</div>
              </div>
            </div>
          </template>
          <template v-slot:item.fournisseur="{ item }">
             <div class="d-flex align-center">
               <div class="vendor-initial mr-2">{{ (item.fournisseur || 'I')[0] }}</div>
               <span class="text-body-2 font-weight-medium text-grey-darken-3">{{ item.fournisseur || 'Stock Interne' }}</span>
             </div>
          </template>
          <template v-slot:item.documentInfo="{ item }">
            <v-chip v-if="item.documentInfo" size="x-small" variant="flat" color="blue-grey-lighten-5" class="text-none font-weight-black px-3 rounded-pill" prepend-icon="mdi-file-document">
              {{ item.documentInfo }}
            </v-chip>
            <span v-else class="text-caption text-disabled italic">Aucun doc.</span>
          </template>
          <template v-slot:item.type="{ item }">
             <div class="type-pill" :class="item.type === 'IN' ? 'type-in' : 'type-out'">
               <v-icon size="14" class="mr-1">{{ item.type === 'IN' ? 'mdi-plus-circle' : 'mdi-minus-circle' }}</v-icon>
               {{ item.type === 'IN' ? 'ENTRÉE' : 'SORTIE' }}
             </div>
          </template>
          <template v-slot:item.quantity="{ item }">
             <div class="text-right pr-4">
               <span class="text-h6 font-weight-black" :class="item.type === 'IN' ? 'text-success' : 'text-error'">
                 {{ item.type === 'IN' ? '+' : '-' }}{{ item.quantity }}
               </span>
               <span class="text-caption text-grey ml-1 font-weight-bold">PCS</span>
             </div>
          </template>
          <template v-slot:item.cost="{ item }">
             <div class="text-right font-weight-black text-blue-grey-darken-3">{{ formatCurrency(item.cost) }}</div>
          </template>
          <template v-slot:item.date="{ item }">
            <div class="text-right">
              <div class="text-caption font-weight-bold text-blue-grey-darken-2">{{ formatDate(item.date) }}</div>
              <div class="text-caption text-grey">{{ formatTime(item.date) }}</div>
            </div>
          </template>
        </v-data-table>
      </v-card>

      <!-- ═══ NEW MOVEMENT DIALOG ═══ -->
      <v-dialog v-model="movementDialog" width="700" persistent transition="dialog-bottom-transition">
        <v-card rounded="xl">
          <v-card-title class="bg-primary text-white pa-6 d-flex align-center">
            <v-icon class="mr-3">mdi-swap-horizontal-bold</v-icon>
            <span class="text-h6 font-weight-bold">Nouvelle Opération de Stock</span>
            <v-spacer></v-spacer>
            <v-btn icon="mdi-close" variant="text" @click="movementDialog = false"></v-btn>
          </v-card-title>
          
          <v-card-text class="pa-8">
            <v-form ref="movementForm" v-model="validMovement">
              <!-- Type Selection -->
              <div class="text-subtitle-2 font-weight-bold mb-3">TYPE DE MOUVEMENT</div>
              <v-btn-toggle v-model="newMovement.type" mandatory color="primary" variant="flat" class="mb-6 w-100 bg-grey-lighten-4 pa-1 rounded-lg">
                <v-btn value="IN" class="flex-grow-1 text-none font-weight-bold" color="success">
                  <v-icon left class="mr-2">mdi-arrow-bottom-left-thick</v-icon>ENTRÉE (RÉCEPTION)
                </v-btn>
                <v-btn value="OUT" class="flex-grow-1 text-none font-weight-bold" color="error">
                  <v-icon left class="mr-2">mdi-arrow-top-right-thick</v-icon>SORTIE (PRÉLÈVEMENT)
                </v-btn>
              </v-btn-toggle>

              <v-row>
                <v-col cols="12">
                  <v-autocomplete
                    v-model="newMovement.articleId"
                    :items="articles"
                    item-title="nom"
                    item-value="id"
                    label="Sélectionner l'article *"
                    variant="outlined"
                    prepend-inner-icon="mdi-package-variant"
                    required
                    :rules="[v => !!v || 'L\'article est obligatoire']"
                  >
                    <template v-slot:item="{ props, item }">
                      <v-list-item v-bind="props" :subtitle="item.raw.famille" :prepend-avatar="item.raw.image"></v-list-item>
                    </template>
                  </v-autocomplete>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field v-model="newMovement.reference" label="Référence / N° Bon" variant="outlined" prepend-inner-icon="mdi-identifier"></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field v-model.number="newMovement.quantity" type="number" label="Quantité *" variant="outlined" prepend-inner-icon="mdi-counter" required :rules="[v => !!v && v > 0 || 'Quantité invalide']"></v-text-field>
                </v-col>
                
                <v-col cols="12" md="6">
                  <v-text-field v-model="newMovement.fournisseur" :label="newMovement.type === 'IN' ? 'Fournisseur' : 'Bénéficiaire / Lieu'" variant="outlined" prepend-inner-icon="mdi-warehouse"></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field v-model.number="newMovement.cost" type="number" label="Valeur Totale (DA)" variant="outlined" prepend-inner-icon="mdi-currency-usd"></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-text-field v-model="newMovement.documentInfo" label="Document (N° Facture, BL, etc.)" variant="outlined" prepend-inner-icon="mdi-file-document-outline"></v-text-field>
                </v-col>
                
                <v-col cols="12">
                  <v-textarea v-model="newMovement.description" label="Notes / Observations" variant="outlined" rows="2" prepend-inner-icon="mdi-text-box-outline"></v-textarea>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
          
          <v-card-actions class="pa-8 pt-0">
            <v-spacer></v-spacer>
            <v-btn variant="text" color="grey-darken-1" @click="movementDialog = false" class="px-6">Annuler</v-btn>
            <v-btn color="primary" variant="elevated" rounded="lg" class="px-10" :loading="saving" :disabled="!validMovement" @click="saveMovement">
              Enregistrer l'Opération
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- ═══ VOUCHER DETAIL DIALOG ═══ -->
      <v-dialog v-model="detailDialog" width="700" transition="scale-transition">
        <v-card v-if="selectedMovement" rounded="2xl" class="voucher-card overflow-hidden shadow-2xl">
          <div class="voucher-header pa-10 text-white position-relative" :class="selectedMovement.type === 'IN' ? 'bg-success' : 'bg-error'">
            <div class="voucher-header-pattern"></div>
            <v-btn icon="mdi-close" variant="text" color="white" class="position-absolute" style="top: 16px; right: 16px" @click="detailDialog = false"></v-btn>
            <div class="d-flex justify-space-between align-center relative-z">
              <div>
                <div class="text-overline font-weight-black opacity-70 mb-2 letter-spacing-2">BON DE MOUVEMENT STOCK</div>
                <h2 class="text-h4 font-weight-black mb-1">{{ selectedMovement.reference || 'MOUV-' + selectedMovement.id }}</h2>
                <div class="d-flex align-center">
                  <v-icon size="18" class="mr-2">mdi-calendar-clock</v-icon>
                  <span class="text-subtitle-1 font-weight-medium">{{ formatDateFull(selectedMovement.date) }}</span>
                </div>
              </div>
              <v-icon size="100" class="opacity-20">mdi-clipboard-text-clock</v-icon>
            </div>
          </div>
          <v-card-text class="pa-0 bg-white">
            <div class="pa-10">
              <v-row class="mb-10">
                <v-col cols="12" md="6">
                  <div class="text-caption font-weight-black text-grey-lighten-1 mb-4 text-uppercase">Détails de l'Article</div>
                  <div class="d-flex align-center">
                    <v-avatar size="64" class="mr-4 rounded-xl border-sm elevation-1" :image="selectedMovement.articleImage">
                      <v-icon v-if="!selectedMovement.articleImage" size="32" color="primary">mdi-package-variant</v-icon>
                    </v-avatar>
                    <div>
                      <div class="text-h6 font-weight-black text-blue-grey-darken-4">{{ selectedMovement.articleName }}</div>
                      <v-chip size="x-small" color="primary" variant="flat" class="font-weight-black">{{ selectedMovement.articleCategory }}</v-chip>
                    </div>
                  </div>
                </v-col>
                <v-col cols="12" md="6" class="text-md-right">
                  <div class="text-caption font-weight-black text-grey-lighten-1 mb-4 text-uppercase">Statut Transaction</div>
                  <v-chip :color="selectedMovement.type === 'IN' ? 'success' : 'error'" size="large" class="font-weight-black px-6 rounded-lg">
                    {{ selectedMovement.type === 'IN' ? 'RÉCEPTION VALIDÉE' : 'EXPÉDITION VALIDÉE' }}
                  </v-chip>
                </v-col>
              </v-row>
              <div class="movement-summary-grid pa-6 rounded-2xl bg-grey-lighten-5 mb-8">
                <v-row>
                  <v-col cols="4">
                    <div class="text-caption font-weight-bold text-grey mb-1">QUANTITÉ</div>
                    <div class="text-h5 font-weight-black" :class="selectedMovement.type === 'IN' ? 'text-success' : 'text-error'">
                      {{ selectedMovement.type === 'IN' ? '+' : '-' }} {{ selectedMovement.quantity }} unités
                    </div>
                  </v-col>
                  <v-col cols="4">
                    <div class="text-caption font-weight-bold text-grey mb-1">VALEUR UNITÉ</div>
                    <div class="text-h5 font-weight-black text-blue-grey-darken-3">{{ formatCurrency(selectedMovement.cost / selectedMovement.quantity) }}</div>
                  </v-col>
                  <v-col cols="4">
                    <div class="text-caption font-weight-bold text-grey mb-1">TOTAL HT</div>
                    <div class="text-h5 font-weight-black text-blue-grey-darken-3">{{ formatCurrency(selectedMovement.cost) }}</div>
                  </v-col>
                </v-row>
              </div>
              <v-row class="mb-8">
                <v-col cols="12" md="6">
                  <div class="info-item mb-6">
                    <div class="text-caption font-weight-black text-grey-darken-1 mb-1">PARTENAIRE / PROVENANCE</div>
                    <div class="text-body-1 font-weight-bold text-blue-grey-darken-4 d-flex align-center">
                      <v-icon size="20" color="primary" class="mr-2">mdi-office-building-marker</v-icon>
                      {{ selectedMovement.fournisseur || 'Interne - Direction Générale' }}
                    </div>
                  </div>
                  <div class="info-item">
                    <div class="text-caption font-weight-black text-grey-darken-1 mb-1">RESPONSABLE OPÉRATION</div>
                    <div class="text-body-1 font-weight-bold text-blue-grey-darken-4 d-flex align-center">
                      <v-icon size="20" color="primary" class="mr-2">mdi-account-check</v-icon>
                      {{ selectedMovement.userName || 'Administrateur Système' }}
                    </div>
                  </div>
                </v-col>
                <v-col cols="12" md="6">
                   <div class="info-item mb-6">
                    <div class="text-caption font-weight-black text-grey-darken-1 mb-1">PIÈCE JUSTIFICATIVE</div>
                    <div class="text-body-1 font-weight-bold text-blue-grey-darken-4 d-flex align-center">
                      <v-icon size="20" color="primary" class="mr-2">mdi-file-certificate</v-icon>
                      {{ selectedMovement.documentInfo || 'Certificat d\'inventaire' }}
                    </div>
                  </div>
                </v-col>
              </v-row>
              <v-divider class="mb-6"></v-divider>
              <div>
                <div class="text-caption font-weight-black text-grey-darken-1 mb-2 text-uppercase">Observations / Notes</div>
                <div class="description-text pa-4 rounded-xl border text-body-2 text-grey-darken-3">
                  {{ selectedMovement.description || 'Aucune observation particulière enregistrée.' }}
                </div>
              </div>
            </div>
          </v-card-text>
          <v-card-actions class="pa-8 bg-grey-lighten-5">
            <v-btn variant="flat" color="blue-grey-darken-4" rounded="lg" block size="large" class="text-none font-weight-bold" @click="detailDialog = false">Fermer</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" rounded="pill">
        {{ snackbarMessage }}
      </v-snackbar>

    </v-container>
  </div>
</template>

<script>
import AnalyticsService from '@/services/AnalyticsService';
import ArticleService from '@/services/ArticleService';
import InventoryService from '@/services/InventoryService';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';

export default {
  name: "MouvementsStock",
  data() {
    return {
      loading: true,
      saving: false,
      search: "",
      filterType: "ALL",
      movements: [],
      articles: [],
      detailDialog: false,
      movementDialog: false,
      validMovement: false,
      selectedMovement: null,
      snackbar: false,
      snackbarMessage: "",
      snackbarColor: "success",
      newMovement: {
        articleId: null,
        type: "IN",
        quantity: 1,
        reference: "",
        fournisseur: "",
        documentInfo: "",
        cost: 0,
        description: ""
      },
      headers: [
        { title: "RÉFÉRENCE", key: "reference", align: "start", sortable: true, width: "140px" },
        { title: "ARTICLE & CATÉGORIE", key: "articleName", align: "start", sortable: true },
        { title: "PARTENAIRE / PROVENANCE", key: "fournisseur", align: "start", sortable: true },
        { title: "DOCUMENT", key: "documentInfo", align: "start", sortable: true },
        { title: "QUANTITÉ", key: "quantity", align: "end", sortable: true, width: "120px" },
        { title: "VALEUR TOTALE", key: "cost", align: "end", sortable: true, width: "150px" },
        { title: "TYPE", key: "type", align: "start", sortable: true, width: "120px" },
        { title: "DATE & HEURE", key: "date", align: "end", sortable: true, width: "160px" },
      ]
    };
  },
  computed: {
    filteredMovements() {
      let filtered = this.movements;
      if (this.filterType !== "ALL") filtered = filtered.filter(m => m.type === this.filterType);
      if (this.search) {
        const q = this.search.toLowerCase();
        filtered = filtered.filter(m => 
          (m.articleName && m.articleName.toLowerCase().includes(q)) ||
          (m.reference && m.reference.toLowerCase().includes(q)) ||
          (m.articleCategory && m.articleCategory.toLowerCase().includes(q)) ||
          (m.fournisseur && m.fournisseur.toLowerCase().includes(q))
        );
      }
      return filtered;
    },
    statsCards() {
      return [
        { title: 'Transactions', value: this.movements.length, icon: 'mdi-chart-timeline-variant', accentColor: '#3949AB' },
        { title: 'Articles Reçus', value: this.movements.filter(m => m.type === 'IN').length, icon: 'mdi-plus-box-multiple', accentColor: '#43A047' },
        { title: 'Articles Sortis', value: this.movements.filter(m => m.type === 'OUT').length, icon: 'mdi-minus-box-multiple', accentColor: '#E53935' },
        { title: 'Investissement', value: this.formatCurrencyRaw(this.totalCost), icon: 'mdi-wallet-outline', accentColor: '#FB8C00' }
      ]
    },
    totalCost() {
      return this.movements.filter(m => m.type === 'IN' && m.cost).reduce((acc, curr) => acc + curr.cost, 0);
    }
  },
  methods: {
    async fetchMovements() {
      this.loading = true;
      try {
        const response = await AnalyticsService.getAllDetailedMovements();
        this.movements = response || [];
      } catch (error) {
        this.showSnackbar("Erreur lors de la récupération des mouvements", "error");
      } finally {
        this.loading = false;
      }
    },
    async fetchArticles() {
      try {
        const response = await ArticleService.getAllArticles();
        this.articles = response.data || [];
      } catch (e) { console.error(e); }
    },
    openMovementDetail(item) {
      this.selectedMovement = item;
      this.detailDialog = true;
    },
    openNewMovementDialog() {
      this.resetNewMovement();
      this.fetchArticles();
      this.movementDialog = true;
    },
    resetNewMovement() {
      this.newMovement = {
        articleId: null, type: "IN", quantity: 1, reference: "", 
        fournisseur: "", documentInfo: "", cost: 0, description: ""
      };
    },
    async saveMovement() {
      if (!this.$refs.movementForm.validate()) return;
      this.saving = true;
      try {
        await InventoryService.recordMovement(this.newMovement);
        this.showSnackbar("Opération enregistrée avec succès ! Stock mis à jour.");
        this.movementDialog = false;
        this.fetchMovements();
      } catch (error) {
        this.showSnackbar(error.response?.data?.message || "Erreur lors de l'enregistrement", "error");
      } finally {
        this.saving = false;
      }
    },
    showSnackbar(msg, color = "success") {
      this.snackbarMessage = msg;
      this.snackbarColor = color;
      this.snackbar = true;
    },
    formatDateFull(d) {
      if (!d) return "";
      return new Date(d).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    },
    formatDate(d) { return d ? new Date(d).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' }) : "" },
    formatTime(d) { return d ? new Date(d).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : "" },
    formatCurrencyRaw(v) { return new Intl.NumberFormat('fr-DZ', { minimumFractionDigits: 0 }).format(v || 0) },
    formatCurrency(v) { return this.formatCurrencyRaw(v) + " DA" },
    getCategoryColor(cat) {
      const map = { 'Consommables': '#4CAF50', 'Ordinateurs': '#1976D2', 'Mobilier': '#795548', 'Imprimantes': '#009688' };
      return map[cat] || '#607D8B';
    },
    getCategoryIcon(cat) {
      const map = { 'Ordinateurs': 'mdi-laptop', 'Imprimantes': 'mdi-printer', 'Consommables': 'mdi-bottle-wine' };
      return map[cat] || 'mdi-package-variant';
    },
    getStatutKey(s) { return s === 'En stock' ? 'stock' : s === 'IN' ? 'stock' : 'other' },
    getStockClass(i) { return i.quantity < 10 ? 'stock-danger' : 'stock-ok' },
    exportData() {
       const doc = new jsPDF('l', 'mm', 'a4');
       doc.text("Rapport d'activité Stock", 14, 15);
       autoTable(doc, { 
         startY: 20, 
         head: [['Réf', 'Article', 'Famille', 'Tier', 'Type', 'Qté', 'Valeur']],
         body: this.filteredMovements.map(m => [m.reference, m.articleName, m.articleCategory, m.fournisseur, m.type, m.quantity, this.formatCurrency(m.cost)])
       });
       doc.save('rapport_stock.pdf');
    }
  },
  mounted() {
    this.fetchMovements();
  }
};
</script>

<style scoped>
.mouvements-dashboard { background-color: #f0f2f5; min-height: 100vh; }
.stat-card { position: relative; background: white !important; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.stat-card-glow { position: absolute; top: 0; right: 0; width: 150px; height: 150px; border-radius: 50%; }
.stat-icon-box { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; }
.shadow-hover:hover { transform: translateY(-8px); box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1) !important; }
.filter-toolbar { border: 1px solid #e2e8f0 !important; }
.search-input-premium { max-width: 400px; }
.type-toggle-premium { background: #f1f5f9; padding: 4px; border-radius: 12px; }
.type-toggle-premium .v-btn { border-radius: 8px !important; font-weight: 700; height: 36px; min-width: 100px; }
.premium-table { background: white !important; }
:deep(.v-data-table-header) { background: #f8fafc !important; }
:deep(.v-data-table-header__content) { font-weight: 800 !important; font-size: 11px !important; color: #475569 !important; text-transform: uppercase; letter-spacing: 1px; }
:deep(tr) { cursor: pointer; transition: background 0.2s; }
:deep(tr:hover) { background-color: #f8fafc !important; }
.ref-dot { width: 8px; height: 8px; border-radius: 50%; box-shadow: 0 0 10px currentColor; }
.category-avatar { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.vendor-initial { width: 28px; height: 28px; border-radius: 50%; background: #e2e8f0; color: #475569; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 900; }
.type-pill { display: inline-flex; align-items: center; padding: 4px 12px; border-radius: 100px; font-size: 11px; font-weight: 900; letter-spacing: 0.5px; }
.type-in { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.type-out { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.voucher-header { min-height: 200px; }
.voucher-header-pattern { position: absolute; inset: 0; opacity: 0.1; background-image: radial-gradient(#fff 1px, transparent 1px); background-size: 20px 20px; }
.movement-summary-grid { border: 1px solid #e2e8f0; }
.description-text { background: #f8fafc; color: #475569; line-height: 1.8; border-color: #e2e8f0; }
.rounded-2xl { border-radius: 20px !important; }
.font-mono { font-family: 'JetBrains Mono', 'Fira Code', monospace; font-size: 0.85rem; }
.italic { font-style: italic; }
</style>
