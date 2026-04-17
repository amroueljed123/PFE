<template>
  <v-container class="pa-4">
    <v-card>
      <v-card-title>
        <v-row class="align-center">
          <v-col cols="6">
            <h2>Invoice Management</h2>
          </v-col>
          <v-col cols="6" class="text-right">
            <v-btn color="primary" @click="openAddDialog">
              <v-icon small>mdi-plus</v-icon> New Invoice
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>

      <v-card-text>
        <!-- Search & Filter -->
        <v-row class="mb-4">
          <v-col cols="12" md="4">
            <v-text-field
              v-model="searchQuery"
              label="Search by Invoice #"
              prepend-icon="mdi-magnify"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="filterStatus"
              :items="['All', 'PENDING', 'PROCESSED', 'VERIFIED']"
              label="Filter by Status"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="filterSupplier"
              :items="suppliers"
              :item-title="(s) => { const c = s.supplierCode || `SUP-${String(s.id).padStart(4, '0')}`; return `[${c}] ${s.name}`; }"
              item-value="id"
              label="Filter by Supplier"
              outlined
              dense
              clearable
            />
          </v-col>
        </v-row>

        <!-- Data Table -->
        <v-data-table
          :headers="headers"
          :items="filteredInvoices"
          :search="searchQuery"
          class="elevation-1"
        >
          <template v-slot:item.date="{ item }">
            {{ formatDate(item.date) }}
          </template>
          <template v-slot:item.supplierId="{ item }">
            {{ getSupplierName(item.supplierId) }}
          </template>
          <template v-slot:item.amountTTC="{ item }">
            {{ formatCurrency(item.amountTTC) }}
          </template>
          <template v-slot:item.status="{ item }">
            <v-chip :color="getStatusColor(item.status)" text-color="white" small>
              {{ item.status }}
            </v-chip>
          </template>
          <template v-slot:item.ocrConfidence="{ item }">
            <v-progress-linear
              :value="item.ocrConfidence || 0"
              :color="item.ocrConfidence >= 85 ? 'green' : 'orange'"
            />
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn
              icon
              small
              color="info"
              @click="editInvoice(item)"
            >
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn
              icon
              small
              color="error"
              @click="deleteInvoice(item.id)"
            >
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Add/Edit Dialog -->
    <v-dialog v-model="showDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editingInvoice.id ? 'Edit Invoice' : 'Create Invoice' }}
        </v-card-title>
        <v-form ref="form" @submit.prevent="saveInvoice">
          <v-card-text>
            <v-text-field
              v-model="editingInvoice.invoiceNumber"
              label="Invoice Number"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-select
              v-model="editingInvoice.supplierId"
              :items="suppliers"
              :item-title="(s) => { const c = s.supplierCode || `SUP-${String(s.id).padStart(4, '0')}`; return `[${c}] ${s.name}`; }"
              item-value="id"
              label="Supplier"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-text-field
              v-model="editingInvoice.date"
              label="Invoice Date"
              type="date"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-text-field
              v-model.number="editingInvoice.amountHT"
              label="Amount HT (DA)"
              type="number"
              step="0.01"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-text-field
              v-model.number="editingInvoice.tva"
              label="TVA (DA)"
              type="number"
              step="0.01"
              outlined
              dense
              class="mb-3"
            />
            <v-text-field
              v-model.number="editingInvoice.amountTTC"
              label="Amount TTC (DA)"
              type="number"
              step="0.01"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-file-input
              v-model="editingInvoice.file"
              label="Invoice File"
              outlined
              dense
              class="mb-3"
              accept=".pdf,.jpg,.png"
              :loading="isExtracting"
              @update:modelValue="handleFileUpload"
            />
            <v-select
              v-model="editingInvoice.status"
              :items="['PENDING', 'PROCESSED', 'VERIFIED']"
              label="Status"
              outlined
              dense
              class="mb-3"
            />
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn @click="closeDialog" text>Cancel</v-btn>
            <v-btn type="submit" color="primary">Save</v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>

    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" location="top right">
      {{ snackbarMessage }}
      <template v-slot:actions>
        <v-btn icon @click="snackbar = false"><v-icon>mdi-close</v-icon></v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import InvoiceService from '../services/InvoiceService'
import SupplyChainService from '../services/SupplyChainService'

