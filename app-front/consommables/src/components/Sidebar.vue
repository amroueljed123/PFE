<template>
  <v-navigation-drawer
    v-model="drawer"
    app
    dark
    color="primary"
    width="280"
    class="admin-sidebar"
  >
    <v-list-item class="px-0">
      <template v-slot:prepend>
        <v-icon size="40" class="mr-3">mdi-shield-admin</v-icon>
      </template>
      <v-list-item-title class="font-weight-bold">Admin Panel</v-list-item-title>
    </v-list-item>

    <v-divider class="my-4"></v-divider>

    <v-list nav>
      <!-- Administration Menu Button -->
      <v-list-item
        @click="administrationExpanded = !administrationExpanded"
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
            @click="navigateTo('/admin/dashboard/overview')"
            :active="isActive('/admin/dashboard/overview')"
            class="pl-10 mb-1"
          >
            <template v-slot:prepend>
              <v-icon small class="mr-2">mdi-home</v-icon>
            </template>
            <v-list-item-title>Accueil</v-list-item-title>
          </v-list-item>

          <v-list-item
            @click="navigateTo('/admin/dashboard/users')"
            :active="isActive('/admin/dashboard/users')"
            class="pl-10 mb-1"
          >
            <template v-slot:prepend>
              <v-icon small class="mr-2">mdi-account-multiple</v-icon>
            </template>
            <v-list-item-title>Utilisateurs</v-list-item-title>
          </v-list-item>
        </div>
      </v-slide-y-transition>
    </v-list>

    <v-divider class="my-4"></v-divider>

    <v-list nav>
      <v-list-item @click="logout" class="mt-auto">
        <template v-slot:prepend>
          <v-icon>mdi-logout</v-icon>
        </template>
        <v-list-item-title>Déconnexion</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
export default {
  data() {
    return {
      drawer: true,
      administrationExpanded: false
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
      localStorage.removeItem('user');
      localStorage.removeItem('isAuthenticated');
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.admin-sidebar {
  position: fixed !important;
  height: 100vh !important;
  z-index: 100;
}

.administration-submenu {
  background: rgba(0, 0, 0, 0.1);
  border-left: 3px solid #00d4ff;
  margin: 5px 0;
}
</style>
