<template>
  <v-container class="pa-4">
    <v-card>
      <v-card-title>
        <v-row class="align-center">
          <v-col cols="6">
            <h2>Physical Inventory</h2>
          </v-col>
          <v-col cols="6" class="text-right">
            <v-btn color="primary" @click="openAddDialog">
              <v-icon small>mdi-plus</v-icon> New Inventory Count
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>

      <v-card-text>
        <!-- Search & Filter -->
        <v-row class="mb-4">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="searchQuery"
              label="Search by reference"
              prepend-icon="mdi-magnify"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="filterStatus"
              :items="['All', 'PLANNED', 'IN_PROGRESS', 'COMPLETED', 'VALIDATED']"
              label="Filter by Status"
              outlined
              dense
            />
          </v-col>
        </v-row>

        <!-- Statistics -->
        <v-row class="mb-4">
          <v-col cols="12" md="3">
            <v-stat-card
              title="Total Inventories"
              :value="inventories.length"
              color="blue"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="In Progress"
              :value="inventories.filter(i => i.status === 'IN_PROGRESS').length"
              color="orange"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="Completed"
              :value="inventories.filter(i => i.status === 'COMPLETED').length"
              color="green"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="Variances"
              :value="totalVariances"
              color="red"
            />
          </v-col>
        </v-row>

        <!-- Data Table -->
        <v-data-table
          :headers="headers"
          :items="filteredInventories"
          :search="searchQuery"
          class="elevation-1"
        >
          <template v-slot:item.date="{ item }">
            {{ formatDate(item.date) }}
          </template>
          <template v-slot:item.status="{ item }">
            <v-chip :color="getStatusColor(item.status)" text-color="white" small>
              {{ item.status }}
            </v-chip>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn
              icon
              small
              color="info"
              @click="viewDetails(item)"
            >
              <v-icon>mdi-eye</v-icon>
            </v-btn>
            <v-btn
              v-if="item.status === 'IN_PROGRESS'"
              icon
              small
              color="success"
              @click="completeInventory(item.id)"
            >
              <v-icon>mdi-check</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Add Dialog -->
    <v-dialog v-model="showDialog" max-width="600px">
      <v-card>
        <v-card-title>Create Inventory Count</v-card-title>
        <v-form ref="form" @submit.prevent="saveInventory">
          <v-card-text>
            <v-text-field
              v-model="editingInventory.date"
              label="Inventory Date"
              type="date"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-select
              v-model="editingInventory.status"
              :items="['PLANNED', 'IN_PROGRESS']"
              label="Status"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-textarea
              v-model="editingInventory.notes"
              label="Notes"
              outlined
              dense
              rows="4"
            />
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn @click="closeDialog" text>Cancel</v-btn>
            <v-btn type="submit" color="primary">Create</v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </v-dialog>

    <!-- Details Dialog -->
    <v-dialog v-model="showDetailsDialog" max-width="800px">
      <v-card>
        <v-card-title>Inventory Details</v-card-title>
        <v-card-text>
          <v-row v-if="selectedInventory" class="mb-4">
            <v-col cols="6">
              <strong>Status:</strong> {{ selectedInventory.status }}
            </v-col>
            <v-col cols="6">
              <strong>Date:</strong> {{ formatDate(selectedInventory.date) }}
            </v-col>
          </v-row>
          <v-data-table
            :headers="itemHeaders"
            :items="inventoryItems"
            class="elevation-1"
          >
            <template v-slot:item.variance="{ item }">
              <v-chip
                :color="item.variance === 0 ? 'green' : 'red'"
                text-color="white"
                small
              >
                {{ item.variance }}
              </v-chip>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="closeDetailsDialog" text>Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import InventoryService from '../services/InventoryService'

export default {
  name: 'InventoryManagement',
  data() {
    return {
      headers: [
        { text: 'ID', value: 'id' },
        { text: 'Date', value: 'date' },
        { text: 'Status', value: 'status' },
        { text: 'Created By', value: 'createdBy' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      itemHeaders: [
        { text: 'Item', value: 'consumableId' },
        { text: 'Theoretical Qty', value: 'theoreticalQty' },
        { text: 'Physical Qty', value: 'physicalQty' },
        { text: 'Variance', value: 'variance' }
      ],
      inventories: [],
      inventoryItems: [],
      searchQuery: '',
      filterStatus: 'All',
      showDialog: false,
      showDetailsDialog: false,
      selectedInventory: null,
      editingInventory: {
        date: new Date().toISOString().split('T')[0],
        status: 'PLANNED',
        notes: ''
      }
    }
  },
  computed: {
    filteredInventories() {
      return this.inventories.filter(inventory => {
        const matchStatus = this.filterStatus === 'All' || inventory.status === this.filterStatus
        return matchStatus
      })
    },
    totalVariances() {
      return this.inventoryItems.reduce((sum, item) => sum + Math.abs(item.variance || 0), 0)
    }
  },
  methods: {
    async loadInventories() {
      try {
        this.inventories = await InventoryService.getInventories()
      } catch (error) {
        console.error('Error loading inventories:', error)
      }
    },
    openAddDialog() {
      this.editingInventory = {
        date: new Date().toISOString().split('T')[0],
        status: 'PLANNED',
        notes: ''
      }
      this.showDialog = true
    },
    async saveInventory() {
      try {
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        const createdBy = user.id || 1
        await InventoryService.createInventory(createdBy)
        this.closeDialog()
        this.loadInventories()
      } catch (error) {
        console.error('Error saving inventory:', error)
      }
    },
    async viewDetails(inventory) {
      try {
        this.selectedInventory = inventory
        this.inventoryItems = await InventoryService.getInventoryItems(inventory.id)
        this.showDetailsDialog = true
      } catch (error) {
        console.error('Error loading inventory details:', error)
      }
    },
    async completeInventory(id) {
      try {
        await InventoryService.completeInventory(id)
        this.loadInventories()
      } catch (error) {
        console.error('Error completing inventory:', error)
      }
    },
    closeDialog() {
      this.showDialog = false
    },
    closeDetailsDialog() {
      this.showDetailsDialog = false
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString()
    },
    getStatusColor(status) {
      const colors = {
        'PLANNED': 'gray',
        'IN_PROGRESS': 'orange',
        'COMPLETED': 'blue',
        'VALIDATED': 'green'
      }
      return colors[status] || 'gray'
    }
  },
  mounted() {
    this.loadInventories()
  }
}
</script>
