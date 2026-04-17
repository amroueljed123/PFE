<template>
  <v-layout class="admin-dashboard">
    <v-navigation-drawer
      v-model="drawer"
      color="#1a2540"
      width="270"
      permanent
    >
      <!-- Logo & Brand -->
      <div class="sidebar-brand pa-4 d-flex align-center">
        <img
          :src="getImageUrl('logo.jpg')"
          alt="ETAP"
          style="height:38px;width:auto;object-fit:contain;filter:brightness(0) invert(1);margin-right:12px"
          @error="logoError = true"
          v-if="!logoError"
        />
        <div>
          <div class="text-white font-weight-bold" style="font-size:16px">ETAP</div>
          <div class="text-blue-lighten-3" style="font-size:11px">Gestion du Parc</div>
        </div>
      </div>

      <v-divider color="rgba(255,255,255,0.1)" class="mb-2"></v-divider>

      <!-- User info -->
      <div class="px-4 py-3 d-flex align-center">
        <v-avatar color="primary" size="36" class="mr-3">
          <v-icon color="white" size="20">mdi-account</v-icon>
        </v-avatar>
        <div>
          <div class="text-white text-caption font-weight-medium">{{ currentUser.name || 'Administrateur' }}</div>
          <v-chip color="primary" size="x-small" variant="elevated" class="mt-1">Admin</v-chip>
        </div>
      </div>

      <v-divider color="rgba(255,255,255,0.1)" class="mb-2"></v-divider>

      <div class="sidebar-nav-sections px-2">

        <!-- ═══ STANDALONE DASHBOARD ═══ -->
        <v-list-item
          @click="navigateTo('/admin/dashboard/overview')"
          :active="isActive('/admin/dashboard/overview')"
          active-color="primary"
          rounded="lg"
          class="mb-2 mx-1 nav-standalone-item"
          prepend-icon="mdi-view-dashboard"
          title="Tableau de bord"
        ></v-list-item>

        <!-- ═══ INVENTAIRE & CATALOGUE ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.catalogue }">
          <div class="nav-section-header" @click="toggleSection('catalogue')">
            <v-icon size="18" class="nav-section-icon">mdi-archive</v-icon>
            <span class="nav-section-title">Inventaire & Catalogue</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.catalogue }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.catalogue ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/catalogue')"
              :active="isActive('/admin/catalogue')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-view-grid"
              title="Catalogue des Articles"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ APPROVISIONNEMENT ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.supply }">
          <div class="nav-section-header" @click="toggleSection('supply')">
            <v-icon size="18" class="nav-section-icon">mdi-truck-delivery</v-icon>
            <span class="nav-section-title">Approvisionnement</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.supply }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.supply ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/supply-chain/suppliers')"
              :active="isActive('/admin/supply-chain/suppliers')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-warehouse"
              title="Fournisseurs"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/supply-chain/purchase-orders')"
              :active="isActive('/admin/supply-chain/purchase-orders')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-clipboard-list"
              title="Bons de commande"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ OPÉRATIONS ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.operations }">
          <div class="nav-section-header" @click="toggleSection('operations')">
            <v-icon size="18" class="nav-section-icon">mdi-cog-transfer</v-icon>
            <span class="nav-section-title">Opérations</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.operations }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.operations ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/operations/inventory')"
              :active="isActive('/admin/operations/inventory')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-database"
              title="Inventaire Physique"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/operations/mouvements')"
              :active="isActive('/admin/operations/mouvements')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-swap-horizontal-bold"
              title="Entrées / Sorties"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/operations/demandes-validation')"
              :active="isActive('/admin/operations/demandes-validation')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-check-decagram"
              title="Validation Demandes"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/operations/maintenance')"
              :active="isActive('/admin/operations/maintenance')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-wrench"
              title="Maintenance"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/vehicles/management')"
              :active="isActive('/admin/vehicles/management')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-truck"
              title="Véhicules"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ COMPTABILITÉ ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.finance }">
          <div class="nav-section-header" @click="toggleSection('finance')">
            <v-icon size="18" class="nav-section-icon">mdi-calculator-variant</v-icon>
            <span class="nav-section-title">Comptabilité</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.finance }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.finance ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/finance/invoices')"
              :active="isActive('/admin/finance/invoices')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-receipt"
              title="Factures"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ ANALYTIQUE ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.analytics }">
          <div class="nav-section-header" @click="toggleSection('analytics')">
            <v-icon size="18" class="nav-section-icon">mdi-chart-areaspline</v-icon>
            <span class="nav-section-title">Analytique</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.analytics }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.analytics ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/dashboard/articles')"
              :active="isActive('/admin/dashboard/articles')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-chart-bar"
              title="Statistiques"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/analytics/anomalies')"
              :active="isActive('/admin/analytics/anomalies')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-alert-circle"
              title="Anomalies"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/analytics/optimization')"
              :active="isActive('/admin/analytics/optimization')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-lightbulb-on"
              title="Optimisation"
            ></v-list-item>
            <v-list-item
              @click="navigateTo('/admin/analytics/audit')"
              :active="isActive('/admin/analytics/audit')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-history"
              title="Audit"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ ADMINISTRATION ═══ -->
        <div class="nav-section" :class="{ 'nav-section--open': sections.admin }">
          <div class="nav-section-header" @click="toggleSection('admin')">
            <v-icon size="18" class="nav-section-icon">mdi-shield-crown</v-icon>
            <span class="nav-section-title">Administration</span>
            <v-icon size="18" class="nav-section-chevron" :class="{ 'rotate-180': sections.admin }">mdi-chevron-down</v-icon>
          </div>
          <div class="nav-section-items" :style="{ maxHeight: sections.admin ? '300px' : '0' }">
            <v-list-item
              @click="navigateTo('/admin/dashboard/users')"
              :active="isActive('/admin/dashboard/users')"
              active-color="primary"
              rounded="lg"
              class="nav-sub-item"
              prepend-icon="mdi-account-group"
              title="Utilisateurs"
            ></v-list-item>
          </div>
        </div>

        <!-- ═══ DÉCONNEXION ═══ -->
        <div class="nav-section nav-section-logout">
          <v-list-item
            @click="logout"
            rounded="lg"
            class="mt-1"
          >
            <template v-slot:prepend>
              <v-icon color="error">mdi-logout</v-icon>
            </template>
            <div class="text-error font-weight-bold ml-2">Déconnexion</div>
          </v-list-item>
        </div>

      </div>
    </v-navigation-drawer>

    <!-- App Bar -->
    <v-app-bar color="white" elevation="1" class="admin-bar">
      <v-app-bar-nav-icon @click="drawer = !drawer" color="grey-darken-2"></v-app-bar-nav-icon>

      <!-- Breadcrumb -->
      <v-breadcrumbs :items="breadcrumbs" class="pa-0 ml-2" density="compact">
        <template v-slot:divider>
          <v-icon size="small" color="grey">mdi-chevron-right</v-icon>
        </template>
      </v-breadcrumbs>

      <v-spacer></v-spacer>

      <!-- Low stock alert -->
      <v-btn
        icon
        @click="navigateTo('/admin/dashboard/articles')"
        class="mr-2"
        v-tooltip="lowStockCount > 0 ? `${lowStockCount} articles en stock bas` : 'Aucune alerte'"
      >
        <v-badge :content="lowStockCount" color="error" v-if="lowStockCount > 0">
          <v-icon color="warning">mdi-bell-alert</v-icon>
        </v-badge>
        <v-icon v-else color="grey-darken-2">mdi-bell</v-icon>
      </v-btn>

      <!-- User menu -->
      <v-menu transition="slide-y-transition" :close-on-content-click="false">
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" variant="text" class="px-2 mr-2" rounded="pill" style="text-transform: none;">
            <v-avatar color="primary" size="34" class="mr-2" style="border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.1)">
              <span class="text-caption font-weight-bold text-white">{{ (currentUser.name || 'A').charAt(0).toUpperCase() }}</span>
            </v-avatar>
            <span class="text-body-2 text-grey-darken-3 font-weight-medium d-none d-sm-flex mr-1">{{ currentUser.name || 'Admin' }}</span>
            <v-icon size="18" color="grey">mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-card min-width="260" rounded="xl" elevation="4" class="mt-2" border>
          <!-- User Profile Header -->
          <div class="pa-4 bg-primary text-white" style="position: relative; overflow: hidden;">
            <div class="d-flex align-center position-relative" style="z-index: 1;">
              <v-avatar color="white" size="48" class="mr-3" style="box-shadow: 0 4px 12px rgba(0,0,0,0.15)">
                <span class="text-h6 font-weight-black text-primary">{{ (currentUser.name || 'A').charAt(0).toUpperCase() }}</span>
              </v-avatar>
              <div>
                <div class="text-subtitle-1 font-weight-bold" style="line-height: 1.2;">{{ currentUser.name || 'Administrateur' }}</div>
                <div class="text-caption" style="opacity: 0.85;">{{ currentUser.email || 'admin@etap.com' }}</div>
              </div>
            </div>
            <!-- Decorative circle -->
            <div style="position: absolute; right: -20px; top: -30px; width: 100px; height: 100px; border-radius: 50%; background: rgba(255,255,255,0.1);"></div>
          </div>

          <!-- Menu Options -->
          <v-list density="compact" class="pa-2">
            <v-list-item color="primary" class="rounded-lg mb-1" @click="() => {}">
              <template v-slot:prepend>
                <v-icon color="grey-darken-1" size="20">mdi-account-cog</v-icon>
              </template>
              <div class="text-body-2 font-weight-medium ml-3 text-grey-darken-3">Paramètres du compte</div>
            </v-list-item>

            <v-divider class="my-2"></v-divider>

            <v-list-item @click="logout" class="rounded-lg hover-logout">
              <template v-slot:prepend>
                <v-icon color="error" size="20">mdi-logout-variant</v-icon>
              </template>
              <div class="text-error font-weight-bold ml-3" style="font-size: 14px;">Déconnexion</div>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>
    </v-app-bar>

    <v-main style="background:#f4f6fb; min-height:100vh;">
      <v-container fluid class="pa-6">
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-layout>
</template>

