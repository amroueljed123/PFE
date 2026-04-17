<template>
  <div class="admin-overview">
    <div class="d-flex align-center mb-6">
      <div>
        <h1 class="text-h5 font-weight-bold text-grey-darken-3">Bienvenue, {{ currentUser.name || 'Administrateur' }}</h1>
        <p class="text-caption text-grey mb-0">Vue d'ensemble du système ETAP</p>
      </div>
      <v-spacer></v-spacer>
      <v-chip color="primary" variant="tonal" prepend-icon="mdi-calendar">
        {{ today }}
      </v-chip>
    </div>

    <!-- User Stats -->
    <v-row>
      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2" rounded="lg">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Utilisateurs</div>
                <div class="stat-value text-primary">{{ totalUsers }}</div>
              </div>
              <v-avatar color="blue-lighten-5" size="52" rounded="lg">
                <v-icon color="primary" size="26">mdi-account-multiple</v-icon>
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
                <div class="stat-label">Administrateurs</div>
                <div class="stat-value text-error">{{ adminUsers }}</div>
              </div>
              <v-avatar color="red-lighten-5" size="52" rounded="lg">
                <v-icon color="error" size="26">mdi-shield-account</v-icon>
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
                <div class="stat-label">Total Articles</div>
                <div class="stat-value text-success">{{ articleStats.totalArticles || 0 }}</div>
              </div>
              <v-avatar color="green-lighten-5" size="52" rounded="lg">
                <v-icon color="success" size="26">mdi-package-variant-closed</v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card" elevation="2" rounded="lg" :class="{ 'border-error': articleStats.lowStockCount > 0 }">
          <v-card-text class="pa-5">
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="stat-label">Stock Critique</div>
                <div class="stat-value" :class="articleStats.lowStockCount > 0 ? 'text-error' : 'text-success'">
                  {{ articleStats.lowStockCount || 0 }}
                </div>
              </div>
              <v-avatar :color="articleStats.lowStockCount > 0 ? 'red-lighten-5' : 'green-lighten-5'" size="52" rounded="lg">
                <v-icon :color="articleStats.lowStockCount > 0 ? 'error' : 'success'" size="26">
                  {{ articleStats.lowStockCount > 0 ? 'mdi-alert-circle' : 'mdi-check-circle' }}
                </v-icon>
              </v-avatar>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Low stock alert -->
    <v-alert
      v-if="articleStats.lowStockCount > 0"
      type="warning"
      variant="tonal"
      class="mt-4"
      rounded="lg"
      icon="mdi-bell-alert"
      closable
    >
      <strong>{{ articleStats.lowStockCount }} article(s)</strong> sont en stock critique.
      <v-btn
        variant="text"
        size="small"
        color="warning"
        @click="$router.push('/admin/dashboard/articles')"
        class="ml-2"
      >
        Voir les statistiques
      </v-btn>
    </v-alert>

    <v-row class="mt-4">
      <!-- Quick Actions -->
      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="pa-5 pb-3">
            <v-icon color="primary" class="mr-2">mdi-lightning-bolt</v-icon>
            <span class="text-subtitle-1 font-weight-bold">Actions Rapides</span>
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-5">
            <v-row>
              <v-col cols="12" sm="6">
                <v-btn
                  block
                  color="primary"
                  variant="tonal"
                  prepend-icon="mdi-account-plus"
                  @click="$router.push('/admin/dashboard/users')"
                  class="mb-2"
                >
                  Gérer les Utilisateurs
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                  block
                  color="success"
                  variant="tonal"
                  prepend-icon="mdi-chart-bar"
                  @click="$router.push('/admin/dashboard/articles')"
                  class="mb-2"
                >
                  Voir les Statistiques
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                  block
                  color="blue"
                  variant="tonal"
                  prepend-icon="mdi-desktop-classic"
                  @click="$router.push('/admin/articles/ordinateurs')"
                  class="mb-2"
                >
                  Ordinateurs
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6">
                <v-btn
                  block
                  color="teal"
                  variant="tonal"
                  prepend-icon="mdi-package-variant"
                  @click="$router.push('/admin/articles/consommables')"
                  class="mb-2"
                >
                  Consommables
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Top categories summary -->
      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="pa-5 pb-3">
            <v-icon color="secondary" class="mr-2">mdi-chart-donut</v-icon>
            <span class="text-subtitle-1 font-weight-bold">Résumé par Catégorie</span>
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text class="pa-4" style="max-height:260px; overflow-y:auto">
            <div class="d-flex flex-column gap-2">
              <div
                v-for="cat in topCategories"
                :key="cat.type"
                class="d-flex align-center justify-space-between pa-2 rounded hover-item mb-1"
                @click="$router.push(cat.route)"
                style="cursor:pointer; background: rgba(0,0,0,0.02);"
              >
                <div class="d-flex align-center">
                  <v-avatar :color="cat.color + '-lighten-5'" size="32" rounded class="mr-3">
                    <v-icon :color="cat.color" size="16">{{ cat.icon }}</v-icon>
                  </v-avatar>
                  <span class="text-body-2 font-weight-medium">{{ cat.label }}</span>
                </div>
                <v-chip :color="cat.color" size="small" variant="tonal" class="font-weight-bold">
                  {{ articleStats.countByType?.[cat.type] || 0 }}
                </v-chip>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import UserService from '@/services/UserService'
