<template>
  <v-app>
    <!-- Stunning Animated Background -->
    <div class="creative-background">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>
    
    <!-- Top Navigation Bar -->
    <v-app-bar color="transparent" elevation="0" class="px-6 mt-2">
      <div class="d-flex align-center">
        <v-avatar color="white" size="48" class="elevation-4 glass-avatar">
          <v-icon color="primary" size="28">mdi-cube-scan</v-icon>
        </v-avatar>
        <h2 class="ml-4 text-h5 font-weight-bold text-white brand-text">StockFlow <span class="text-caption font-weight-light">Portal</span></h2>
      </div>
      <v-spacer></v-spacer>
      <div class="user-greeting d-none d-sm-flex align-center mr-6 text-white text-subtitle-1">
        <span class="mr-2">Bonjour, <strong>{{ userName }}</strong></span>
        <v-avatar size="36" color="primary">
          <span class="text-white text-button">{{ userInitials }}</span>
        </v-avatar>
      </div>
      <v-btn class="glass-btn text-white text-uppercase font-weight-bold" rounded="xl" @click="logout" prepend-icon="mdi-logout-variant">
        Déconnexion
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container class="fill-height pb-12" fluid>
        <v-row class="fill-height align-center justify-center">
          <v-col cols="12" md="11" lg="10" xl="9">
            
            <!-- Main Content Card -->
            <v-card class="glassmorphism-card pa-8 pa-md-12 rounded-xl border" elevation="24">
              
              <div class="text-center mb-10">
                <v-chip color="primary" variant="flat" size="small" class="mb-4 text-uppercase font-weight-bold px-4 tracking-widest label-chip">
                  Interface Unique
                </v-chip>
                <h1 class="text-h3 text-md-h2 font-weight-black gradient-text mb-2">Demander un Prélèvement</h1>
                <p class="text-h6 text-medium-emphasis font-weight-regular mt-4 max-w-xl mx-auto">
                  Sélectionnez un article, indiquez la quantité désirée et validez votre demande en un seul clic.
                </p>
              </div>

              <!-- Content Sections -->
              <v-row>
                
                <!-- Left Column: Form & Catalog -->
                <v-col cols="12" md="7" class="pr-md-8">
                  <div class="section-title mb-6 d-flex align-center">
                    <v-icon color="primary" class="mr-3" size="32">mdi-cart-arrow-down</v-icon>
                    <h3 class="text-h5 font-weight-bold text-grey-darken-3">Nouvelle Demande</h3>
                  </div>

                  <v-form ref="form" v-model="valid">
                    
                    <!-- Search / Select Article with rich design -->
                    <div class="creative-input-group mb-6">
                      <v-autocomplete
                        v-model="selectedArticle"
                        :items="availableArticles"
                        item-title="nom"
                        item-value="id"
                        return-object
                        placeholder="Rechercher un article..."
                        variant="solo"
                        class="glass-input"
                        bg-color="transparent"
                        elevation="0"
                        hide-details
                        :rules="[v => !!v || '']"
                      >
                        <template v-slot:prepend-inner>
                          <v-icon color="primary" class="mr-2">mdi-magnify</v-icon>
                        </template>
                        
                        <template v-slot:item="{ props, item }">
                          <v-list-item v-bind="props" class="py-3 px-4 custom-list-item" :class="{'out-of-stock': (item?.raw || item)?.quantite <= 0}">
                            <template v-slot:prepend>
                              <v-avatar size="48" rounded="lg" color="grey-lighten-3" class="mr-4 img-container">
                                <v-img v-if="(item?.raw || item)?.image" :src="(item?.raw || item)?.image" cover></v-img>
                                <v-icon v-else color="grey-darken-1" size="30">mdi-package-variant</v-icon>
                              </v-avatar>
                            </template>
                            <v-list-item-title class="font-weight-bold text-subtitle-1 mb-1">{{ (item?.raw || item)?.nom }}</v-list-item-title>
                            <v-list-item-subtitle class="d-flex align-center">
                              <v-chip size="x-small" :color="(item?.raw || item)?.quantite > 0 ? 'success' : 'error'" class="mr-2 border" variant="outlined">
                                Stock: {{ (item?.raw || item)?.quantite }}
                              </v-chip>
                              <span class="text-caption text-grey">{{ (item?.raw || item)?.type }}</span>
                            </v-list-item-subtitle>
                          </v-list-item>
                        </template>
                      </v-autocomplete>
                    </div>

                    <!-- Selected Article Preview & Quantity -->
                    <v-expand-transition>
                      <v-card v-if="selectedArticle" class="selected-item-card mb-8 rounded-lg border pa-4 d-flex flex-column flex-sm-row align-center elevation-0">
                        <v-avatar size="80" rounded="lg" color="grey-lighten-2" class="mb-4 mb-sm-0 mr-sm-6 selected-img">
                          <v-img v-if="selectedArticle.image" :src="selectedArticle.image" cover></v-img>
                          <v-icon v-else size="40" color="white">mdi-check-circle-outline</v-icon>
                        </v-avatar>
                        
                        <div class="flex-grow-1 text-center text-sm-left w-100">
                          <h4 class="text-h6 font-weight-bold mb-1">{{ selectedArticle.nom }}</h4>
                          <p class="text-body-2 text-primary font-weight-medium mb-4">{{ selectedArticle.type }}</p>
                          
                          <div class="d-flex align-center justify-center justify-sm-start flex-wrap gap-2">
                            <span class="text-body-2 font-weight-bold mr-3">Quantité :</span>
                            <div class="quantite-selector d-flex align-center bg-white rounded-pill border px-2 py-1 elevation-2">
                              <v-btn icon="mdi-minus" size="x-small" variant="text" color="grey-darken-2" @click="decrement" :disabled="quantite <= 1"></v-btn>
                              <input type="number" v-model.number="quantite" class="quantite-input text-center text-subtitle-1 font-weight-bold mx-2" min="1" :max="selectedArticle.quantite" />
                              <v-btn icon="mdi-plus" size="x-small" variant="text" color="primary" @click="increment" :disabled="quantite >= selectedArticle.quantite"></v-btn>
                            </div>
                          </div>
                        </div>
                      </v-card>
                    </v-expand-transition>

                    <!-- Submit Button -->
                    <v-btn
                      block
                      size="x-large"
                      color="primary"
                      class="submit-btn text-white font-weight-bold rounded-xl mt-2 elevation-6"
                      height="64"
                      :disabled="!selectedArticle || quantite < 1 || quantite > (selectedArticle?.quantite || 0)"
                      :loading="submitting"
                      @click="submitDemande"
                    >
                      <v-icon left class="mr-2" size="24">mdi-send-circle</v-icon>
                      <span class="text-h6">Confirmer la demande</span>
                    </v-btn>
                    
                    <div v-if="selectedArticle && quantite > selectedArticle.quantite" class="text-error text-caption text-center mt-3 font-weight-medium d-flex align-center justify-center">
                      <v-icon size="16" class="mr-1">mdi-alert-circle</v-icon> La quantité demandée dépasse le stock disponible.
                    </div>

                  </v-form>
                </v-col>

                <!-- Right Column: History -->
                <v-col cols="12" md="5" class="pl-md-8 pt-8 pt-md-0 border-left-md mt-6 mt-md-0 d-flex flex-column">
                  <div class="section-title mb-6 d-flex align-center justify-space-between w-100">
                    <div class="d-flex align-center">
                      <v-icon color="secondary" class="mr-3" size="32">mdi-history</v-icon>
                      <h3 class="text-h5 font-weight-bold text-grey-darken-3">Mes Demandes</h3>
                    </div>
                    <v-chip color="secondary" variant="tonal" size="small" class="font-weight-bold">
                      {{ demandes.length }} Total
                    </v-chip>
                  </div>

                  <div v-if="loadingHistory" class="d-flex justify-center my-10 w-100">
                    <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
                  </div>

                  <div v-else-if="demandes.length === 0" class="empty-state fill-height d-flex flex-column align-center justify-center py-10 w-100 text-center">
                    <v-avatar color="blue-lighten-5" size="100" class="mb-6">
                      <v-icon size="50" color="blue-lighten-2">mdi-flask-empty-outline</v-icon>
                    </v-avatar>
                    <h4 class="text-h6 font-weight-medium text-grey-darken-2">Aucune demande</h4>
                    <p class="text-body-2 text-grey">Faites votre première demande à gauche.</p>
                  </div>

                  <div v-else class="history-list pr-2 flex-grow-1 w-100 pb-2">
                    <v-hover v-for="demande in displayedDemandes" :key="demande.id" v-slot="{ isHovering, props }">
                      <v-card 
                        v-bind="props" 
                        class="history-card mb-4 rounded-xl border d-flex align-center pa-4 transition-swing"
                        :elevation="isHovering ? 4 : 0"
                        :class="{'on-hover': isHovering}"
                        color="white"
                      >
                        <div class="status-indicator" :class="getStatusClass(demande.statut)"></div>
                        
                        <div class="flex-grow-1 ml-4 overflow-hidden">
                          <div class="d-flex justify-space-between align-start mb-1">
                            <h4 class="text-subtitle-1 font-weight-bold text-truncate pr-2">{{ demande.article.nom }}</h4>
                            <v-chip size="x-small" :color="getStatusColor(demande.statut)" class="font-weight-bold text-uppercase px-2" variant="flat">
                              {{ demande.statut }}
                            </v-chip>
                          </div>
                          
                          <div class="d-flex justify-space-between align-center mt-2">
                            <div class="d-flex align-center text-primary text-body-2 font-weight-bold bg-blue-grey-lighten-5 px-3 py-1 rounded-pill">
                              <v-icon size="14" class="mr-1">mdi-package-variant-closed</v-icon> QTE: {{ demande.quantite }}
                            </div>
                            <div class="text-caption text-grey font-weight-medium d-flex align-center">
                              <v-icon size="12" class="mr-1">mdi-calendar-clock</v-icon>
                              {{ formatDate(demande.dateDemande) }}
                            </div>
                          </div>
                        </div>
                      </v-card>
                    </v-hover>
                    
                    <div v-if="demandes.length > 4" class="text-center mt-4">
                      <v-btn variant="text" color="primary" class="font-weight-bold text-none" @click="showAllHistory = !showAllHistory">
                        {{ showAllHistory ? 'Afficher moins' : 'Voir tout l\'historique' }} <v-icon right>{{ showAllHistory ? 'mdi-chevron-up' : 'mdi-chevron-down' }}</v-icon>
                      </v-btn>
                    </div>
                  </div>
                </v-col>

              </v-row>
            </v-card>
            
          </v-col>
        </v-row>
      </v-container>
    </v-main>
    
    <!-- Success Snackbar -->
    <v-snackbar v-model="snackbar" :color="snackbarColor" location="top" elevation="24" class="mt-8" timeout="4000" rounded="pill">
      <div class="d-flex align-center">
        <v-icon color="white" class="mr-3">{{ snackbarIcon }}</v-icon>
        <span class="text-subtitle-1 font-weight-medium">{{ snackbarText }}</span>
      </div>
    </v-snackbar>
  </v-app>