<script>
import ArticleService from '@/services/ArticleService'

export default {
  data() {
    return {
      drawer: true,
      logoError: false,
      currentUser: {},
      lowStockCount: 0,
      sections: {
        admin: true,
        catalogue: false,
        supply: false,
        operations: false,
        finance: false,
        analytics: false
      }
    }
  },
  computed: {
    breadcrumbs() {
      const path = this.$route.path
      const crumbs = [{ title: 'Accueil', disabled: false, href: '/admin/dashboard/overview' }]
      if (path.includes('/dashboard/users')) {
        crumbs.push({ title: 'Utilisateurs', disabled: true })
      } else if (path.includes('/dashboard/articles')) {
        crumbs.push({ title: 'Statistiques', disabled: true })
      } else if (path.includes('/articles/')) {
        crumbs.push({ title: 'Articles', disabled: false, href: '/admin/dashboard/articles' })
        const labels = {
          consommables: 'Consommables', cartouches: 'Cartouches', meubles: 'Meubles',
          bureaux: 'Bureaux', chaises: 'Chaises', etageres: 'Étagères',
          imprimantes: 'Imprimantes', ordinateurs: 'Ordinateurs',
          moniteurs: 'Moniteurs', peripheriques: 'Périphériques', telephones: 'Téléphones'
        }
        const cat = path.split('/').pop()
        if (labels[cat]) crumbs.push({ title: labels[cat], disabled: true })
      } else if (path.includes('/supply-chain/')) {
        crumbs.push({ title: 'Approvisionnement', disabled: true })
        if (path.includes('suppliers')) crumbs.push({ title: 'Fournisseurs', disabled: true })
        if (path.includes('purchase-orders')) crumbs.push({ title: 'Bons de commande', disabled: true })
      } else if (path.includes('/vehicles/')) {
        crumbs.push({ title: 'Véhicules', disabled: true })
      } else if (path.includes('/operations/')) {
        crumbs.push({ title: 'Opérations', disabled: true })
        if (path.includes('inventory')) crumbs.push({ title: 'Inventaire Physique', disabled: true })
        if (path.includes('maintenance')) crumbs.push({ title: 'Maintenance', disabled: true })
        if (path.includes('demandes-validation')) crumbs.push({ title: 'Validation Demandes', disabled: true })
      } else if (path.includes('/finance/')) {
        crumbs.push({ title: 'Comptabilité', disabled: true })
        if (path.includes('invoices')) crumbs.push({ title: 'Factures', disabled: true })
      } else if (path.includes('/analytics/')) {
        crumbs.push({ title: 'Analytique', disabled: true })
        if (path.includes('anomalies')) crumbs.push({ title: 'Anomalies', disabled: true })
        if (path.includes('optimization')) crumbs.push({ title: 'Optimisation', disabled: true })
        if (path.includes('audit')) crumbs.push({ title: 'Audit', disabled: true })
      }
      return crumbs
    }
  },
  mounted() {
    const token = localStorage.getItem('jwt_token')
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!token || !user.id || user.role !== 'admin') {
      this.$router.push('/login')
      return
    }
    this.currentUser = user
    this.loadLowStockCount()
  },
  methods: {
    async loadLowStockCount() {
      try {
        const response = await ArticleService.getArticleStats()
        this.lowStockCount = response.data?.lowStockCount || 0
      } catch (e) {
        // silent
      }
    },
    toggleSection(key) {
      this.sections[key] = !this.sections[key]
    },
    navigateTo(route) {
      this.$router.push(route)
    },
    isActive(route) {
      return this.$route.path === route || this.$route.path.startsWith(route + '/')
    },
    logout() {
      localStorage.removeItem('jwt_token')
      localStorage.removeItem('user')
      localStorage.removeItem('isAuthenticated')
      this.$router.push('/')
    },
    getImageUrl(name) {
      try {
        return new URL(`../assets/${name}`, import.meta.url).href
      } catch {
        return ''
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
}

.sidebar-brand {
  background: rgba(255, 255, 255, 0.04);
}

.admin-bar {
  border-bottom: 1px solid #e0e0e0 !important;
}

/* ── Collapsible Sections ── */
.sidebar-nav-sections {
  padding-top: 4px;
}

.nav-section {
  margin-bottom: 4px;
  border-radius: 12px;
  transition: background 0.25s ease;
}

.nav-section--open {
  background: rgba(255, 255, 255, 0.04);
}

.nav-section-header {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  cursor: pointer;
  border-radius: 10px;
  user-select: none;
  transition: background 0.2s ease;
}

.nav-section-header:hover {
  background: rgba(255, 255, 255, 0.08);
}

.nav-section--open > .nav-section-header {
  background: rgba(25, 118, 210, 0.12);
}

.nav-section-icon {
  color: rgba(144, 202, 249, 0.7) !important;
  margin-right: 12px;
  flex-shrink: 0;
}

.nav-section--open > .nav-section-header .nav-section-icon {
  color: #90caf9 !important;
}

.nav-section-title {
  flex: 1;
  font-size: 12.5px;
  font-weight: 700;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.55);
  transition: color 0.2s ease;
}

.nav-section--open > .nav-section-header .nav-section-title {
  color: rgba(255, 255, 255, 0.9);
}

.nav-section-chevron {
  color: rgba(255, 255, 255, 0.3) !important;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), color 0.2s ease;
  flex-shrink: 0;
}