export default {
  name: 'InvoiceManagement',
  data() {
    return {
      headers: [
        { text: 'Invoice #', value: 'invoiceNumber' },
        { text: 'Supplier', value: 'supplierId' },
        { text: 'Date', value: 'date' },
        { text: 'Amount (DA)', value: 'amountTTC' },
        { text: 'Status', value: 'status' },
        { text: 'OCR Confidence', value: 'ocrConfidence' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      invoices: [],
      suppliers: [],
      searchQuery: '',
      filterStatus: 'All',
      filterSupplier: null,
      showDialog: false,
      editingInvoice: {
        invoiceNumber: '',
        supplierId: null,
        date: new Date().toISOString().split('T')[0],
        amountHT: 0,
        tva: 0,
        amountTTC: 0,
        status: 'PENDING',
        file: null,
        ocrConfidence: 0
      },
      isExtracting: false,
      lastOcrResult: null,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success'
    }
  },
  computed: {
    filteredInvoices() {
      return this.invoices.filter(invoice => {
        const matchStatus = this.filterStatus === 'All' || invoice.status === this.filterStatus
        const matchSupplier = !this.filterSupplier || invoice.supplierId === this.filterSupplier
        return matchStatus && matchSupplier
      })
    }
  },
  methods: {
    async loadInvoices() {
      try {
        this.invoices = await InvoiceService.getAllInvoices()
        this.loadSuppliers()
      } catch (error) {
        console.error('Error loading invoices:', error)
      }
    },
    async loadSuppliers() {
      try {
        this.suppliers = await SupplyChainService.getAllSuppliers()
      } catch (error) {
        console.error('Error loading suppliers:', error)
      }
    },
    openAddDialog() {
      this.editingInvoice = {
        invoiceNumber: '',
        supplierId: null,
        date: new Date().toISOString().split('T')[0],
        amountHT: 0,
        tva: 0,
        amountTTC: 0,
        status: 'PENDING',
        file: null
      }
      this.showDialog = true
    },
    editInvoice(invoice) {
      this.editingInvoice = { ...invoice }
      this.showDialog = true
    },
    async saveInvoice() {
      if (!this.editingInvoice.supplierId) {
        this.showNotification('Please select a Supplier before saving!', 'error')
        return
      }
      try {
        if (this.editingInvoice.id) {
          await InvoiceService.updateInvoice(this.editingInvoice.id, this.editingInvoice)
        } else if (this.lastOcrResult && this.lastOcrResult.articles && this.lastOcrResult.articles.length > 0) {
          // Use the ingest endpoint to auto-create articles in their dedicated DB tables
          const ocrPayload = { ...this.lastOcrResult }
          // Override with any user-corrected values from the form
          ocrPayload.invoiceNumber = this.editingInvoice.invoiceNumber
          ocrPayload.supplierId = this.editingInvoice.supplierId
          ocrPayload.amountHT = this.editingInvoice.amountHT
          ocrPayload.tva = this.editingInvoice.tva
          ocrPayload.amountTTC = this.editingInvoice.amountTTC
          await InvoiceService.ingestOcrResult(ocrPayload)
          this.lastOcrResult = null
          this.showNotification(`Invoice saved + ${ocrPayload.articles.length} articles auto-added to inventory!`, 'success')
        } else {
          await InvoiceService.createInvoice(this.editingInvoice)
        }
        this.closeDialog()
        this.loadInvoices()
        this.showNotification('Invoice saved successfully', 'success')
      } catch (error) {
        console.error('Error saving invoice:', error)
        let errorMsg = 'Error saving invoice'
        if (error.response && error.response.data && error.response.data.details) {
          // If the DB complains about a duplicate ("rompt la contrainte unique"), format it beautifully for the user
          if (error.response.data.details.includes('dupliquée') || error.response.data.details.includes('existe déjà')) {
            errorMsg = 'This Invoice Number already exists in the system!'
          } else {
            errorMsg = error.response.data.details
          }
        }
        this.showNotification(errorMsg, 'error')
      }
    },
    async handleFileUpload(files) {
      // Vuetify 3 v-file-input passes the files array or single file.
      let file = Array.isArray(files) ? files[0] : files;
      if (!file) return;

      this.isExtracting = true;
      this.showNotification('Smart AI is reading the invoice...', 'info');

      try {
        const result = await InvoiceService.extractDataFromInvoice(file);
        
        // Auto-fill the form with OCR results
        if (result.invoiceNumber) this.editingInvoice.invoiceNumber = result.invoiceNumber;
        if (result.date) this.editingInvoice.date = result.date;
        if (result.supplierId) {
          // Verify that this supplier actually exists in our dropdown list
          const exists = this.suppliers.some(s => s.id === result.supplierId);
          if (exists) this.editingInvoice.supplierId = result.supplierId;
        }
        if (result.amountTTC) this.editingInvoice.amountTTC = result.amountTTC;
        if (result.amountHT) this.editingInvoice.amountHT = result.amountHT;
        if (result.tva) this.editingInvoice.tva = result.tva;
        
        this.editingInvoice.ocrConfidence = result.confidence;
        this.lastOcrResult = result;

        if (result.confidence > 70) {
          this.showNotification(`Data extracted successfully! (Confidence: ${result.confidence}%)`, 'success');
        } else if (result.confidence > 30) {
          this.showNotification('Data partially extracted, please verify fields.', 'warning');
        } else {
          this.showNotification('Could not extract data well. Please fill manually.', 'error');
        }
      } catch (error) {
        console.error('Error extracting invoice data:', error);
        this.showNotification('AI Extraction failed.', 'error');
      } finally {
        this.isExtracting = false;
      }
    },
    async deleteInvoice(id) {
      if (confirm('Are you sure?')) {
        try {
          await InvoiceService.deleteInvoice(id)
          this.loadInvoices()
        } catch (error) {
          console.error('Error deleting invoice:', error)
        }
      }
    },
    closeDialog() {
      this.showDialog = false
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString()
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('fr-DZ', {
        style: 'currency',
        currency: 'DZD'
      }).format(value || 0)
    },
    getStatusColor(status) {
      const colors = {
        'PENDING': 'orange',
        'PROCESSED': 'blue',
        'VERIFIED': 'green'
      }
      return colors[status] || 'gray'
    },
    getSupplierName(supplierData) {
      if (!supplierData) return 'N/A';
      
      const formatDisplay = (s) => {
        let name = s.name || s.nom || JSON.stringify(s);
        let code = s.supplierCode || `SUP-${String(s.id).padStart(4, '0')}`;
        return `[${code}] ${name}`;
      };

      // If the backend returns the entire object
      if (typeof supplierData === 'object') {
        return formatDisplay(supplierData);
      }
      // If it's an ID, lookup in suppliers array
      const supplier = this.suppliers.find(s => s.id === supplierData);
      return supplier ? formatDisplay(supplier) : supplierData;
    },
    showNotification(message, color = 'success') {
      this.snackbarMessage = message;
      this.snackbarColor = color;
      this.snackbar = true;
    }
  },
  mounted() {
    this.loadInvoices()
  }
}
</script>