</template>

<script>
import ArticleService from '@/services/ArticleService';
import DemandePrelevementService from '@/services/DemandePrelevementService';

export default {
  name: 'CreativeUserDashboard',
  data() {
    return {
      userName: '',
      userId: null,
      articles: [],
      selectedArticle: null,
      quantite: 1,
      valid: false,
      submitting: false,
      
      demandes: [],
      loadingHistory: true,
      showAllHistory: false,
      
      snackbar: false,
      snackbarText: '',
      snackbarColor: 'success',
      snackbarIcon: 'mdi-check-circle'
    }
  },
  computed: {
    userInitials() {
      if (!this.userName) return 'U';
      const parts = this.userName.split(' ');
      if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
      return this.userName.substring(0, 2).toUpperCase();
    },
    availableArticles() {
      // Show articles with their exact names, searching handles internal filtering
      return this.articles;
    },
    displayedDemandes() {
      if (this.showAllHistory) return this.demandes;
      return this.demandes.slice(0, 4);
    }
  },
  watch: {
    selectedArticle(newVal) {
      if (newVal) {
        this.quantite = 1;
      }
    }
  },
  mounted() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.userId = user.id;
      this.userName = user.name || user.username || 'Utilisateur';
      
      this.fetchData();
    } else {
      this.$router.push('/login');
    }
  },
  methods: {
    async fetchData() {
      this.loadingHistory = true;
      try {
        const [articlesRes, demandesRes] = await Promise.all([
          ArticleService.getAllArticles(),
          DemandePrelevementService.getDemandesByUser(this.userId)
        ]);
        
        // Sort articles alphabetically, push out of stock to bottom
        this.articles = articlesRes.data.sort((a, b) => {
          if (a.quantite <= 0 && b.quantite > 0) return 1;
          if (a.quantite > 0 && b.quantite <= 0) return -1;
          return a.nom.localeCompare(b.nom);
        });
        
        // Sort demandes descending date
        this.demandes = demandesRes.data.sort((a, b) => new Date(b.dateDemande) - new Date(a.dateDemande));
      } catch (error) {
        console.error("Error fetching data:", error);
        this.showSnackbar('Erreur de chargement des données. Veuillez rafraîchir.', 'error', 'mdi-alert-circle');
      } finally {
        this.loadingHistory = false;
      }
    },
    
    increment() {
      if (this.selectedArticle && this.quantite < this.selectedArticle.quantite) {
        this.quantite++;
      }
    },
    
    decrement() {
      if (this.quantite > 1) {
        this.quantite--;
      }
    },
    
    async submitDemande() {
      if (!this.selectedArticle || this.quantite < 1 || this.quantite > this.selectedArticle.quantite) return;
      
      this.submitting = true;
      try {
        await DemandePrelevementService.createDemande({
          userId: this.userId,
          articleId: this.selectedArticle.id,
          quantite: this.quantite
        });
        
        this.showSnackbar('Demande envoyée avec succès !', 'success', 'mdi-check-decagram');
        
        // Reset form completely & elegantly
        this.selectedArticle = null;
        this.quantite = 1;
        if(this.$refs.form) this.$refs.form.resetValidation();
        
        // Refresh silently
        await this.fetchData();
        
      } catch (error) {
        console.error("Error creating demande:", error);
        const msg = error.response?.data?.message || 'Une erreur est survenue lors de la demande.';
        this.showSnackbar(msg, 'error', 'mdi-close-octagon');
      } finally {
        this.submitting = false;
      }
    },
    
    getStatusColor(status) {
      if (status === 'Approuvée') return '#00C853';
      if (status === 'Rejetée') return '#D50000';
      if (status === 'Validée') return '#2196F3';
      return '#FF9100'; // En attente
    },
    
    getStatusClass(status) {
      if (status === 'Approuvée') return 'bg-success';
      if (status === 'Rejetée') return 'bg-error';
      if (status === 'Validée') return 'bg-info';
      return 'bg-warning';
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('fr-FR', { 
        day: '2-digit', month: 'short', 
        hour: '2-digit', minute: '2-digit'
      }).format(date).replace(',', ' à');
    },
    
    showSnackbar(text, color, icon) {
      this.snackbarText = text;
      this.snackbarColor = color;
      this.snackbarIcon = icon;
      this.snackbar = true;
    },
    
    logout() {
      localStorage.clear();
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
/* ───── CREATIVE BACKGROUND ───── */
.creative-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #091a28 0%, #1a365d 100%);
  z-index: 0;
  overflow: hidden;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s infinite ease-in-out alternate;
}

.blob-1 {
  top: -10%;
  left: 20%;
  width: 50vw;
  height: 50vw;
  background: radial-gradient(circle, rgba(66,153,225,0.8) 0%, rgba(43,108,176,0) 70%);
  animation-delay: 0s;
}

.blob-2 {
  bottom: -20%;
  right: -10%;
  width: 60vw;
  height: 60vw;
  background: radial-gradient(circle, rgba(129,230,217,0.5) 0%, rgba(49,151,149,0) 70%);
  animation-delay: -5s;
}

.blob-3 {
  top: 30%;
  left: -20%;
  width: 40vw;
  height: 40vw;
  background: radial-gradient(circle, rgba(159,122,234,0.4) 0%, rgba(107,70,193,0) 70%);
  animation-delay: -10s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(5%, 5%) scale(1.05); }
  100% { transform: translate(-5%, 10%) scale(0.95); }
}

