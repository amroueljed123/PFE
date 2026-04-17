<template>
  <div class="articles-dashboard">
    <div class="d-flex align-center mb-6">
      <div>
        <h1 class="text-h5 font-weight-bold text-grey-darken-3">Catalogue des Articles</h1>
        <p class="text-caption text-grey mb-0">Gestion de l'inventaire par catégorie</p>
      </div>
      <v-spacer></v-spacer>
      <v-btn
        prepend-icon="mdi-plus"
        color="success"
        variant="elevated"
        @click="openAddDialog"
      >
        Ajouter une Catégorie
      </v-btn>
    </div>

    <!-- Category Grid -->
    <v-card elevation="2" rounded="lg">
      <v-card-text class="pa-4">
        <v-row v-if="!loading">
          <v-col
            v-for="cat in categories"
            :key="cat.id"
            cols="6" sm="4" md="3" lg="2"
          >
            <v-hover v-slot="{ isHovering, props }">
              <v-card
                v-bind="props"
                @click="$router.push(cat.routePath)"
                class="category-card text-center pa-3 position-relative"
                :class="!cat.color.startsWith('#') ? cat.color + '-lighten-5' : ''"
                :style="{ backgroundColor: getBgColor(cat.color) }"
                elevation="0"
                rounded="lg"
              >
                <!-- Actions overlay -->
                <v-fade-transition>
                  <div v-if="isHovering" class="quick-actions">
                    <v-btn icon="mdi-pencil" size="x-small" color="white" class="mr-1" variant="elevated" @click.stop="openEditDialog(cat)"></v-btn>
                    <v-btn icon="mdi-delete" size="x-small" color="error" variant="elevated" @click.stop="confirmDelete(cat)"></v-btn>
                  </div>
                </v-fade-transition>

                <div class="clickable-area">
                  <v-icon :color="cat.color.startsWith('#') ? cat.color : cat.color" size="32" class="mb-3 mt-2">{{ cat.icon }}</v-icon>
                  <div class="text-body-2 font-weight-bold text-grey-darken-3">{{ cat.name }}</div>
                </div>
              </v-card>
            </v-hover>
          </v-col>
        </v-row>
        
        <div v-if="loading" class="d-flex justify-center my-6">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>
      </v-card-text>
    </v-card>

    <!-- Dialog to Add/Edit Category -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title class="bg-primary text-white pa-4">
          <v-icon class="mr-2">{{ isEditing ? 'mdi-pencil' : 'mdi-shape-plus' }}</v-icon>
          {{ isEditing ? 'Modifier la Catégorie' : 'Nouvelle Catégorie' }}
        </v-card-title>
        <v-card-text class="pa-5">
          <v-form ref="categoryForm" @submit.prevent="saveCategory">
            <v-text-field
              v-model="editedItem.name"
              label="Nom de la catégorie"
              variant="outlined"
              required
              class="mb-3"
            ></v-text-field>

            <v-row>
              <v-col cols="12" sm="6">
                <div class="text-subtitle-2 mb-2">Choisir une couleur :</div>
                <v-color-picker
                  v-model="editedItem.color"
                  hide-inputs
                  canvas-height="100"
                  class="mx-auto"
                ></v-color-picker>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-subtitle-2 mb-2">Rechercher une icône :</div>
                <v-autocomplete
                  v-model="editedItem.icon"
                  :items="iconList"
                  item-title="title"
                  item-value="value"
                  label="Tapez pour chercher (ex: car, phone)"
                  variant="outlined"
                  :loading="fetchingIcons"
                >
                  <template v-slot:item="{ props, item }">
                    <v-list-item v-bind="{...props, title: undefined}" class="py-2">
                      <div class="d-flex justify-center w-100">
                        <v-icon size="32" color="grey-darken-3">{{ item.value }}</v-icon>
                      </div>
                    </v-list-item>
                  </template>
                  <template v-slot:prepend-inner>
                    <v-icon>{{ editedItem.icon || 'mdi-help-circle' }}</v-icon>
                  </template>
                </v-autocomplete>
              </v-col>
            </v-row>

            <v-row class="mt-4">
              <v-col class="text-right">
                <v-btn color="grey" variant="text" @click="dialog = false">Annuler</v-btn>
                <v-btn color="success" variant="elevated" type="submit" :loading="saving">Sauvegarder</v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" max-width="400px">
      <v-card>
        <v-card-title class="text-h6 bg-error text-white">Confirmation</v-card-title>
        <v-card-text class="pt-4">
          Êtes-vous sûr de vouloir supprimer la catégorie <strong>{{ categoryToDelete?.name }}</strong> ?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="deleteDialog = false">Annuler</v-btn>
          <v-btn color="error" variant="text" @click="executeDelete" :loading="deleting">Supprimer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000">{{ snackbarMessage }}</v-snackbar>
  </div>
