<template>
  <v-layout class="responsable-dashboard">
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
          <div class="text-orange-lighten-3" style="font-size:11px">Responsable Opérationel</div>
        </div>
      </div>

      <v-divider color="rgba(255,255,255,0.1)" class="mb-2"></v-divider>

      <!-- User info -->
      <div class="px-4 py-3 d-flex align-center">
        <v-avatar color="orange-darken-1" size="36" class="mr-3">
          <v-icon color="white" size="20">mdi-account</v-icon>
        </v-avatar>
        <div>
          <div class="text-white text-caption font-weight-medium">{{ currentUser.name || 'Responsable' }}</div>
          <v-chip color="orange" size="x-small" variant="elevated" class="mt-1">Responsable</v-chip>
        </div>
      </div>

      <v-divider color="rgba(255,255,255,0.1)" class="mb-2"></v-divider>

      <v-list density="compact" nav class="px-2">

        <!-- Overview -->
        <div class="sidebar-group-label text-orange-lighten-3 text-caption font-weight-bold px-3 py-2">
          TABLEAU DE BORD
        </div>

        <v-list-item
          @click="navigateTo('/responsable/overview')"
          :active="isActive('/responsable/overview')"
          active-color="orange"
          rounded="lg"
          class="mb-1"
          prepend-icon="mdi-view-dashboard"
          title="Vue d'ensemble"
        ></v-list-item>

        <v-divider color="rgba(255,255,255,0.1)" class="my-3"></v-divider>

        <!-- Approvisionnement group -->
        <div class="sidebar-group-label text-orange-lighten-3 text-caption font-weight-bold px-3 py-2">
          APPROVISIONNEMENT
        </div>

        <v-list-item
          @click="navigateTo('/responsable/supply/purchase-orders')"
          :active="isActive('/responsable/supply/purchase-orders')"
          active-color="orange"
          rounded="lg"
          class="mb-1"
          prepend-icon="mdi-clipboard-list"
          title="Bons de Commande"
        ></v-list-item>

        <v-list-item
          @click="navigateTo('/responsable/supply/suppliers')"
          :active="isActive('/responsable/supply/suppliers')"
          active-color="orange"
          rounded="lg"
          class="mb-1"
          prepend-icon="mdi-warehouse"
          title="Fournisseurs"
        ></v-list-item>

        <v-divider color="rgba(255,255,255,0.1)" class="my-3"></v-divider>

        <!-- Analytique group -->
        <div class="sidebar-group-label text-orange-lighten-3 text-caption font-weight-bold px-3 py-2">
          ANALYTIQUE
        </div>

        <v-list-item
          @click="navigateTo('/responsable/analytics/anomalies')"
          :active="isActive('/responsable/analytics/anomalies')"
          active-color="orange"
          rounded="lg"
          class="mb-1"
          prepend-icon="mdi-alert-circle"
          title="Détection d'Anomalies"
        ></v-list-item>

        <v-list-item
          @click="navigateTo('/responsable/analytics/optimization')"
          :active="isActive('/responsable/analytics/optimization')"
          active-color="orange"
          rounded="lg"
          class="mb-1"
          prepend-icon="mdi-lightbulb-on"
          title="Prévisions & Optimisation"
        ></v-list-item>

        <v-divider color="rgba(255,255,255,0.1)" class="my-3"></v-divider>

        <v-list-item
          @click="logout"
          rounded="lg"
          class="mt-2"
        >
          <template v-slot:prepend>
            <v-icon color="error">mdi-logout</v-icon>
          </template>
          <div class="text-error font-weight-bold ml-2">Déconnexion</div>
        </v-list-item>

      </v-list>
    </v-navigation-drawer>

    <!-- App Bar -->
    <v-app-bar color="white" elevation="1" class="dashboard-bar">
      <v-app-bar-nav-icon @click="drawer = !drawer" color="grey-darken-2"></v-app-bar-nav-icon>

      <v-breadcrumbs :items="breadcrumbs" class="pa-0 ml-2" density="compact">
        <template v-slot:divider>
          <v-icon size="small" color="grey">mdi-chevron-right</v-icon>
        </template>
      </v-breadcrumbs>

      <v-spacer></v-spacer>

      <v-menu transition="slide-y-transition" :close-on-content-click="false">
        <template v-slot:activator="{ props }">
          <v-btn v-bind="props" variant="text" class="px-2 mr-2" rounded="pill" style="text-transform: none;">
            <v-avatar color="orange-darken-1" size="34" class="mr-2" style="border: 2px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.1)">
              <span class="text-caption font-weight-bold text-white">{{ (currentUser.name || 'R').charAt(0).toUpperCase() }}</span>
            </v-avatar>
            <span class="text-body-2 text-grey-darken-3 font-weight-medium d-none d-sm-flex mr-1">{{ currentUser.name || 'Responsable' }}</span>
            <v-icon size="18" color="grey">mdi-chevron-down</v-icon>
          </v-btn>
        </template>

        <v-card min-width="260" rounded="xl" elevation="4" class="mt-2" border>
          <div class="pa-4 bg-orange-darken-1 text-white" style="position: relative; overflow: hidden;">
            <div class="d-flex align-center position-relative" style="z-index: 1;">
              <v-avatar color="white" size="48" class="mr-3" style="box-shadow: 0 4px 12px rgba(0,0,0,0.15)">
                <span class="text-h6 font-weight-black text-orange-darken-1">{{ (currentUser.name || 'R').charAt(0).toUpperCase() }}</span>
              </v-avatar>
              <div>
                <div class="text-subtitle-1 font-weight-bold" style="line-height: 1.2;">{{ currentUser.name || 'Responsable' }}</div>
                <div class="text-caption" style="opacity: 0.85;">{{ currentUser.email || '' }}</div>
              </div>
            </div>
            <div style="position: absolute; right: -20px; top: -30px; width: 100px; height: 100px; border-radius: 50%; background: rgba(255,255,255,0.1);"></div>
          </div>
          <v-list density="compact" class="pa-2">
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
export default {
  data() {
    return {
      drawer: true,
      logoError: false,
      currentUser: {}
    }
  },
  computed: {
    breadcrumbs() {
      const path = this.$route.path
      const crumbs = [{ title: 'Accueil', disabled: false, href: '/responsable/overview' }]
      if (path.includes('/supply/purchase-orders')) crumbs.push({ title: 'Bons de Commande', disabled: true })
      else if (path.includes('/supply/suppliers')) crumbs.push({ title: 'Fournisseurs', disabled: true })
      else if (path.includes('/analytics/anomalies')) crumbs.push({ title: 'Anomalies', disabled: true })
      else if (path.includes('/analytics/optimization')) crumbs.push({ title: 'Optimisation', disabled: true })
      return crumbs
    }
  },
  mounted() {
    const token = localStorage.getItem('jwt_token')
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!token || !user.id || user.role !== 'responsable') {
      this.$router.push('/login')
      return
    }
    this.currentUser = user
  },
  methods: {
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
.responsable-dashboard {
  min-height: 100vh;
}
.sidebar-brand {
  background: rgba(255, 255, 255, 0.04);
}
.sidebar-group-label {
  text-transform: uppercase;
  letter-spacing: 1px;
  opacity: 0.6;
}
.dashboard-bar {
  border-bottom: 1px solid #e0e0e0 !important;
}
:deep(.v-list-item--active) {
  background: rgba(255, 152, 0, 0.2) !important;
}
:deep(.v-list-item__prepend .v-icon) {
  color: rgba(255, 255, 255, 0.6) !important;
}
:deep(.v-list-item--active .v-list-item__prepend .v-icon) {
  color: #ffcc80 !important;
}
:deep(.v-list-item-title) {
  color: rgba(255, 255, 255, 0.82) !important;
  font-size: 13.5px !important;
}
:deep(.v-list-item--active .v-list-item-title) {
  color: white !important;
  font-weight: 600 !important;
}
.hover-logout {
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.hover-logout:hover {
  background-color: #fef2f2 !important;
}
</style>