/* ───── UI COMPONENTS OVERRIDES ───── */
.v-main {
  position: relative;
  z-index: 1;
}

.glassmorphism-card {
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5) !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4) !important;
}

.glass-btn {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  transition: all 0.3s ease;
}

.glass-btn:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  transform: translateY(-2px);
}

.glass-input :deep(.v-field) {
  background: rgba(243, 244, 246, 0.8) !important;
  border-radius: 16px !important;
  border: 1px solid rgba(229, 231, 235, 1) !important;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05) inset !important;
  padding-top: 8px !important;
  padding-bottom: 8px !important;
  transition: all 0.3s ease;
}

.glass-input :deep(.v-field--focused) {
  background: #ffffff !important;
  border-color: var(--v-primary-base) !important;
  box-shadow: 0 4px 20px -5px rgba(var(--v-primary-base), 0.3) !important;
}

.gradient-text {
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.tracking-widest {
  letter-spacing: 0.1em !important;
}

.border-left-md {
  @media (min-width: 960px) {
    border-left: 1px dashed rgba(0, 0, 0, 0.1);
  }
}

/* ───── INTERACTIVE ELEMENTS ───── */
.custom-list-item {
  transition: all 0.2s ease;
  border-bottom: 1px solid rgba(0,0,0,0.05);
}

.custom-list-item:hover {
  background: rgba(var(--v-primary-base), 0.04);
}

.out-of-stock {
  opacity: 0.6;
  filter: grayscale(100%);
}

.selected-item-card {
  background: linear-gradient(to right, #f8fafc, #ffffff);
  border: 1px solid #e2e8f0;
}

.selected-img {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  border: 3px solid white;
}

.quantite-input {
  width: 50px;
  outline: none;
  border: none;
  background: transparent;
  -moz-appearance: textfield;
}
.quantite-input::-webkit-outer-spin-button,
.quantite-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.submit-btn {
  background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%) !important;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 10px 20px -5px rgba(33, 150, 243, 0.5) !important;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 15px 25px -5px rgba(33, 150, 243, 0.6) !important;
}

.submit-btn:disabled {
  background: #e0e0e0 !important;
  color: #9e9e9e !important;
  box-shadow: none !important;
  transform: none;
}

/* ───── HISTORY STYLES ───── */
.history-list {
  max-height: 500px;
  overflow-y: auto;
}

.history-list::-webkit-scrollbar {
  width: 6px;
}

.history-list::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.1);
  border-radius: 10px;
}

.history-card {
  position: relative;
  transition: all 0.3s ease;
  border: 1px solid #edf2f7;
}

.history-card.on-hover {
  transform: translateX(5px);
  border-color: #cbd5e1;
}

.status-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  border-radius: 20px 0 0 20px;
}
</style>
