<template>
  <div class="supplier-management">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-card-title>
              <v-icon left>mdi-truck</v-icon>
              Gestion des Fournisseurs
            </v-card-title>
            <v-divider></v-divider>
            
            <v-card-text>
              <v-row class="mb-4">
                <v-col cols="12" md="8">
                  <v-text-field v-model="searchQuery" label="Rechercher un fournisseur" prepend-icon="mdi-magnify"></v-text-field>
                </v-col>
                <v-col cols="12" md="4">
                  <v-btn color="primary" @click="dialogOpen = true" block>
                    <v-icon left>mdi-plus</v-icon>
                    Ajouter Fournisseur
                  </v-btn>
                </v-col>
              </v-row>

              <v-data-table
                :headers="headers"
                :items="filteredSuppliers"
                class="elevation-1"
              >
                <template v-slot:item.supplierCode="{ item }">
                  <v-chip color="secondary" small outlined>
                    {{ item.supplierCode || `SUP-${String(item.id).padStart(4, '0')}` }}
                  </v-chip>
                </template>
                <template v-slot:item.rating="{ item }">
                  <v-rating :value="item.rating" readonly small></v-rating>
                </template>
                <template v-slot:item.active="{ item }">
                  <v-chip :color="item.active ? 'green' : 'red'" text-color="white">
                    {{ item.active ? 'Actif' : 'Inactif' }}
                  </v-chip>
                </template>
                <template v-slot:item.actions="{ item }">
                  <v-btn icon small color="primary" @click="editSupplier(item)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn icon small color="red" @click="deleteSupplier(item.id)">
                    <v-icon>mdi-trash</v-icon>
                  </v-btn>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Dialog for adding/editing suppliers -->
      <v-dialog v-model="dialogOpen" max-width="600px">
        <v-card>
          <v-card-title>
            {{ editingSupplier ? 'Modifier Fournisseur' : 'Ajouter Fournisseur' }}
          </v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-form ref="form" v-model="formValid">
              <v-text-field v-model="form.name" label="Nom du Fournisseur" required :rules="[v => !!v || 'Requis']"></v-text-field>
              <v-text-field v-model="form.contact" label="Contact"></v-text-field>
              <v-text-field v-model="form.email" label="Email" type="email"></v-text-field>
              <v-text-field v-model="form.phone" label="Téléphone"></v-text-field>
              <v-textarea v-model="form.address" label="Adresse"></v-textarea>
              <v-rating v-model="form.rating" label="Note"></v-rating>
              <v-text-field v-model="form.paymentTerms" label="Conditions de Paiement"></v-text-field>
              <v-checkbox v-model="form.active" label="Actif"></v-checkbox>
            </v-form>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="secondary" @click="dialogOpen = false">Annuler</v-btn>
            <v-btn color="primary" @click="saveSupplier" :loading="saving">Enregistrer</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </div>
</template>

<script>
import SupplierService from '@/services/SupplierService';

export default {
  name: 'SupplierManagement',
  data() {
    return {
      suppliers: [],
      searchQuery: '',
      dialogOpen: false,
      editingSupplier: null,
      formValid: false,
      saving: false,
      headers: [
        { text: 'ID', value: 'supplierCode' },
        { text: 'Nom', value: 'name' },
        { text: 'Contact', value: 'contact' },
        { text: 'Email', value: 'email' },
        { text: 'Téléphone', value: 'phone' },
        { text: 'Note', value: 'rating' },
        { text: 'Statut', value: 'active' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      form: {
        name: '',
        contact: '',
        email: '',
        phone: '',
        address: '',
        rating: 0,
        paymentTerms: '',
        active: true
      }
    };
  },
  computed: {
    filteredSuppliers() {
      if (!this.searchQuery) return this.suppliers;
      const q = this.searchQuery.toLowerCase();
      return this.suppliers.filter(s => 
        (s.name && s.name.toLowerCase().includes(q)) ||
        (s.email && s.email.toLowerCase().includes(q)) ||
        (s.supplierCode && s.supplierCode.toLowerCase().includes(q))
      );
    }
  },
  methods: {
    async loadSuppliers() {
      try {
        this.suppliers = await SupplierService.getAllSuppliers();
      } catch (error) {
        console.error(error);
      }
    },
    async saveSupplier() {
      if (!this.$refs.form.validate()) return;
      this.saving = true;
      try {
        if (this.editingSupplier) {
          this.form.id = this.editingSupplier.id;
          await SupplierService.updateSupplier(this.form);
        } else {
          await SupplierService.createSupplier(this.form);
        }
        this.dialogOpen = false;
        this.resetForm();
        this.loadSuppliers();
      } catch (error) {
        console.error(error);
      } finally {
        this.saving = false;
      }
    },
    editSupplier(supplier) {
      this.editingSupplier = supplier;
      this.form = { ...supplier };
      this.dialogOpen = true;
    },
    async deleteSupplier(id) {
      if (!confirm('Êtes-vous sûr?')) return;
      try {
        await SupplierService.deleteSupplier(id);
        this.loadSuppliers();
      } catch (error) {
        console.error(error);
      }
    },
    resetForm() {
      this.form = {
        name: '', contact: '', email: '', phone: '', address: '',
        rating: 0, paymentTerms: '', active: true
      };
      this.editingSupplier = null;
      this.$refs.form.resetValidation();
    }
  },
  mounted() {
    this.loadSuppliers();
  }
};
</script>

<style scoped>
.supplier-management {
  padding: 20px;
}
</style>