</template>

<script>
import CategoryService from '@/services/CategoryService'

export default {
  name: 'Catalogue',
  data() {
    return {
      loading: false,
      categories: [],
      
      // Add/Edit Dialog
      dialog: false,
      isEditing: false,
      saving: false,
      editedItem: { id: null, name: '', color: '#2196F3', icon: 'mdi-tag', routePath: '' },
      
      // Delete Dialog
      deleteDialog: false,
      deleting: false,
      categoryToDelete: null,
      
      // Snackbar
      snackbar: false,
      snackbarColor: 'success',
      snackbarMessage: '',
      
      // Icons API
      iconList: [],
      fetchingIcons: false,
      iconsLoaded: false
    }
  },
  mounted() {
    this.fetchCategories()
  },
  methods: {
    async fetchCategories() {
      this.loading = true
      try {
        const res = await CategoryService.getAllCategories()
        this.categories = res.data
      } catch (e) {
        this.showError("Échec du chargement des catégories")
      } finally {
        this.loading = false
      }
    },
    
    // Fetch icons only when dialog opens for performance
    async loadIcons() {
      if (this.iconsLoaded) return
      this.fetchingIcons = true
      try {
        const response = await fetch('https://cdn.jsdelivr.net/npm/@mdi/svg@latest/meta.json')
        const data = await response.json()
        this.iconList = data.map(i => ({
          name: i.name,
          title: i.name + (i.aliases.length ? ` (${i.aliases.join(', ')})` : ''),
          value: `mdi-${i.name}`
        }))
        this.iconsLoaded = true
      } catch (e) {
        console.error("Failed to load MDI icons", e)
      } finally {
        this.fetchingIcons = false
      }
    },
    
    // Convert hex to very light version for the card background
    getBgColor(hexColor) {
      if (!hexColor) return '#f5f5f5'
      if (!hexColor.startsWith('#')) return null // Let Vuetify class handle it (amber-lighten-5 etc)
      
      const hex = hexColor.replace('#', '')
      const r = parseInt(hex.substring(0, 2), 16)
      const g = parseInt(hex.substring(2, 4), 16)
      const b = parseInt(hex.substring(4, 6), 16)
      return `rgba(${r}, ${g}, ${b}, 0.1)`
    },
    
    openAddDialog() {
      this.isEditing = false
      this.editedItem = { id: null, name: '', color: '#2196F3', icon: 'mdi-tag', routePath: '' }
      this.loadIcons()
      this.dialog = true
    },
    
    openEditDialog(cat) {
      this.isEditing = true
      this.editedItem = { ...cat }
      // Convert legacy color name to hex if necessary, but v-color-picker supports basic names anyway.
      // Easiest is just passing it.
      this.loadIcons()
      this.dialog = true
    },
    
    async saveCategory() {
      if (!this.editedItem.name || !this.editedItem.icon) return
      this.saving = true
      try {
        if (this.isEditing) {
          await CategoryService.updateCategory(this.editedItem.id, this.editedItem)
          this.showSuccess("Catégorie modifiée avec succès")
        } else {
          await CategoryService.createCategory(this.editedItem)
          this.showSuccess("Catégorie créée avec succès")
        }
        this.dialog = false
        this.fetchCategories()
      } catch (err) {
        this.showError(err.response?.data || "Erreur de sauvegarde")
      } finally {
        this.saving = false
      }
    },
    
    confirmDelete(cat) {
      this.categoryToDelete = cat
      this.deleteDialog = true
    },
    
    async executeDelete() {
      if (!this.categoryToDelete) return
      this.deleting = true
      try {
        await CategoryService.deleteCategory(this.categoryToDelete.id)
        this.showSuccess("Catégorie supprimée")
        this.deleteDialog = false
        this.fetchCategories()
      } catch (err) {
        this.showError("Erreur lors de la suppression")
      } finally {
        this.deleting = false
      }
    },
    
    showSuccess(msg) {
      this.snackbarMessage = msg
      this.snackbarColor = 'success'
      this.snackbar = true
    },
    
    showError(msg) {
      this.snackbarMessage = msg
      this.snackbarColor = 'error'
      this.snackbar = true
    }
  }
}
</script>

<style scoped>
.category-card {
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  height: 100%;
}
.clickable-area {
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1) !important;
}

.quick-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  z-index: 2;
}</style>
