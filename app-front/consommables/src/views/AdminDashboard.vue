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
          <v-icon size="40" class="mr-3">mdi-shield-admin</v-icon>
        </template>
        <v-list-item-title class="font-weight-bold">Admin Panel</v-list-item-title>
      </v-list-item>

      <v-divider class="my-4"></v-divider>

      <v-list nav>
        <v-list-item
          @click.stop="navigateTo('/admin/dashboard/overview')"
          :active="isActive('/admin/dashboard/overview')"
          class="mb-2"
        >
          <template v-slot:prepend>
            <v-icon>mdi-home</v-icon>
          </template>
          <v-list-item-title>Accueil</v-list-item-title>
        </v-list-item>

        <v-list-item
          @click.stop="navigateTo('/admin/dashboard/users')"
          :active="isActive('/admin/dashboard/users')"
          class="mb-2"
        >
          <template v-slot:prepend>
            <v-icon>mdi-account-multiple</v-icon>
          </template>
          <v-list-item-title>Utilisateurs</v-list-item-title>
        </v-list-item>
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
      <v-toolbar-title>Admin Dashboard</v-toolbar-title>
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
      drawer: true
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
</style>