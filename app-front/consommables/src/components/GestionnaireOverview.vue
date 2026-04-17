<template>
  <div>
    <h2 class="text-h5 font-weight-bold mb-6">Tableau de Bord — Gestionnaire de Stock</h2>

    <!-- Stats Row -->
    <v-row class="mb-6">
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="blue" size="36" class="mb-2">mdi-package-variant-closed</v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.totalArticles }}</div>
            <div class="text-caption text-grey">Total Articles</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="red" size="36" class="mb-2">mdi-alert</v-icon>
            <div class="text-h4 font-weight-bold text-red">{{ stats.lowStockCount }}</div>
            <div class="text-caption text-grey">Stock Bas</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="green" size="36" class="mb-2">mdi-check-circle</v-icon>
            <div class="text-h4 font-weight-bold text-green">{{ stats.inStockCount }}</div>
            <div class="text-caption text-grey">En Stock</div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <v-card rounded="xl" elevation="2">
          <v-card-text class="text-center py-6">
            <v-icon color="orange" size="36" class="mb-2">mdi-receipt</v-icon>
            <div class="text-h4 font-weight-bold text-orange">{{ stats.invoiceCount }}</div>
            <div class="text-caption text-grey">Factures</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Quick Actions -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="green" class="mr-2">mdi-flash</v-icon>
            Actions Rapides
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-list>
              <v-list-item prepend-icon="mdi-view-grid" title="Consulter le Catalogue" @click="$router.push('/gestionnaire/stock/catalogue')"></v-list-item>
              <v-list-item prepend-icon="mdi-chart-bar" title="Voir le Suivi Consommation" @click="$router.push('/gestionnaire/stock/articles')"></v-list-item>
              <v-list-item prepend-icon="mdi-receipt" title="Importer une Facture (OCR)" @click="$router.push('/gestionnaire/documents/invoices')"></v-list-item>
              <v-list-item prepend-icon="mdi-database" title="Inventaire Physique" @click="$router.push('/gestionnaire/stock/inventory')"></v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card rounded="xl" elevation="2">
          <v-card-title class="d-flex align-center">
            <v-icon color="red" class="mr-2">mdi-bell-alert</v-icon>
            Alertes de Stock Bas
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <div v-if="stats.lowStockCount > 0" class="text-center py-4">
              <v-icon color="warning" size="48">mdi-alert-outline</v-icon>
              <p class="text-body-1 mt-2"><strong>{{ stats.lowStockCount }}</strong> articles nécessitent un réapprovisionnement</p>
              <v-btn color="primary" variant="outlined" @click="$router.push('/gestionnaire/stock/articles')">
                Voir les détails
              </v-btn>
            </div>
            <div v-else class="text-center py-4">
              <v-icon color="success" size="48">mdi-check-circle-outline</v-icon>
              <p class="text-body-1 mt-2">Tous les niveaux de stock sont corrects</p>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import ArticleService from '@/services/ArticleService'
import InvoiceService from '@/services/InvoiceService'

export default {
  name: 'GestionnaireOverview',
  data() {
    return {
      stats: {
        totalArticles: 0,
        lowStockCount: 0,
        inStockCount: 0,
        invoiceCount: 0
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
      const invoices = await InvoiceService.getAllInvoices()
      this.stats.invoiceCount = invoices?.length || 0
    } catch (e) { console.error(e) }
  }
}
</script>
