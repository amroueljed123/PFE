<template>
  <div class="article-management">
    <!-- ═══ SUB-CATEGORY NAVIGATION BAR ═══ -->
    <div class="subcategory-nav mb-6 pa-3 bg-white rounded-xl border shadow-sm">
      <div class="d-flex align-center mb-3 px-2">
        <div class="d-flex align-center">
          <v-icon color="primary" class="mr-2" size="22">mdi-layers-outline</v-icon>
          <span class="text-subtitle-1 font-weight-bold text-grey-darken-3">Sous-catégories</span>
        </div>
        <v-spacer></v-spacer>
        <v-btn
          v-if="currentRootCategory"
          size="small"
          color="primary"
          variant="tonal"
          prepend-icon="mdi-cog-outline"
          rounded="lg"
          class="text-none"
          @click="openAddSubDialog"
        >
          Gérer les catégories
        </v-btn>
      </div>
      
      <div class="subcategory-scroll-container">
        <div class="subcategory-flex">
          <!-- "All" Button -->
          <div
            class="modern-subcat-card"
            :class="{ 'modern-subcat-card--active': !activeSubcategory }"
            @click="activeSubcategory = null"
          >
            <div class="modern-subcat-icon-box bg-grey-lighten-4">
              <v-icon size="20" :color="!activeSubcategory ? 'primary' : 'grey-darken-1'">mdi-view-grid-outline</v-icon>
            </div>
            <span class="modern-subcat-label">Toutes</span>
            <v-chip size="x-small" class="ml-2 font-weight-bold" :color="!activeSubcategory ? 'primary' : 'grey-lighten-2'">{{ items.length }}</v-chip>
          </div>

          <!-- Dynamic Subcategories -->
          <div
            v-for="sub in dynamicSubcategoriesWithCount"
            :key="sub.id"
            class="modern-subcat-card"
            :class="{ 'modern-subcat-card--active': activeSubcategory && activeSubcategory.id === sub.id }"
            :style="activeSubcategory && activeSubcategory.id === sub.id ? { borderColor: sub.color + '66', background: sub.color + '0A' } : {}"
            @click="activeSubcategory = sub"
          >
            <div class="modern-subcat-icon-box" :style="{ backgroundColor: sub.color + '1A' }">
              <v-icon size="20" :style="{ color: sub.color || '#2196F3' }">{{ sub.icon || 'mdi-tag-outline' }}</v-icon>
            </div>
            <span class="modern-subcat-label">{{ sub.name }}</span>
            <v-chip size="x-small" class="ml-2 font-weight-bold" :style="activeSubcategory && activeSubcategory.id === sub.id ? { backgroundColor: sub.color, color: 'white' } : {}">{{ sub.count }}</v-chip>
            
            <!-- Context Actions -->
            <div class="modern-subcat-actions" v-if="activeSubcategory && activeSubcategory.id === sub.id">
               <v-btn icon="mdi-pencil" size="x-small" variant="text" color="primary" density="compact" @click.stop="openEditSubDialog(sub)"></v-btn>
               <v-btn icon="mdi-delete" size="x-small" variant="text" color="error" density="compact" @click.stop="confirmDeleteSub(sub)"></v-btn>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ MAIN TOOLBAR ═══ -->
    <div class="catalog-toolbar mb-6">
      <div class="toolbar-left">
        <v-btn
          color="primary"
          variant="elevated"
          @click="openAddDialog"
          prepend-icon="mdi-plus"
          class="add-btn px-6"
          rounded="lg"
          size="large"
          elevation="2"
        >
          Ajouter {{ singularTitle }}
        </v-btn>
        <div class="item-stats-pill ml-4 d-none d-sm-flex">
          <v-icon size="18" color="primary" class="mr-2">mdi-package-variant-closed</v-icon>
          <span class="font-weight-black text-primary mr-1">{{ filteredItems.length }}</span>
          <span class="text-grey-darken-1 text-caption font-weight-medium">articles</span>
        </div>
      </div>
      <div class="toolbar-right">
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          placeholder="Rechercher par nom, code..."
          variant="solo"
          density="comfortable"
          clearable
          hide-details
          rounded="xl"
          class="search-input shadow-sm"
          bg-color="white"
        ></v-text-field>
      </div>
    </div>

    <!-- ═══ PRODUCT GRID ═══ -->
    <div v-if="!loading && searchedItems.length" class="product-grid">
      <div
        v-for="item in searchedItems"
        :key="item.id"
        class="glass-product-card"
        @click="openDetail(item)"
      >
        <div class="product-visual">
          <v-img v-if="item.image" :src="item.image" cover class="product-image" />
          <div v-else class="product-placeholder">
            <v-icon size="56" color="primary" style="opacity: 0.2">mdi-cube-scan</v-icon>
          </div>
          
          <div class="product-status-tag" :class="'status-' + getStatutKey(item.statut)">
            {{ item.statut || 'N/A' }}
          </div>
          
          <!-- Actions visible ONLY on HOVER -->
          <div class="product-hover-actions">
            <v-btn size="small" variant="elevated" color="white" icon class="mb-2 action-bounce" @click.stop="editItem(item)">
              <v-icon size="18" color="primary">mdi-pencil</v-icon>
            </v-btn>
            <v-btn size="small" variant="elevated" color="error" icon class="action-bounce" @click.stop="deleteItem(item)">
              <v-icon size="18">mdi-delete</v-icon>
            </v-btn>
          </div>
        </div>

        <div class="product-info pa-4">
          <h3 class="product-title mb-1 text-truncate">{{ item.nom }}</h3>
          <div class="d-flex align-center mb-3">
            <span class="text-caption text-grey-darken-1 mr-2">{{ item.marque || 'Sans marque' }}</span>
            <v-divider vertical length="10" class="mx-1"></v-divider>
            <span class="text-caption text-primary font-weight-bold ml-1">{{ item.sousCategorie || familyFilter }}</span>
          </div>
          
          <div class="d-flex align-center justify-space-between mt-auto">
            <div class="price-display">
              <span class="currency">DA</span>
              <span class="amount">{{ formatPriceRaw(item.prix) }}</span>
            </div>
            <div class="stock-badge" :class="getStockClass(item)">
              <v-icon size="12" class="mr-1">mdi-circle</v-icon>
              {{ item.quantite }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-grid">
      <div v-for="n in 8" :key="n" class="skeleton-card pa-4 rounded-xl border">
        <v-skeleton-loader type="image, article" height="280"></v-skeleton-loader>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && !searchedItems.length" class="empty-state-v2 pa-12 text-center rounded-xl border-dashed">
       <v-icon size="100" color="grey-lighten-3" class="mb-4">mdi-package-variant-closed</v-icon>
       <h3 class="text-h6 text-grey-darken-1">Aucun article trouvé</h3>
       <p class="text-body-2 text-grey">Essayez de modifier vos filtres ou d'ajouter un nouvel article.</p>
    </div>

    <!-- ═══ DIALOGS ═══ -->
    
    <!-- Detail Dialog -->
    <v-dialog v-model="detailDialog" width="900" scrollable transition="dialog-bottom-transition">
      <v-card v-if="selectedItem" rounded="xl" class="detail-card overflow-hidden">
        <div class="detail-hero pa-0">
          <div class="detail-hero-content d-flex flex-column flex-md-row align-center pa-8">
            <v-btn icon="mdi-close" variant="text" color="white" class="detail-close-btn" @click="detailDialog = false"></v-btn>
            <div class="detail-hero-img-box rounded-xl mr-md-8 mb-6 mb-md-0">
              <v-img v-if="selectedItem.image" :src="selectedItem.image" cover class="fill-height" />
              <v-icon v-else size="80" color="white" style="opacity: 0.3">mdi-cube-outline</v-icon>
            </div>
            <div class="detail-hero-text text-white text-center text-md-left">
              <v-chip size="small" color="white" variant="outlined" class="mb-3">{{ selectedItem.type }}</v-chip>
              <h2 class="text-h3 font-weight-black mb-2">{{ selectedItem.nom }}</h2>
              <div class="text-h5 font-weight-medium text-blue-lighten-4 mb-4">{{ formatPrice(selectedItem.prix) }}</div>
              <div class="d-flex align-center justify-center justify-md-start">
                <v-icon size="16" class="mr-2">mdi-barcode</v-icon>
                <span class="text-body-2 font-weight-bold opacity-80">{{ selectedItem.codeBarre }}</span>
              </div>
            </div>
          </div>
        </div>

        <v-card-text class="pa-8">
          <v-row>
            <v-col cols="12" md="7">
              <div class="detail-section mb-8">
                <div class="d-flex align-center mb-4">
                  <v-icon color="primary" class="mr-2">mdi-text-subject</v-icon>
                  <span class="text-h6 font-weight-bold">Description</span>
                </div>
                <p class="text-body-1 text-grey-darken-1 line-height-relaxed">
                  {{ selectedItem.description || 'Aucune description disponible pour cet article.' }}
                </p>
              </div>

              <div class="detail-section">
                <div class="d-flex align-center mb-4">
                  <v-icon color="primary" class="mr-2">mdi-information-outline</v-icon>
                  <span class="text-h6 font-weight-bold">Spécifications techniques</span>
                </div>
                <div class="modern-specs-grid">
                  <div class="modern-spec-item" v-for="spec in itemSpecs" :key="spec.label">
                    <v-icon :icon="spec.icon" size="20" color="grey-darken-1" class="mr-3"></v-icon>
                    <div>
                      <div class="modern-spec-label">{{ spec.label }}</div>
                      <div class="modern-spec-value">{{ spec.value }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </v-col>
            
            <v-col cols="12" md="5">
              <div class="stock-highlight-card pa-6 rounded-xl border text-center mb-6">
                <div class="text-overline text-grey-darken-1 mb-1">Niveau de Stock</div>
                <div class="text-h2 font-weight-black text-primary">{{ selectedItem.quantite }}</div>
                <div class="text-subtitle-2 text-grey mt-1">{{ selectedItem.unite || 'Unité(s)' }}</div>
                <v-divider class="my-4"></v-divider>
                <div class="d-flex align-center justify-center">
                  <v-icon :color="getStockClass(selectedItem)" size="14" class="mr-2">mdi-circle</v-icon>
                  <span class="text-body-2 font-weight-bold">{{ selectedItem.statut }}</span>
                </div>
              </div>

              <div v-if="selectedItem.qrCode" class="qr-preview pa-6 rounded-xl border bg-grey-lighten-5 text-center">
                <div class="text-caption font-weight-bold text-grey-darken-1 mb-3 text-uppercase">QR de traçabilité</div>
                <v-img :src="selectedItem.qrCode" width="160" class="mx-auto rounded-lg border shadow-sm" />
                <v-btn variant="text" size="small" color="primary" class="mt-4" prepend-icon="mdi-download">Télécharger</v-btn>
              </div>
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="pa-6 border-top bg-white">
          <v-spacer></v-spacer>
          <v-btn variant="text" color="grey-darken-1" class="px-6" @click="detailDialog = false">Fermer</v-btn>
          <v-btn color="primary" variant="elevated" @click="editFromDetail" prepend-icon="mdi-pencil" rounded="lg" class="px-6">Modifier</v-btn>
          <v-btn color="error" variant="tonal" @click="deleteFromDetail" prepend-icon="mdi-delete" rounded="lg" class="ml-2 px-6">Supprimer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Subcategory Management Dialog -->
    <v-dialog v-model="subcatDialog" max-width="650px" transition="dialog-fade-transition">
      <v-card rounded="xl" class="subcat-mgmt-card">
        <v-card-title class="bg-primary text-white pa-6 d-flex align-center">
          <v-icon class="mr-3" size="28">mdi-shape-plus</v-icon>
          <span class="text-h6 font-weight-bold">{{ editingSubcatId ? 'Modifier la Sous-catégorie' : 'Nouvelle Sous-catégorie' }}</span>
        </v-card-title>
        
        <v-card-text class="pa-8">
          <v-form @submit.prevent="saveSubcategory">
            <v-text-field 
              v-model="subcatForm.name" 
              label="Nom de la sous-catégorie" 
              variant="outlined" 
              required 
              class="mb-6"
              bg-color="grey-lighten-4"
              hide-details
            ></v-text-field>
            
            <v-row>
              <v-col cols="12" sm="6">
                <div class="text-subtitle-2 font-weight-bold mb-3 d-flex align-center">
                  <v-icon size="18" class="mr-2" color="primary">mdi-palette-outline</v-icon>Couleur d'identité
                </div>
                <v-color-picker 
                  v-model="subcatForm.color" 
                  hide-inputs 
                  canvas-height="120" 
                  class="mx-auto border-0 rounded-xl"
                  elevation="0"
                ></v-color-picker>
              </v-col>
              <v-col cols="12" sm="6">
                <div class="text-subtitle-2 font-weight-bold mb-3 d-flex align-center">
                  <v-icon size="18" class="mr-2" color="primary">mdi-emoticon-outline</v-icon>Icône représentative
                </div>
                <v-autocomplete
                  v-model="subcatForm.icon"
                  :items="iconList"
                  item-title="title"
                  item-value="value"
                  label="Rechercher une icône..."
                  variant="outlined"
                  class="icon-search-input"
                  :loading="fetchingIcons"
                  bg-color="grey-lighten-4"
                >
                  <template v-slot:prepend-inner>
                    <div class="pa-1 rounded" :style="{ backgroundColor: subcatForm.color + '22' }">
                      <v-icon :color="subcatForm.color">{{ subcatForm.icon || 'mdi-tag' }}</v-icon>
                    </div>
                  </template>
                  <template v-slot:item="{ props, item }">
                    <v-list-item v-bind="props" :prepend-icon="item.value" :title="item.title"></v-list-item>
                  </template>
                </v-autocomplete>
                
                <div class="preview-pill mt-6 pa-4 rounded-xl border text-center" :style="{ background: subcatForm.color + '05', borderColor: subcatForm.color + '33' }">
                  <div class="text-caption text-grey-darken-1 mb-2">Aperçu du Badge</div>
                  <v-chip :prepend-icon="subcatForm.icon" :color="subcatForm.color" variant="flat" class="font-weight-bold">
                    {{ subcatForm.name || 'Nom' }}
                  </v-chip>
                </div>
              </v-col>
            </v-row>

            <v-row class="mt-8">
              <v-col class="text-right">
                <v-btn color="grey-darken-1" variant="text" class="px-6 mr-2" @click="subcatDialog = false">Annuler</v-btn>
                <v-btn color="primary" variant="elevated" type="submit" :loading="savingSub" rounded="lg" class="px-8 shadow-sm">
                  {{ editingSubcatId ? 'Mettre à jour' : 'Créer maintenant' }}
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Standard Forms -->
    <v-dialog v-model="dialog" width="800" scrollable>
      <v-card rounded="xl">
        <v-card-title class="bg-primary text-white pa-4">
          <v-icon class="mr-2">{{ editingId ? 'mdi-pencil' : 'mdi-plus-circle' }}</v-icon>
          {{ editingId ? `Modifier ${singularTitle}` : `Ajouter ${singularTitle}` }}
        </v-card-title>
        <v-card-text class="pa-6">
          <v-form ref="form" @submit.prevent="saveItem">
            <v-row>
              <v-col cols="12" md="6"><v-text-field v-model="formData.codeBarre" label="Code Barre *" variant="outlined" density="comfortable" required></v-text-field></v-col>
              <v-col cols="12" md="6"><v-text-field v-model="formData.nom" label="Nom *" variant="outlined" density="comfortable" required></v-text-field></v-col>
              <v-col cols="12" md="6">
                <v-select v-model="formData.sousCategorie" :items="availableSubcategoryNames" label="Sous-catégorie" variant="outlined" density="comfortable" clearable></v-select>
              </v-col>
              <v-col cols="12" md="6"><v-text-field v-model="formData.famille" label="Famille" variant="outlined" density="comfortable" readonly></v-text-field></v-col>
              <v-col cols="12"><v-textarea v-model="formData.description" label="Description" variant="outlined" density="comfortable" rows="2"></v-textarea></v-col>
              <v-col cols="4"><v-text-field v-model.number="formData.prix" label="Prix" type="number" variant="outlined" density="comfortable"></v-text-field></v-col>
              <v-col cols="4"><v-text-field v-model.number="formData.quantite" label="Quantité" type="number" variant="outlined" density="comfortable"></v-text-field></v-col>
              <v-col cols="4"><v-text-field v-model.number="formData.seuilAlerte" label="Seuil Alerte" type="number" variant="outlined" density="comfortable"></v-text-field></v-col>
            </v-row>
            <div v-if="hasCategoryFields" class="mt-4"><v-divider class="mb-6"></v-divider><slot name="categoryFields" :formData="formData"></slot></div>
            <v-row class="mt-6 pa-3"><v-spacer></v-spacer><v-btn color="grey" variant="text" @click="dialog = false" class="mr-2">Annuler</v-btn><v-btn color="success" variant="elevated" type="submit" rounded="lg" class="px-8">Enregistrer</v-btn></v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="deleteDialog" width="400"><v-card rounded="xl" class="overflow-hidden"><v-card-title class="bg-error text-white pa-4 text-center">Attention</v-card-title><v-card-text class="pa-8 text-center"><v-icon size="64" color="error" class="mb-4">mdi-alert-circle-outline</v-icon><div class="text-h6">Confirmer la suppression ?</div><div class="text-body-2 text-grey-darken-1 mt-2">Cette action est définitive et supprimera l'article de la base de données.</div></v-card-text><v-card-actions class="pa-4 bg-grey-lighten-4"><v-spacer></v-spacer><v-btn variant="text" color="grey-darken-1" @click="deleteDialog=false">Annuler</v-btn><v-btn color="error" variant="elevated" @click="confirmDelete" rounded="lg" class="px-6">Supprimer</v-btn></v-card-actions></v-card></v-dialog>

    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" rounded="pill" elevation="10" location="bottom right">
      <div class="d-flex align-center"><v-icon class="mr-2" size="20">mdi-check-circle</v-icon>{{ snackbarMessage }}</div>
    </v-snackbar>
  </div>
</template>

<script>
import CategoryService from '@/services/CategoryService'

export default {
  props: {
    title: { type: String, required: true },
    singularTitle: { type: String, required: true },
    items: { type: Array, required: true },
    headers: { type: Array, required: true },
    loading: { type: Boolean, default: false },
    hasCategoryFields: { type: Boolean, default: false },
    familyFilter: { type: String, default: '' }
  },
  emits: ['add-item', 'update-item', 'delete-item'],
  data() {
    return {
      dialog: false,
      deleteDialog: false,
      detailDialog: false,
      selectedItem: null,
      editingId: null,
      itemToDelete: null,
      search: '',
      activeSubcategory: null,
      
      allCategories: [],
      subcatDialog: false,
      editingSubcatId: null,
      savingSub: false,
      subcatForm: { name: '', icon: 'mdi-tag', color: '#2196F3' },
      
      iconList: [],
      fetchingIcons: false,
      iconsLoaded: false,
      
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      
      formData: {
        codeBarre: '', nom: '', description: '', prix: 0, quantite: 0,
        unite: '', famille: '', fournisseur: '', fabricant: '',
        statut: 'En stock', lieu: '', responsableTechnique: '', utilisateur: '',
        numeroSerie: '', numeroInventaire: '', seuilAlerte: null, 
        sousCategorie: '', image: ''
      }
    }
  },
  computed: {
    currentRootCategory() {
      if (!this.allCategories.length) return null
      const family = this.familyFilter || this.singularTitle + 's'
      return this.allCategories.find(c => !c.parent && c.name.toLowerCase() === family.toLowerCase())
             || this.allCategories.find(c => !c.parent && c.name.toLowerCase().includes(family.toLowerCase().substring(0, 4))) 
             || this.allCategories.find(c => !c.parent && this.title.toLowerCase().includes(c.name.toLowerCase()))
    },
    dynamicSubcategories() {
      if (!this.currentRootCategory) return []
      return this.allCategories.filter(c => c.parent && c.parent.id === this.currentRootCategory.id)
    },
    dynamicSubcategoriesWithCount() {
      return this.dynamicSubcategories.map(sub => ({
        ...sub,
        count: this.items.filter(item => item.sousCategorie === sub.name).length
      }))
    },
    availableSubcategoryNames() {
      return this.dynamicSubcategories.map(s => s.name)
    },
    filteredItems() {
      if (!this.activeSubcategory) return this.items
      return this.items.filter(item => item.sousCategorie === this.activeSubcategory.name)
    },
    searchedItems() {
      if (!this.search) return this.filteredItems
      const q = this.search.toLowerCase()
      return this.filteredItems.filter(item => (item.nom && item.nom.toLowerCase().includes(q)) || (item.codeBarre && item.codeBarre.toLowerCase().includes(q)))
    },
    itemSpecs() {
      if (!this.selectedItem) return []
      const exclude = ['id', 'image', 'qrCode', 'donnee', 'createdAt', 'updatedAt', 'type', 'nom', 'codeBarre', 'description', 'prix', 'quantite', 'unite']
      const iconMap = {
        marque: { icon: 'mdi-domain', label: 'Marque' },
        modele: { icon: 'mdi-card-text', label: 'Modèle' },
        famille: { icon: 'mdi-folder', label: 'Famille' },
        fournisseur: { icon: 'mdi-truck', label: 'Fournisseur' },
        fabricant: { icon: 'mdi-factory', label: 'Fabricant' },
        statut: { icon: 'mdi-circle-slice-8', label: 'Statut' },
        lieu: { icon: 'mdi-map-marker', label: 'Lieu' },
        utilisateur: { icon: 'mdi-account', label: 'Utilisateur' },
        responsableTechnique: { icon: 'mdi-account-wrench', label: 'Resp. Technique' },
        numeroSerie: { icon: 'mdi-numeric', label: 'N° Série' },
        numeroInventaire: { icon: 'mdi-clipboard-list', label: 'N° Inventaire' },
        seuilAlerte: { icon: 'mdi-alert', label: 'Seuil Alerte' },
        typeOrdinateur: { icon: 'mdi-desktop-classic', label: 'Type Ordinateur' },
        processeur: { icon: 'mdi-chip', label: 'Processeur' },
        ram: { icon: 'mdi-memory', label: 'RAM' },
        stockage: { icon: 'mdi-harddisk', label: 'Stockage' },
        systemeExploitation: { icon: 'mdi-microsoft-windows', label: 'Système' },
        sousCategorie: { icon: 'mdi-shape-plus', label: 'Sous-catégorie' }
      }
      const specs = []
      for (const [key, val] of Object.entries(this.selectedItem)) {
        if (exclude.includes(key) || val === null || val === undefined || val === '') continue
        const meta = iconMap[key] || { icon: 'mdi-information', label: key.charAt(0).toUpperCase() + key.slice(1) }
        specs.push({ ...meta, value: val })
      }
      return specs
    }
  },
  mounted() {
    this.fetchCategories()
    if (this.familyFilter) this.formData.famille = this.familyFilter
    this.loadIcons()
  },
  methods: {
    async fetchCategories() {
      try {
        const res = await CategoryService.getAllCategories()
        this.allCategories = res.data
      } catch (e) { console.error(e) }
    },
    async loadIcons() {
      if (this.iconsLoaded) return
      this.fetchingIcons = true
      try {
        const response = await fetch('https://cdn.jsdelivr.net/npm/@mdi/svg@latest/meta.json')
        const data = await response.json()
        this.iconList = data.map(i => ({ title: i.name, value: `mdi-${i.name}` }))
        this.iconsLoaded = true
      } catch (e) { console.error(e) } finally { this.fetchingIcons = false }
    },
    openDetail(item) {
      this.selectedItem = { ...item }
      this.detailDialog = true
    },
    editFromDetail() {
      const item = { ...this.selectedItem }
      this.detailDialog = false
      setTimeout(() => this.editItem(item), 300)
    },
    deleteFromDetail() {
      const item = { ...this.selectedItem }
      this.detailDialog = false
      setTimeout(() => this.deleteItem(item), 300)
    },
    openAddDialog() {
      this.editingId = null
      this.resetForm()
      this.formData.famille = this.familyFilter || (this.currentRootCategory ? this.currentRootCategory.name : '')
      this.dialog = true
    },
    editItem(item) {
      this.editingId = item.id
      this.formData = { ...item }
      this.dialog = true
    },
    deleteItem(item) {
      this.itemToDelete = item
      this.deleteDialog = true
    },
    confirmDelete() {
      this.$emit('delete-item', this.itemToDelete.id)
      this.deleteDialog = false
    },
    saveItem() {
      if (this.editingId) this.$emit('update-item', { id: this.editingId, data: this.formData })
      else this.$emit('add-item', this.formData)
      this.dialog = false
    },
    resetForm() {
       this.formData = { codeBarre: '', nom: '', description: '', prix: 0, quantite: 0, unite: 'Unit', famille: this.familyFilter, fournisseur: '', statut: 'En stock', sousCategorie: '' }
    },
    formatPriceRaw(p) { return p ? new Intl.NumberFormat('fr-DZ').format(p) : '0' },
    formatPrice(p) { return this.formatPriceRaw(p) + ' DA' },
    getStatutKey(s) { const m = { 'En stock': 'stock', 'En utilisation': 'use', 'En réparation': 'repair' }; return m[s] || 'other' },
    getStockClass(i) { 
      if (i.seuilAlerte && i.quantite <= i.seuilAlerte) return 'stock-danger'
      return i.quantite < 5 ? 'stock-danger' : 'stock-ok' 
    },
    
    openAddSubDialog() {
      this.editingSubcatId = null
      this.subcatForm = { name: '', icon: 'mdi-tag', color: this.currentRootCategory?.color || '#2196F3' }
      this.subcatDialog = true
    },
    openEditSubDialog(sub) {
      this.editingSubcatId = sub.id
      this.subcatForm = { ...sub }
      this.subcatDialog = true
    },
    async saveSubcategory() {
      if (!this.subcatForm.name || !this.currentRootCategory) return
      this.savingSub = true
      try {
        const payload = { ...this.subcatForm, parentId: this.currentRootCategory.id }
        if (this.editingSubcatId) await CategoryService.updateCategory(this.editingSubcatId, payload)
        else await CategoryService.createCategory(payload)
        this.subcatDialog = false
        this.fetchCategories()
      } catch (e) { this.showNotification('Erreur subcat', 'error') }
      finally { this.savingSub = false }
    },
    async confirmDeleteSub(sub) {
      if (confirm(`Supprimer la sous-catégorie ${sub.name} ?`)) {
        try { await CategoryService.deleteCategory(sub.id); this.fetchCategories() } catch(e) { console.error(e) }
      }
    },
    showNotification(m, c) { this.snackbarMessage = m; this.snackbarColor = c; this.snackbar = true }
  }
}
</script>

<style scoped>
.article-management { padding: 20px; background: #f8fafc; min-height: 100vh; }

/* ── MODERN SUBCAT CARDS ── */
.subcategory-scroll-container { overflow-x: auto; padding-bottom: 8px; }
.subcategory-flex { display: flex; gap: 14px; padding: 4px; }

.modern-subcat-card {
  display: flex; align-items: center; padding: 8px 16px; border-radius: 100px;
  background: white; border: 1px solid #e2e8f0; cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); min-width: max-content;
}
.modern-subcat-card:hover { transform: translateY(-3px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); border-color: #cbd5e1; }
.modern-subcat-card--active { border-color: #2563eb66; box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.1); }

.modern-subcat-icon-box {
  width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  margin-right: 12px; transition: transform 0.3s ease;
}
.modern-subcat-card:hover .modern-subcat-icon-box { transform: scale(1.1); }
.modern-subcat-label { font-size: 14px; font-weight: 700; color: #334155; }
.modern-subcat-actions { display: flex; margin-left: 8px; border-left: 1px solid #e2e8f0; padding-left: 4px; }

/* ── PRODUCT GRID & GLASS CARDS ── */
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }

.glass-product-card {
  background: white; border-radius: 24px; overflow: hidden; cursor: pointer;
  border: 1px solid #f1f5f9; transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex; flex-direction: column; height: 100%;
}
.glass-product-card:hover { transform: translateY(-10px); box-shadow: 0 25px 30px -10px rgba(0,0,0,0.08); border-color: #e2e8f0; }

.product-visual { position: relative; height: 200px; background: #f8fafc; overflow: hidden; }
.product-image { height: 100%; width: 100%; transition: transform 0.6s ease; }
.glass-product-card:hover .product-image { transform: scale(1.1); }
.product-placeholder { height: 100%; display: flex; align-items: center; justify-content: center; background: #f1f5f9; }

/* HOVER ACTIONS */
.hover-overlay-actions {
  position: absolute; top: 12px; right: 12px; display: flex; flex-direction: column;
  opacity: 0; transform: translateX(20px); transition: all 0.3s ease; z-index: 10;
}
.glass-product-card:hover .hover-overlay-actions { opacity: 1; transform: translateX(0); }
.action-bounce:hover { transform: scale(1.1); }

.product-status-tag {
  position: absolute; bottom: 12px; left: 12px; padding: 4px 10px; border-radius: 100px;
  background: rgba(255,255,255,0.9); backdrop-filter: blur(8px); font-size: 11px; font-weight: 800; text-transform: uppercase;
}
.status-stock { color: #16a34a; } .status-use { color: #2563eb; } .status-repair { color: #d97706; }

.product-title { font-size: 16px; font-weight: 800; color: #1e293b; }
.price-display .amount { font-size: 20px; font-weight: 900; color: #1e40af; }
.price-display .currency { font-size: 12px; font-weight: 700; color: #64748b; margin-right: 2px; }

.stock-badge { padding: 4px 10px; border-radius: 8px; font-size: 12px; font-weight: 700; display: flex; align-items: center; }
.stock-badge.stock-ok { background: #f0fdf4; color: #16a34a; }
.stock-badge.stock-danger { background: #fef2f2; color: #dc2626; }

/* ── DETAIL VIEW ── */
.detail-hero { background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%); position: relative; }
.detail-hero-img-box { width: 220px; height: 220px; background: white; padding: 12px; shadow: 0 20px 25px -5px rgba(0,0,0,0.2); }
.detail-close-btn { position: absolute; top: 16px; right: 16px; }
.modern-specs-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.modern-spec-item { display: flex; align-items: center; padding: 12px; background: #f8fafc; border-radius: 16px; }
.modern-spec-label { font-size: 11px; text-transform: uppercase; color: #94a3b8; font-weight: 800; letter-spacing: 0.5px; }
.modern-spec-value { font-size: 14px; font-weight: 700; color: #334155; }

.catalog-toolbar { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; }
.item-stats-pill { background: white; padding: 8px 16px; border-radius: 100px; border: 1px solid #e2e8f0; align-items: center; }
.search-input { min-width: 320px; }

.shadow-sm { box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06) !important; }
.border-dashed { border: 2px dashed #e2e8f0; }
</style>
