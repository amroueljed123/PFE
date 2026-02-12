<template>
  <div class="users-management">
    <v-card class="mb-6" elevation="0">
      <v-card-title class="d-flex justify-space-between align-center">
        <div>
          <h2 class="text-h5 mb-2">Gestion des Utilisateurs</h2>
          <p class="text-subtitle-2 text-medium-emphasis">Gérez tous les utilisateurs du système</p>
        </div>
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="showAddDialog = true"
        >
          Ajouter un utilisateur
        </v-btn>
      </v-card-title>
    </v-card>

    <!-- Users Table -->
    <v-card elevation="0">
      <v-data-table
        :headers="headers"
        :items="users"
        :loading="loading"
        class="elevation-0"
        item-key="id"
      >
        <template v-slot:item.role="{ item }">
          <v-chip
            :color="item.role === 'admin' ? 'red' : 'blue'"
            text-color="white"
            small
          >
            {{ item.role }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn
            icon="mdi-pencil"
            size="small"
            variant="text"
            color="primary"
            @click="editUser(item)"
            class="mr-2"
          ></v-btn>
          <v-btn
            icon="mdi-delete"
            size="small"
            variant="text"
            color="error"
            @click="deleteUser(item.id)"
          ></v-btn>
        </template>

        <template v-slot:no-data>
          <div class="py-10 text-center">
            <v-icon size="60" color="grey-lighten-2" class="mb-4">mdi-account-off</v-icon>
            <p class="text-subtitle-2 text-medium-emphasis">Aucun utilisateur trouvé</p>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- Add/Edit User Dialog -->
    <v-dialog v-model="showAddDialog" max-width="500px">
      <v-card>
        <v-card-title>
          {{ editingUser ? 'Modifier l\'utilisateur' : 'Ajouter un nouvel utilisateur' }}
        </v-card-title>

        <v-card-text class="pa-6">
          <v-form ref="userForm" v-model="formValid">
            <v-text-field
              v-model="editingUser.name"
              label="Nom"
              variant="outlined"
              class="mb-4"
              :rules="[v => !!v || 'Le nom est requis']"
            ></v-text-field>

            <v-text-field
              v-model="editingUser.email"
              label="Email"
              type="email"
              variant="outlined"
              class="mb-4"
              :rules="[v => !!v || 'L\'email est requis', v => /.+@.+\..+/.test(v) || 'Email invalide']"
            ></v-text-field>

            <v-text-field
              v-model="editingUser.password"
              label="Mot de passe"
              type="password"
              variant="outlined"
              class="mb-4"
              :rules="[v => !isNewUser || (v && v.length >= 6) || 'Min 6 caractères']"
              :hint="isNewUser ? 'Requis' : 'Laissez vide pour garder le même'"
            ></v-text-field>

            <v-select
              v-model="editingUser.role"
              label="Rôle"
              :items="['admin', 'user']"
              variant="outlined"
              class="mb-4"
              :rules="[v => !!v || 'Le rôle est requis']"
            ></v-select>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn text @click="showAddDialog = false">Annuler</v-btn>
          <v-btn color="primary" @click="saveUser" :loading="savingUser">
            {{ editingUser.id ? 'Mettre à jour' : 'Créer' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="showDeleteDialog" max-width="400px">
      <v-card>
        <v-card-title>Confirmer la suppression</v-card-title>
        <v-card-text>
          Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn text @click="showDeleteDialog = false">Annuler</v-btn>
          <v-btn color="error" @click="confirmDelete" :loading="deletingUser">
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar for notifications -->
    <v-snackbar v-model="showSnackbar" :color="snackbarColor">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
import UserService from '@/services/UserService'

export default {
  data() {
    return {
      users: [],
      loading: false,
      showAddDialog: false,
      showDeleteDialog: false,
      formValid: false,
      savingUser: false,
      deletingUser: false,
      editingUser: {},
      userToDelete: null,
      showSnackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      isNewUser: false,
      headers: [
        { text: 'Nom', value: 'name', width: '25%' },
        { text: 'Email', value: 'email', width: '30%' },
        { text: 'Rôle', value: 'role', width: '15%' },
        { text: 'Actions', value: 'actions', align: 'center', width: '30%', sortable: false }
      ]
    }
  },
  mounted() {
    this.loadUsers();
  },
  methods: {
    async loadUsers() {
      this.loading = true;
      try {
        const response = await UserService.getAllUsers();
        this.users = response.data;
      } catch (error) {
        console.error('Error loading users:', error);
        this.showNotification('Erreur lors du chargement des utilisateurs', 'error');
      } finally {
        this.loading = false;
      }
    },

    editUser(user) {
      this.isNewUser = false;
      this.editingUser = { ...user, password: '' };
      this.showAddDialog = true;
    },

    async saveUser() {
      const { valid } = await this.$refs.userForm.validate();
      
      if (!valid) {
        this.showNotification('Veuillez corriger les erreurs du formulaire', 'error');
        return;
      }

      this.savingUser = true;
      try {
        if (this.editingUser.id) {
          // Update existing user
          await UserService.updateUser(this.editingUser.id, this.editingUser);
          this.showNotification('Utilisateur mis à jour avec succès', 'success');
        } else {
          // Create new user
          if (!this.editingUser.password) {
            this.showNotification('Le mot de passe est requis pour un nouvel utilisateur', 'error');
            return;
          }
          await UserService.createUser(this.editingUser);
          this.showNotification('Utilisateur créé avec succès', 'success');
        }
        
        this.showAddDialog = false;
        this.editingUser = {};
        await this.loadUsers();
      } catch (error) {
        console.error('Error saving user:', error);
        this.showNotification('Erreur lors de la sauvegarde de l\'utilisateur', 'error');
      } finally {
        this.savingUser = false;
      }
    },

    deleteUser(id) {
      this.userToDelete = id;
      this.showDeleteDialog = true;
    },

    async confirmDelete() {
      if (!this.userToDelete) return;

      this.deletingUser = true;
      try {
        await UserService.deleteUser(this.userToDelete);
        this.showNotification('Utilisateur supprimé avec succès', 'success');
        this.showDeleteDialog = false;
        this.userToDelete = null;
        await this.loadUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
        this.showNotification('Erreur lors de la suppression de l\'utilisateur', 'error');
      } finally {
        this.deletingUser = false;
      }
    },

    showNotification(message, color = 'success') {
      this.snackbarMessage = message;
      this.snackbarColor = color;
      this.showSnackbar = true;
    }
  }
}
</script>

<style scoped>
.users-management {
  width: 100%;
}
</style>
