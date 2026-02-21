<template>
  <div class="articles-dashboard">
    <div class="d-flex align-center mb-6">
      <v-icon color="primary" size="32" class="mr-3">mdi-chart-box</v-icon>
      <h1 class="text-h4 font-weight-bold">Tableau de Bord des Articles</h1>
      <v-spacer></v-spacer>
      <v-btn
        prepend-icon="mdi-refresh"
        color="primary"
        variant="tonal"
        :loading="loading"
        @click="loadStats"
      >
        Actualiser
      </v-btn>
    </div>

    <!-- Summary Cards -->
    <v-row>
      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Total Articles</div>
                <div class="stat-value text-primary">{{ stats.totalArticles || 0 }}</div>
              </div>
              <v-avatar color="primary-lighten-5" size="56">
                <v-icon color="primary" size="32">mdi-package-variant-closed</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Valeur Totale</div>
                <div class="stat-value text-success">{{ formatCurrency(stats.totalValue) }}</div>
              </div>
              <v-avatar color="success-lighten-5" size="56">
                <v-icon color="success" size="32">mdi-currency-eur</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Alerte Stock Bas</div>
                <div class="stat-value text-error">{{ stats.lowStockCount || 0 }}</div>
              </div>
              <v-avatar color="error-lighten-4" size="56">
                <v-icon color="error" size="32">mdi-alert-circle</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Catégories</div>
                <div class="stat-value text-info">{{ Object.keys(stats.countByType || {}).length }}</div>
              </div>
              <v-avatar color="info-lighten-5" size="56">
                <v-icon color="info" size="32">mdi-shape</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <!-- Type Distribution -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="fill-height">
          <v-card-title class="d-flex align-center py-4 px-6">
            <v-icon color="primary" class="mr-2">mdi-chart-pie</v-icon>
            Distribution par Type
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-6">
            <template v-for="(count, type) in stats.countByType" :key="type">
              <div class="mb-4">
                <div class="d-flex justify-space-between mb-1">
                  <span class="text-subtitle-2 font-weight-medium">{{ type }}</span>
                  <span class="text-caption font-weight-bold">{{ count }} articles</span>
                </div>
                <v-progress-linear
                  :model-value="(count / stats.totalArticles) * 100"
                  color="primary"
                  height="12"
                  rounded
                ></v-progress-linear>
              </div>
            </template>
            <div v-if="!stats.totalArticles" class="text-center py-8 text-grey">
              Aucune donnée à afficher
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Family Distribution -->
      <v-col cols="12" md="6">
        <v-card elevation="2" class="fill-height">
          <v-card-title class="d-flex align-center py-4 px-6">
            <v-icon color="secondary" class="mr-2">mdi-family-tree</v-icon>
            Articles par Famille
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-6">
             <v-list density="comfortable">
               <v-list-item
                 v-for="(count, famille) in stats.countByFamille"
                 :key="famille"
                 :title="famille"
                 :append-avatar="count"
               >
                 <template v-slot:append>
                    <v-chip color="secondary" size="small" variant="flat">{{ count }}</v-chip>
                 </template>
               </v-list-item>
             </v-list>
             <div v-if="!stats.totalArticles" class="text-center py-8 text-grey">
              Aucune famille enregistrée
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Low Stock Items Notification -->
    <v-alert
      v-if="stats.lowStockCount > 0"
      type="warning"
      variant="tonal"
      icon="mdi-alert-decagram"
      class="mt-6"
      title="Alerte Stock"
      closable
    >
      Il y a {{ stats.lowStockCount }} articles avec un stock critique (moins de 5 unités).
      Vérifiez les inventaires pour éviter les ruptures.
    </v-alert>
  </div>
</template>

<script>
import ArticleService from '@/services/ArticleService'

export default {
  name: 'ArticlesDashboard',
  data() {
    return {
      loading: false,
      stats: {
        totalArticles: 0,
        totalValue: 0,
        countByType: {},
        countByFamille: {},
        lowStockCount: 0
      }
    }
  },
  mounted() {
    this.loadStats()
  },
  methods: {
    async loadStats() {
      this.loading = true
      try {
        const response = await ArticleService.getArticleStats()
        this.stats = response.data
      } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error)
      } finally {
        this.loading = false
      }
    },
    formatCurrency(value) {
      if (!value) return '0,00 €'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(value)
    }
  }
}
</script>

<style scoped>
.articles-dashboard {
  padding: 24px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.2s ease-in-out;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-label {
  font-size: 14px;
  color: #616161;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-top: 4px;
}
</style>
