<template>
  <div class="admin-overview">
    <h1 class="text-h4 mb-6">Bienvenue dans le tableau de bord admin</h1>
    
    <v-row>
      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card">
          <v-card-text>
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-label">Utilisateurs totaux</div>
            <v-icon color="primary" size="40" class="mt-2">mdi-account-multiple</v-icon>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card">
          <v-card-text>
            <div class="stat-value">{{ adminUsers }}</div>
            <div class="stat-label">Administrateurs</div>
            <v-icon color="error" size="40" class="mt-2">mdi-shield-account</v-icon>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" sm="6" md="3">
        <v-card class="stat-card">
          <v-card-text>
            <div class="stat-value">{{ regularUsers }}</div>
            <div class="stat-label">Utilisateurs normaux</div>
            <v-icon color="success" size="40" class="mt-2">mdi-account</v-icon>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-6">
      <v-col cols="12">
        <v-card elevation="0">
          <v-card-title>Actions rapides</v-card-title>
          <v-card-text>
            <div class="d-flex gap-2 flex-wrap">
              <v-btn
                color="primary"
                prepend-icon="mdi-account-plus"
                @click="$router.push('/admin/dashboard/users')"
              >
                Ajouter un utilisateur
              </v-btn>
              <v-btn
                color="secondary"
                prepend-icon="mdi-account-multiple"
                @click="$router.push('/admin/dashboard/users')"
              >
                Gérer les utilisateurs
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import UserService from '@/services/UserService'

export default {
  data() {
    return {
      users: [],
      totalUsers: 0,
      adminUsers: 0,
      regularUsers: 0
    }
  },
  mounted() {
    this.loadStatistics();
  },
  methods: {
    async loadStatistics() {
      try {
        const response = await UserService.getAllUsers();
        this.users = response.data;
        this.totalUsers = this.users.length;
        this.adminUsers = this.users.filter(u => u.role === 'admin').length;
        this.regularUsers = this.users.filter(u => u.role === 'user').length;
      } catch (error) {
        console.error('Error loading statistics:', error);
      }
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
  border-top: 4px solid #1976d2;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