.nav-section--open > .nav-section-header .nav-section-chevron {
  color: rgba(144, 202, 249, 0.8) !important;
}

.rotate-180 {
  transform: rotate(180deg);
}

/* ── Collapsible Items Container ── */
.nav-section-items {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  padding-left: 8px;
  border-left: 2px solid transparent;
  margin-left: 22px;
}

.nav-section--open .nav-section-items {
  border-left-color: rgba(25, 118, 210, 0.4);
}

.nav-standalone-item {
  color: rgba(255, 255, 255, 0.8) !important;
}

:deep(.nav-standalone-item .v-list-item__prepend .v-icon) {
  color: rgba(144, 202, 249, 0.8) !important;
}

:deep(.nav-standalone-item.v-list-item--active) {
  background: rgba(25, 118, 210, 0.25) !important;
  color: white !important;
}

.nav-sub-item {
  margin-bottom: 2px !important;
  min-height: 38px !important;
}

:deep(.nav-sub-item .v-list-item__prepend .v-icon) {
  color: rgba(255, 255, 255, 0.45) !important;
  font-size: 18px !important;
}

:deep(.nav-sub-item.v-list-item--active .v-list-item__prepend .v-icon) {
  color: #90caf9 !important;
}

:deep(.nav-sub-item .v-list-item-title) {
  color: rgba(255, 255, 255, 0.7) !important;
  font-size: 13px !important;
}

:deep(.nav-sub-item.v-list-item--active .v-list-item-title) {
  color: white !important;
  font-weight: 600 !important;
}

:deep(.nav-sub-item.v-list-item--active) {
  background: rgba(25, 118, 210, 0.18) !important;
}

/* ── Logout ── */
.nav-section-logout {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.hover-logout {
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.hover-logout:hover {
  background-color: #fef2f2 !important;
}
</style>
