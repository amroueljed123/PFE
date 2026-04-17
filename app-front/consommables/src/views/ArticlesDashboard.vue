<template>
  <div class="articles-dashboard">
    <div class="d-flex align-center mb-6">
      <div>
        <h1 class="text-h5 font-weight-bold text-grey-darken-3">Tableau de Bord</h1>
        <p class="text-caption text-grey mb-0">Vue d'ensemble de l'inventaire</p>
      </div>
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
        <v-card class="stat-card" elevation="2" rounded="lg">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Total Articles</div>
                <div class="stat-value text-primary">{{ stats.totalArticles || 0 }}</div>
                <div class="text-caption text-grey mt-1">{{ Object.keys(stats.countByType || {}).length }} catégorie(s)</div>
              </div>
              <v-avatar color="blue-lighten-5" size="56" rounded="lg">
                <v-icon color="primary" size="28">mdi-package-variant-closed</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2" rounded="lg">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Valeur Totale</div>
                <div class="stat-value text-success" style="font-size:20px">{{ formatCurrency(stats.totalValue) }}</div>
                <div class="text-caption text-grey mt-1">Valeur inventaire</div>
              </div>
              <v-avatar color="green-lighten-5" size="56" rounded="lg">
                <v-icon color="success" size="28">mdi-cash-multiple</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2" rounded="lg" :class="{ 'border-error': stats.lowStockCount > 0 }">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Stock Critique</div>
                <div class="stat-value" :class="stats.lowStockCount > 0 ? 'text-error' : 'text-success'">
                  {{ stats.lowStockCount || 0 }}
                </div>
                <div class="text-caption text-grey mt-1">Sous le seuil d'alerte</div>
              </div>
              <v-avatar :color="stats.lowStockCount > 0 ? 'red-lighten-5' : 'green-lighten-5'" size="56" rounded="lg">
                <v-icon :color="stats.lowStockCount > 0 ? 'error' : 'success'" size="28">
                  {{ stats.lowStockCount > 0 ? 'mdi-alert-circle' : 'mdi-check-circle' }}
                </v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2" rounded="lg">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Familles</div>
                <div class="stat-value text-info">{{ Object.keys(stats.countByFamille || {}).length }}</div>
                <div class="text-caption text-grey mt-1">Familles d'articles</div>
              </div>
              <v-avatar color="cyan-lighten-5" size="56" rounded="lg">
                <v-icon color="info" size="28">mdi-shape</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Low stock alert banner -->
    <v-alert
      v-if="stats.lowStockCount > 0"
      type="warning"
      variant="tonal"
      icon="mdi-alert-decagram"
      class="mt-4"
      rounded="lg"
      closable
    >
      <strong>{{ stats.lowStockCount }} article(s)</strong> ont un stock critique (en dessous du seuil d'alerte).
      Pensez à réapprovisionner.
    </v-alert>

    <v-row class="mt-4">
      <!-- Distribution by Type -->
      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="d-flex align-center pa-5 pb-3">
            <v-icon color="primary" class="mr-2">mdi-chart-bar</v-icon>
            <span class="text-subtitle-1 font-weight-bold">Répartition par Catégorie</span>
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-5">
            <template v-if="stats.totalArticles">
              <div
                v-for="(count, type) in stats.countByType"
                :key="type"
                class="mb-4"
              >
                <div class="d-flex justify-space-between align-center mb-1">
                  <div class="d-flex align-center">
                    <v-icon :color="getTypeColor(type)" size="16" class="mr-2">{{ getTypeIcon(type) }}</v-icon>
                    <span class="text-body-2 font-weight-medium">{{ formatTypeName(type) }}</span>
                  </div>
                  <span class="text-caption font-weight-bold text-grey-darken-1">{{ count }} / {{ stats.totalArticles }}</span>
                </div>
                <v-progress-linear
                  :model-value="(count / stats.totalArticles) * 100"
                  :color="getTypeColor(type)"
                  height="8"
                  rounded
                  bg-color="grey-lighten-3"
                ></v-progress-linear>
              </div>
            </template>
            <div v-else class="text-center py-8">
              <v-icon size="48" color="grey-lighten-2">mdi-chart-bar</v-icon>
              <div class="text-grey mt-2">Aucune donnée disponible</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Distribution by Family -->
      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="d-flex align-center pa-5 pb-3">
            <v-icon color="secondary" class="mr-2">mdi-folder-multiple</v-icon>
            <span class="text-subtitle-1 font-weight-bold">Articles par Famille</span>
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-4" style="max-height:320px; overflow-y:auto;">
            <template v-if="stats.totalArticles">
              <div class="d-flex flex-column gap-2">
                <div
                  v-for="(count, famille) in stats.countByFamille"
                  :key="famille"
                  class="d-flex align-center justify-space-between pa-2 rounded mb-1"
                  style="background: rgba(0,0,0,0.02);"
                >
                  <div class="d-flex align-center">
                    <v-icon color="secondary" size="18" class="mr-3">mdi-folder</v-icon>
                    <span class="text-body-2 font-weight-medium">{{ famille }}</span>
                  </div>
                  <v-chip color="secondary" size="small" variant="tonal" class="font-weight-bold">
                    {{ count }}
                  </v-chip>
                </div>
              </div>
            </template>
            <div v-else class="text-center py-8">
              <v-icon size="48" color="grey-lighten-2">mdi-folder-open</v-icon>
              <div class="text-grey mt-2">Aucune famille enregistrée</div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Category Quick Access -->
    <v-card elevation="2" rounded="lg" class="mt-4">
      <v-card-title class="pa-5 pb-3">
        <v-icon color="primary" class="mr-2">mdi-apps</v-icon>
        <span class="text-subtitle-1 font-weight-bold">Accès Rapide aux Catégories</span>
      </v-card-title>
      <v-divider></v-divider>
      <v-card-text class="pa-4">
        <v-row>
          <v-col
            v-for="cat in categories"
            :key="cat.route"
            cols="6" sm="4" md="3" lg="2"
          >
            <v-card
              @click="$router.push(cat.route)"
              class="category-card text-center pa-3"
              :color="cat.color + '-lighten-5'"
              elevation="0"
              hover
              rounded="lg"
            >
              <v-icon :color="cat.color" size="28" class="mb-2">{{ cat.icon }}</v-icon>
              <div class="text-caption font-weight-medium text-grey-darken-2">{{ cat.label }}</div>
              <v-chip
                :color="cat.color"
                size="x-small"
                variant="tonal"
                class="mt-1"
              >
                {{ stats.countByType?.[cat.type] || 0 }}
              </v-chip>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
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
      },
      categories: [
        { label: 'Ordinateurs', type: 'ORDINATEUR', route: '/admin/articles/ordinateurs', icon: 'mdi-desktop-classic', color: 'blue' },
        { label: 'Moniteurs', type: 'MONITEUR', route: '/admin/articles/moniteurs', icon: 'mdi-monitor', color: 'indigo' },
        { label: 'Périphériques', type: 'PERIPHERIQUE', route: '/admin/articles/peripheriques', icon: 'mdi-usb', color: 'purple' },
        { label: 'Imprimantes', type: 'IMPRIMANTE', route: '/admin/articles/imprimantes', icon: 'mdi-printer', color: 'teal' },
        { label: 'Téléphones', type: 'TELEPHONE', route: '/admin/articles/telephones', icon: 'mdi-phone', color: 'green' },
        { label: 'Cartouches', type: 'CARTOUCHE', route: '/admin/articles/cartouches', icon: 'mdi-printer-pos', color: 'orange' },
        { label: 'Consommables', type: 'CONSOMMABLE', route: '/admin/articles/consommables', icon: 'mdi-package-variant', color: 'amber' },
        { label: 'Bureaux', type: 'BUREAU', route: '/admin/articles/bureaux', icon: 'mdi-desk', color: 'brown' },
        { label: 'Chaises', type: 'CHAISE', route: '/admin/articles/chaises', icon: 'mdi-seat', color: 'red' },
        { label: 'Meubles', type: 'MEUBLE', route: '/admin/articles/meubles', icon: 'mdi-wardrobe', color: 'deep-orange' },
        { label: 'Étagères', type: 'ETAGERE', route: '/admin/articles/etageres', icon: 'mdi-bookshelf', color: 'grey' }
      ]
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
      if (!value) return '0,00 DA'
      return new Intl.NumberFormat('fr-DZ', {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(value) + ' DA'
    },
    formatTypeName(type) {
      const names = {
        CONSOMMABLE: 'Consommables', CARTOUCHE: 'Cartouches', MEUBLE: 'Meubles',
        BUREAU: 'Bureaux', CHAISE: 'Chaises', ETAGERE: 'Étagères',
        IMPRIMANTE: 'Imprimantes', ORDINATEUR: 'Ordinateurs',
        MONITEUR: 'Moniteurs', PERIPHERIQUE: 'Périphériques', TELEPHONE: 'Téléphones'
      }
      return names[type] || type
    },
    getTypeColor(type) {
      const colors = {
        CONSOMMABLE: 'amber', CARTOUCHE: 'orange', MEUBLE: 'deep-orange',
        BUREAU: 'brown', CHAISE: 'red', ETAGERE: 'grey',
        IMPRIMANTE: 'teal', ORDINATEUR: 'blue', MONITEUR: 'indigo',
        PERIPHERIQUE: 'purple', TELEPHONE: 'green'
      }
      return colors[type] || 'primary'
    },
    getTypeIcon(type) {
      const icons = {
        CONSOMMABLE: 'mdi-package-variant', CARTOUCHE: 'mdi-printer-pos',
        MEUBLE: 'mdi-wardrobe', BUREAU: 'mdi-desk', CHAISE: 'mdi-seat',
        ETAGERE: 'mdi-bookshelf', IMPRIMANTE: 'mdi-printer',
        ORDINATEUR: 'mdi-desktop-classic', MONITEUR: 'mdi-monitor',
        PERIPHERIQUE: 'mdi-usb', TELEPHONE: 'mdi-phone'
      }
      return icons[type] || 'mdi-package'
    }
  }
}
</script>

<style scoped>
.articles-dashboard {
  padding: 4px 0;
}

.stat-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1) !important;
}

.stat-label {
  font-size: 11px;
  color: #9e9e9e;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.8px;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  margin-top: 4px;
  line-height: 1;
}

.border-error {
  border: 1px solid #ef9a9a !important;
}

.category-card {
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.category-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}
</style>
