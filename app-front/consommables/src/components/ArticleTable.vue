<template>
  <div class="article-management">
    <v-card>
      <v-card-title class="bg-primary text-white">
        <v-icon left>mdi-table</v-icon>
        {{ title }}
      </v-card-title>

      <v-card-subtitle class="pa-4">
        <v-btn
          color="success"
          variant="elevated"
          @click="openAddDialog"
          prepend-icon="mdi-plus"
        >
          Ajouter {{ singularTitle }}
        </v-btn>
      </v-card-subtitle>

      <v-data-table
        :headers="headers"
        :items="items"
        :loading="loading"
        class="elevation-1"
      >
        <template v-slot:item.prix="{ item }">
          {{ formatPrice(item.prix) }}
        </template>

        <template v-slot:item.qrCode="{ item }">
          <v-img
            v-if="item.qrCode"
            :src="item.qrCode"
            max-width="80"
            max-height="80"
            alt="QR Code"
            class="cursor-pointer"
            @click="showQRDialog(item)"
          />
          <span v-else class="text-grey">N/A</span>
        </template>

        <template v-slot:item.image="{ item }">
          <v-img
            v-if="item.image"
            :src="item.image"
            max-width="80"
            max-height="80"
            cover
            alt="Image"
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-progress-circular indeterminate color="grey-lighten-4"></v-progress-circular>
              </div>
            </template>
            <template v-slot:error>
              <v-icon color="error">mdi-image-off</v-icon>
            </template>
          </v-img>
          <span v-else class="text-grey">N/A</span>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn
            size="small"
            color="primary"
            variant="text"
            @click="editItem(item)"
            prepend-icon="mdi-pencil"
          >
            Modifier
          </v-btn>
          <v-btn
            size="small"
            color="error"
            variant="text"
            @click="deleteItem(item)"
            prepend-icon="mdi-delete"
          >
            Supprimer
          </v-btn>
        </template>

        <template v-slot:no-data>
          <v-alert type="info" class="ma-4">
            Aucun {{ singularTitle.toLowerCase() }} trouvé
          </v-alert>
        </template>
      </v-data-table>
    </v-card>

    <!-- Add/Edit Dialog -->
    <v-dialog v-model="dialog" width="600">
      <v-card>
        <v-card-title>
          {{ editingId ? `Modifier ${singularTitle}` : `Ajouter ${singularTitle}` }}
        </v-card-title>

        <v-card-text class="pa-6">
          <v-form ref="form" @submit.prevent="saveItem">
            <v-text-field
              v-model="formData.codeBarre"
              label="Code Barre"
              variant="outlined"
              required
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="formData.nom"
              label="Nom"
              variant="outlined"
              required
              class="mb-4"
            ></v-text-field>

            <v-textarea
              v-model="formData.description"
              label="Description"
              variant="outlined"
              rows="3"
              class="mb-4"
            ></v-textarea>

            <v-row class="mb-4">
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="formData.prix"
                  label="Prix"
                  type="number"
                  step="0.01"
                  variant="outlined"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model.number="formData.quantite"
                  label="Quantité"
                  type="number"
                  variant="outlined"
                  required
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row class="mb-4">
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.unite"
                  label="Unité"
                  variant="outlined"
                  required
                ></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field
                  v-model="formData.famille"
                  label="Famille"
                  variant="outlined"
                  required
                ></v-text-field>
              </v-col>
            </v-row>

            <v-text-field
              v-model="formData.fournisseur"
              label="Fournisseur"
              variant="outlined"
              required
              class="mb-4"
            ></v-text-field>

            <!-- Image Upload Section -->
            <div class="mb-4">
              <label class="text-subtitle2 font-weight-bold mb-2 d-block">Image</label>
              <v-file-input
                v-model="imageFile"
                accept="image/*"
                label="Sélectionner une image"
                variant="outlined"
                prepend-icon="mdi-image"
                @change="onImageSelected"
              ></v-file-input>
              <v-img
                v-if="formData.image && !imageFile.length"
                :src="formData.image"
                max-width="200"
                max-height="200"
                alt="Article Image"
                class="mt-2"
              />
              <img
                v-else-if="previewImage"
                :src="previewImage"
                style="max-width: 200px; max-height: 200px; margin-top: 8px"
                alt="Image Preview"
              />
            </div>

            <!-- Category-specific fields via slot -->
            <slot name="categoryFields" :formData="formData"></slot>

            <v-divider class="my-4"></v-divider>

            <v-row>
              <v-col class="text-right">
                <v-btn
                  color="gray"
                  variant="outlined"
                  @click="dialog = false"
                  class="mr-2"
                >
                  Annuler
                </v-btn>
                <v-btn color="success" variant="elevated" type="submit">
                  {{ editingId ? 'Mettre à jour' : 'Ajouter' }}
                </v-btn>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="deleteDialog" width="400">
      <v-card>
        <v-card-title class="text-error">
          <v-icon left>mdi-alert</v-icon>
          Confirmation de suppression
        </v-card-title>
        <v-card-text>
          Êtes-vous sûr de vouloir supprimer cet article ?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="deleteDialog = false">Annuler</v-btn>
          <v-btn color="error" variant="elevated" @click="confirmDelete">
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- QR Code Preview Dialog -->
    <v-dialog v-model="qrDialog" width="500">
      <v-card>
        <v-card-title>QR Code</v-card-title>
        <v-card-text class="text-center py-8">
          <v-img
            v-if="selectedQRCode"
            :src="selectedQRCode"
            max-width="300"
            max-height="300"
            alt="QR Code"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="qrDialog = false">Fermer</v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            @click="downloadQRCode"
            prepend-icon="mdi-download"
          >
            Télécharger
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar for notifications -->
    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000">
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      required: true
    },
    singularTitle: {
      type: String,
      required: true
    },
    items: {
      type: Array,
      required: true
    },
    headers: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['add-item', 'update-item', 'delete-item', 'fetch-items'],
  data() {
    return {
      dialog: false,
      deleteDialog: false,
      qrDialog: false,
      editingId: null,
      itemToDelete: null,
      selectedQRCode: null,
      imageFile: [],
      previewImage: null,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success',
      formData: {
        codeBarre: '',
        nom: '',
        description: '',
        prix: 0,
        quantite: 0,
        unite: '',
        famille: '',
        fournisseur: '',
        image: ''
      }
    }
  },
  methods: {
    openAddDialog() {
      this.editingId = null
      this.resetForm()
      this.imageFile = []
      this.previewImage = null
      this.dialog = true
    },
    editItem(item) {
      console.log('Editing item:', item);
      this.editingId = item.id
      this.formData = { ...item }
      this.imageFile = []
      this.previewImage = null
      this.dialog = true
    },
    deleteItem(item) {
      this.itemToDelete = item
      this.deleteDialog = true
    },
    confirmDelete() {
      this.$emit('delete-item', this.itemToDelete.id)
      this.deleteDialog = false
      this.itemToDelete = null
    },
    saveItem() {
      if (!this.formData.codeBarre || !this.formData.nom) {
        this.showNotification('Le code barre et le nom sont obligatoires', 'error');
        return;
      }
      
      if (this.formData.image) {
        console.log('Image length:', this.formData.image.length);
        if (this.formData.image.length > 1000000) { // arbitrary 1MB check
           console.warn('Large image detected, might cause issues on some databases');
        }
      }

      if (this.editingId) {
        this.$emit('update-item', { id: this.editingId, data: this.formData })
      } else {
        this.$emit('add-item', this.formData)
      }
      this.dialog = false
      this.resetForm()
    },
    resetForm() {
      this.formData = {
        codeBarre: '',
        nom: '',
        description: '',
        prix: 0,
        quantite: 0,
        unite: '',
        famille: '',
        fournisseur: '',
        image: ''
      }
      this.imageFile = []
      this.previewImage = null
    },
    onImageSelected() {
      // imageFile is an array in Vuetify 3 v-file-input
      if (this.imageFile && this.imageFile.length > 0) {
        console.log('Image selected, processing...');
        const file = this.imageFile[0]
        const reader = new FileReader()
        reader.onload = (e) => {
          this.formData.image = e.target.result
          this.previewImage = e.target.result
          console.log('Image processed, Base64 length:', this.formData.image.length);
        }
        reader.readAsDataURL(file)
      } else {
        console.warn('No image file found in selection');
      }
    },
    showQRDialog(item) {
      this.selectedQRCode = item.qrCode
      this.qrDialog = true
    },
    downloadQRCode() {
      const link = document.createElement('a')
      link.href = this.selectedQRCode
      link.download = 'qrcode.png'
      link.click()
    },
    showNotification(message, color = 'success') {
      this.snackbarMessage = message
      this.snackbarColor = color
      this.snackbar = true
    },
    formatPrice(price) {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(price)
    }
  }
}
</script>

<style scoped>
.article-management {
  padding: 20px;
}
</style>
