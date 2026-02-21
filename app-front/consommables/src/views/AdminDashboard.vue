<template>
  <v-layout class="admin-dashboard">
    <v-navigation-drawer
      v-model="drawer"
      dark
      color="primary"
      width="280"
    >
      <v-list-item class="px-0">
        <template v-slot:prepend>
          <img :src="getImageUrl('logo.jpg')" alt="ETAP" style="height:36px;width:auto;object-fit:contain;filter:brightness(0) invert(1);margin-right:12px" />
        </template>
        <v-list-item-title class="font-weight-bold">ETAP Admin</v-list-item-title>
      </v-list-item>

      <v-divider class="my-4"></v-divider>

      <v-list nav>
        <!-- Administration Menu Button -->
        <v-list-item
          @click.stop="administrationExpanded = !administrationExpanded"
          class="mb-2"
        >
          <template v-slot:prepend>
            <v-icon>mdi-cog</v-icon>
          </template>
          <v-list-item-title class="font-weight-bold">Administration</v-list-item-title>
          <template v-slot:append>
            <v-icon v-show="administrationExpanded">mdi-chevron-up</v-icon>
            <v-icon v-show="!administrationExpanded">mdi-chevron-down</v-icon>
          </template>
        </v-list-item>

        <!-- Nested Administration Items -->
        <v-slide-y-transition>
          <div v-if="administrationExpanded" class="administration-submenu">
            <v-list-item
              @click.stop="navigateTo('/admin/dashboard/overview')"
              :active="isActive('/admin/dashboard/overview')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-home</v-icon>
              </template>
              <v-list-item-title>Accueil</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/dashboard/users')"
              :active="isActive('/admin/dashboard/users')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-account-multiple</v-icon>
              </template>
              <v-list-item-title>Utilisateurs</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/dashboard/articles')"
              :active="isActive('/admin/dashboard/articles')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-chart-box</v-icon>
              </template>
              <v-list-item-title>Stats Articles</v-list-item-title>
            </v-list-item>
          </div>
        </v-slide-y-transition>
      </v-list>

      <v-divider class="my-4"></v-divider>

      <v-list nav>
        <!-- Categories Menu Button -->
        <v-list-item
          @click.stop="categoriesExpanded = !categoriesExpanded"
          class="mb-2"
        >
          <template v-slot:prepend>
            <v-icon>mdi-tag-multiple</v-icon>
          </template>
          <v-list-item-title class="font-weight-bold">Catégories</v-list-item-title>
          <template v-slot:append>
            <v-icon v-show="categoriesExpanded">mdi-chevron-up</v-icon>
            <v-icon v-show="!categoriesExpanded">mdi-chevron-down</v-icon>
          </template>
        </v-list-item>

        <!-- Nested Category Items -->
        <v-slide-y-transition>
          <div v-if="categoriesExpanded" class="categories-submenu">
            <v-list-item
              @click.stop="navigateTo('/admin/articles/consommables')"
              :active="isActive('/admin/articles/consommables')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Consommables</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/cartouches')"
              :active="isActive('/admin/articles/cartouches')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Cartouches</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/meubles')"
              :active="isActive('/admin/articles/meubles')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Meubles</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/bureaux')"
              :active="isActive('/admin/articles/bureaux')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Bureaux</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/chaises')"
              :active="isActive('/admin/articles/chaises')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Chaises</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/etageres')"
              :active="isActive('/admin/articles/etageres')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Étagères</v-list-item-title>
            </v-list-item>

            <v-list-item
              @click.stop="navigateTo('/admin/articles/imprimantes')"
              :active="isActive('/admin/articles/imprimantes')"
              class="pl-10 mb-1"
            >
              <template v-slot:prepend>
                <v-icon small class="mr-2">mdi-folder</v-icon>
              </template>
              <v-list-item-title>Imprimantes</v-list-item-title>
            </v-list-item>
          </div>
        </v-slide-y-transition>
      </v-list>

      <v-divider class="my-4"></v-divider>

      <v-list nav>
        <v-list-item @click.stop="logout">
          <template v-slot:prepend>
            <v-icon>mdi-logout</v-icon>
          </template>
          <v-list-item-title>Déconnexion</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="primary" dark flat class="admin-bar">
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>ETAP Admin Dashboard</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn icon v-bind="props">
            <v-icon>mdi-account-circle</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="logout">
            <v-list-item-title>
              <v-icon start size="small">mdi-logout</v-icon>
              Déconnexion
            </v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main>
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
      administrationExpanded: false,
      categoriesExpanded: false
    }
  },
  mounted() {
    // Check if user has valid JWT token and is admin
    const token = localStorage.getItem('jwt_token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    
    if (!token || !user.id || user.role !== 'admin') {
      this.$router.push('/login');
    }
  },
  methods: {
    navigateTo(route) {
      this.$router.push(route);
    },
    isActive(route) {
      return this.$route.path === route;
    },
    logout() {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user');
      localStorage.removeItem('isAuthenticated');
      this.$router.push('/');
    },
    getImageUrl(name) {
      return new URL(`../assets/${name}`, import.meta.url).href;
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-bar {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.administration-submenu {
  background: rgba(0, 0, 0, 0.1);
  border-left: 3px solid #00d4ff;
  margin: 5px 0;
}

.categories-submenu {
  background: rgba(0, 102, 255, 0.05);
  border-left: 3px solid #0066ff;
  margin: 5px 0;
}
</style>