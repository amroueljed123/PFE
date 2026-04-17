<template>
  <div class="purchase-order-management">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-card-title>
              <v-icon left>mdi-file-document-outline</v-icon>
              Gestion des Commandes d'Achat
            </v-card-title>
            <v-divider></v-divider>
            
            <v-card-text>
              <v-row class="mb-4">
                <v-col cols="12" md="8">
                  <v-text-field v-model="searchQuery" label="Rechercher une commande" prepend-icon="mdi-magnify"></v-text-field>
                </v-col>
                <v-col cols="12" md="4">
                  <v-select v-model="statusFilter" :items="statuses" label="Filtrer par statut" clearable></v-select>
                </v-col>
              </v-row>

              <v-row class="mb-4">
                <v-col cols="12" md="12">
                  <v-btn color="primary" @click="dialogOpen = true">
                    <v-icon left>mdi-plus</v-icon>
                    Nouvelle Commande
                  </v-btn>
                </v-col>
              </v-row>

              <v-data-table
                :headers="headers"
                :items="filteredOrders"
                class="elevation-1"
              >
                <template v-slot:item.status="{ item }">
                  <v-chip :color="getStatusColor(item.status)" text-color="white">
                    {{ item.status }}
                  </v-chip>
                </template>
                <template v-slot:item.totalAmount="{ item }">
                  {{ item.totalAmount.toLocaleString() }} DA
                </template>
                <template v-slot:item.actions="{ item }">
                  <v-btn icon small color="primary" @click="viewOrder(item)">
                    <v-icon>mdi-eye</v-icon>
                  </v-btn>
                  <v-btn v-if="item.status === 'DRAFT'" icon small color="warning" @click="editOrder(item)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Dialog for creating/editing orders -->
      <v-dialog v-model="dialogOpen" max-width="700px">
        <v-card>
          <v-card-title>{{ editingOrder ? 'Modifier Commande' : 'Nouvelle Commande' }}</v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-form ref="form" v-model="formValid">
              <v-text-field v-model="form.orderNumber" label="Numéro de Commande" required></v-text-field>
              <v-autocomplete v-model="form.supplierId" :items="suppliers" item-text="name" item-value="id" label="Fournisseur" required></v-autocomplete>
              <v-text-field v-model.number="form.totalAmount" label="Montant Total" type="number" required></v-text-field>
              <v-select v-model="form.status" :items="['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED']" label="Statut"></v-select>
            </v-form>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="secondary" @click="dialogOpen = false">Annuler</v-btn>
            <v-btn color="primary" @click="saveOrder" :loading="saving">Enregistrer</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </div>
</template>

<script>
import SupplyChainService from '@/services/SupplyChainService';

export default {
  name: 'PurchaseOrderManagement',
  data() {
    return {
      orders: [],
      suppliers: [],
      searchQuery: '',
      statusFilter: '',
      dialogOpen: false,
      editingOrder: null,
      formValid: false,
      saving: false,
      statuses: ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'COMPLETED', 'CANCELLED'],
      headers: [
        { text: 'N° Commande', value: 'orderNumber' },
        { text: 'Fournisseur', value: 'supplierName' },
        { text: 'Date', value: 'date' },
        { text: 'Montant', value: 'totalAmount' },
        { text: 'Statut', value: 'status' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      form: {
        orderNumber: '',
        supplierId: null,
        totalAmount: 0,
        status: 'DRAFT'
      }
    };
  },
  computed: {
    filteredOrders() {
      return this.orders.filter(o => {
        const matchesSearch = o.orderNumber.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesStatus = !this.statusFilter || o.status === this.statusFilter;
        return matchesSearch && matchesStatus;
      });
    }
  },
  methods: {
    async loadOrders() {
      try {
        this.orders = await SupplyChainService.getAllPurchaseOrders();
      } catch (error) {
        console.error(error);
      }
    },
    async loadSuppliers() {
      try {
        this.suppliers = await SupplyChainService.getAllSuppliers();
      } catch (error) {
        console.error(error);
      }
    },
    async saveOrder() {
      if (!this.$refs.form.validate()) return;
      this.saving = true;
      try {
        if (this.editingOrder) {
          this.form.id = this.editingOrder.id;
          await SupplyChainService.updatePurchaseOrder(this.form);
        } else {
          await SupplyChainService.createPurchaseOrder(this.form);
        }
        this.dialogOpen = false;
        this.resetForm();
        this.loadOrders();
      } catch (error) {
        console.error(error);
      } finally {
        this.saving = false;
      }
    },
    editOrder(order) {
      this.editingOrder = order;
      this.form = { ...order };
      this.dialogOpen = true;
    },
    viewOrder(order) {
      this.$router.push(`/admin/purchase-order/${order.id}`);
    },
    getStatusColor(status) {
      const colors = {
        'DRAFT': '#808080',
        'SUBMITTED': '#2196F3',
        'APPROVED': '#4CAF50',
        'REJECTED': '#F44336',
        'COMPLETED': '#00BCD4',
        'CANCELLED': '#FF9800'
      };
      return colors[status] || '#9C27B0';
    },
    resetForm() {
      this.form = { orderNumber: '', supplierId: null, totalAmount: 0, status: 'DRAFT' };
      this.editingOrder = null;
    }
  },
  mounted() {
    this.loadOrders();
    this.loadSuppliers();
  }
};
</script>

<style scoped>
.purchase-order-management {
  padding: 20px;
}
</style>
