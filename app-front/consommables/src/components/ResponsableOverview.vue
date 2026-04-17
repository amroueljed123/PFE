<template>
  <div>
    <h2 class="text-h5 font-weight-bold mb-6">Tableau de Bord — Responsable Opérationel</h2>

    <!-- Stats Row -->
    <v-row class="mb-6">
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="orange" size="36" class="mb-2">mdi-clipboard-check</v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.pendingOrders }}</div>
            <div class="text-caption text-grey">Commandes en attente</div>
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
            <v-icon color="blue" size="36" class="mb-2">mdi-truck-delivery</v-icon>
            <div class="text-h4 font-weight-bold text-blue">{{ stats.totalSuppliers }}</div>
            <div class="text-caption text-grey">Fournisseurs</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="green" size="36" class="mb-2">mdi-lightbulb-on</v-icon>
            <div class="text-h4 font-weight-bold text-green">{{ stats.recommendations }}</div>
            <div class="text-caption text-grey">Recommandations</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Quick Actions -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="orange" class="mr-2">mdi-flash</v-icon>
            Actions Rapides
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-list>
              <v-list-item prepend-icon="mdi-clipboard-list" title="Gérer les Bons de Commande" @click="$router.push('/responsable/supply/purchase-orders')"></v-list-item>
              <v-list-item prepend-icon="mdi-warehouse" title="Consulter les Fournisseurs" @click="$router.push('/responsable/supply/suppliers')"></v-list-item>
              <v-list-item prepend-icon="mdi-alert-circle" title="Voir les Anomalies" @click="$router.push('/responsable/analytics/anomalies')"></v-list-item>
              <v-list-item prepend-icon="mdi-lightbulb-on" title="Prévisions & Optimisation" @click="$router.push('/responsable/analytics/optimization')"></v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="red" class="mr-2">mdi-shield-alert</v-icon>
            État des Anomalies
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <div v-if="stats.activeAnomalies > 0" class="text-center py-4">
              <v-icon color="error" size="48">mdi-alert-octagram</v-icon>
              <p class="text-body-1 mt-2"><strong>{{ stats.activeAnomalies }}</strong> anomalies nécessitent votre attention</p>
              <v-btn color="error" variant="outlined" @click="$router.push('/responsable/analytics/anomalies')">
                Investiguer
              </v-btn>
            </div>
            <div v-else class="text-center py-4">
              <v-icon color="success" size="48">mdi-shield-check</v-icon>
              <p class="text-body-1 mt-2">Aucune anomalie active détectée</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import AnalyticsService from '@/services/AnalyticsService'
import SupplyChainService from '@/services/SupplyChainService'

export default {
  name: 'ResponsableOverview',
  data() {
    return {
      stats: {
        pendingOrders: 0,
        activeAnomalies: 0,
        totalSuppliers: 0,
        recommendations: 0
      }
    }
  },
  async mounted() {
    try {
      const anomalies = await AnalyticsService.getActiveAnomalies()
      this.stats.activeAnomalies = anomalies?.length || 0
    } catch (e) { console.error(e) }
    try {
      const recommendations = await AnalyticsService.getHighPriorityRecommendations()
      this.stats.recommendations = recommendations?.length || 0
    } catch (e) { console.error(e) }
    try {
      const suppliers = await SupplyChainService.getSuppliers()
      this.stats.totalSuppliers = suppliers?.length || 0
    } catch (e) { console.error(e) }
    try {
      const orders = await SupplyChainService.getPurchaseOrders()
      this.stats.pendingOrders = (orders || []).filter(o => o.status === 'PENDING' || o.status === 'DRAFT').length
    } catch (e) { console.error(e) }
  }
}
</script>
