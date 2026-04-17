<template>
  <div>
    <h2 class="text-h5 font-weight-bold mb-6">Tableau de Bord — Direction Générale</h2>

    <!-- KPI Row -->
    <v-row class="mb-6">
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="purple" size="36" class="mb-2">mdi-package-variant-closed</v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.totalArticles }}</div>
            <div class="text-caption text-grey">Total Articles</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="red" size="36" class="mb-2">mdi-alert-circle</v-icon>
            <div class="text-h4 font-weight-bold text-red">{{ stats.activeAnomalies }}</div>
            <div class="text-caption text-grey">Anomalies Actives</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="green" size="36" class="mb-2">mdi-currency-usd</v-icon>
            <div class="text-h4 font-weight-bold text-green">{{ stats.potentialSavings }}</div>
            <div class="text-caption text-grey">Économies (DA)</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="blue" size="36" class="mb-2">mdi-lightbulb-on</v-icon>
            <div class="text-h4 font-weight-bold text-blue">{{ stats.recommendations }}</div>
            <div class="text-caption text-grey">Recommandations</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Content Row -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="purple" class="mr-2">mdi-monitor-dashboard</v-icon>
            Indicateurs Clés
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-list>
              <v-list-item>
                <template v-slot:prepend><v-icon color="warning">mdi-alert</v-icon></template>
                <v-list-item-title>Articles en stock bas</v-list-item-title>
                <template v-slot:append><v-chip color="warning" size="small">{{ stats.lowStockCount }}</v-chip></template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend><v-icon color="info">mdi-clipboard-list</v-icon></template>
                <v-list-item-title>Commandes en cours</v-list-item-title>
                <template v-slot:append><v-chip color="info" size="small">{{ stats.pendingOrders }}</v-chip></template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend><v-icon color="error">mdi-alert-octagram</v-icon></template>
                <v-list-item-title>Anomalies critiques</v-list-item-title>
                <template v-slot:append><v-chip color="error" size="small">{{ stats.criticalAnomalies }}</v-chip></template>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend><v-icon color="success">mdi-check-decagram</v-icon></template>
                <v-list-item-title>Articles en stock</v-list-item-title>
                <template v-slot:append><v-chip color="success" size="small">{{ stats.inStockCount }}</v-chip></template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="purple" class="mr-2">mdi-flash</v-icon>
            Accès Rapide
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-list>
              <v-list-item prepend-icon="mdi-lightbulb-on" title="Rapport d'Optimisation" @click="$router.push('/directeur/reports/optimization')"></v-list-item>
              <v-list-item prepend-icon="mdi-alert-circle" title="Consulter les Anomalies" @click="$router.push('/directeur/reports/anomalies')"></v-list-item>
              <v-list-item prepend-icon="mdi-chart-bar" title="Statistiques des Articles" @click="$router.push('/directeur/stats/articles')"></v-list-item>
              <v-list-item prepend-icon="mdi-history" title="Journal d'Audit" @click="$router.push('/directeur/supervision/audit')"></v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import ArticleService from '@/services/ArticleService'
import AnalyticsService from '@/services/AnalyticsService'
import SupplyChainService from '@/services/SupplyChainService'

export default {
  name: 'DirecteurOverview',
  data() {
    return {
      stats: {
        totalArticles: 0,
        lowStockCount: 0,
        inStockCount: 0,
        activeAnomalies: 0,
        criticalAnomalies: 0,
        recommendations: 0,
        potentialSavings: '0',
        pendingOrders: 0
      }
    }
  },
  async mounted() {
    try {
      const articleStats = await ArticleService.getArticleStats()
      this.stats.totalArticles = articleStats.data?.totalArticles || 0
      this.stats.lowStockCount = articleStats.data?.lowStockCount || 0
      this.stats.inStockCount = articleStats.data?.inStockCount || 0
    } catch (e) { console.error(e) }
    try {
      const anomalies = await AnalyticsService.getActiveAnomalies()
      this.stats.activeAnomalies = anomalies?.length || 0
      this.stats.criticalAnomalies = (anomalies || []).filter(a => a.severity === 'CRITICAL').length
    } catch (e) { console.error(e) }
    try {
      const recommendations = await AnalyticsService.getHighPriorityRecommendations()
      this.stats.recommendations = recommendations?.length || 0
      const savings = (recommendations || []).reduce((sum, r) => sum + (r.expectedSavings || 0), 0)
      this.stats.potentialSavings = savings.toLocaleString()
    } catch (e) { console.error(e) }
    try {
      const orders = await SupplyChainService.getPurchaseOrders()
      this.stats.pendingOrders = (orders || []).filter(o => o.status === 'PENDING' || o.status === 'DRAFT').length
    } catch (e) { console.error(e) }
  }
}
</script>