import ArticleService from '@/services/ArticleService'

export default {
  data() {
    return {
      totalUsers: 0,
      adminUsers: 0,
      regularUsers: 0,
      currentUser: {},
      articleStats: {
        totalArticles: 0,
        lowStockCount: 0,
        countByType: {}
      },
      topCategories: [
        { label: 'Ordinateurs', type: 'ORDINATEUR', route: '/admin/articles/ordinateurs', icon: 'mdi-desktop-classic', color: 'blue' },
        { label: 'Moniteurs', type: 'MONITEUR', route: '/admin/articles/moniteurs', icon: 'mdi-monitor', color: 'indigo' },
        { label: 'Périphériques', type: 'PERIPHERIQUE', route: '/admin/articles/peripheriques', icon: 'mdi-usb', color: 'purple' },
        { label: 'Imprimantes', type: 'IMPRIMANTE', route: '/admin/articles/imprimantes', icon: 'mdi-printer', color: 'teal' },
        { label: 'Téléphones', type: 'TELEPHONE', route: '/admin/articles/telephones', icon: 'mdi-phone', color: 'green' },
        { label: 'Cartouches', type: 'CARTOUCHE', route: '/admin/articles/cartouches', icon: 'mdi-printer-pos', color: 'orange' },
        { label: 'Consommables', type: 'CONSOMMABLE', route: '/admin/articles/consommables', icon: 'mdi-package-variant', color: 'amber' }
      ]
    }
  },
  computed: {
    today() {
      return new Date().toLocaleDateString('fr-FR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
    }
  },
  mounted() {
    this.currentUser = JSON.parse(localStorage.getItem('user') || '{}')
    this.loadStatistics()
    this.loadArticleStats()
  },
  methods: {
    async loadStatistics() {
      try {
        const response = await UserService.getAllUsers()
        const users = response.data || []
        this.totalUsers = users.length
        this.adminUsers = users.filter(u => u.role === 'admin').length
        this.regularUsers = users.filter(u => u.role === 'user').length
      } catch (error) {
        console.error('Error loading user statistics:', error)
      }
    },
    async loadArticleStats() {
      try {
        const response = await ArticleService.getArticleStats()
        this.articleStats = response.data || {}
      } catch (error) {
        console.error('Error loading article stats:', error)
      }
    }
  }
}
</script>

<style scoped>
.admin-overview {
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
</style>
