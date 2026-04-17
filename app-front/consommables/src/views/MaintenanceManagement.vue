<template>
  <v-container class="pa-4">
    <v-card>
      <v-card-title>
        <v-row class="align-center">
          <v-col cols="6">
            <h2>Maintenance Planning</h2>
          </v-col>
          <v-col cols="6" class="text-right">
            <v-btn color="primary" @click="openAddDialog">
              <v-icon small>mdi-plus</v-icon> Schedule Maintenance
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
              label="Search by vehicle/equipment"
              prepend-icon="mdi-magnify"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="filterType"
              :items="['All', 'ROUTINE', 'PREVENTIVE', 'CORRECTIVE']"
              label="Maintenance Type"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-select
              v-model="filterStatus"
              :items="['All', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED']"
              label="Status"
              outlined
              dense
            />
          </v-col>
        </v-row>

        <!-- Statistics Cards -->
        <v-row class="mb-4">
          <v-col cols="12" md="3">
            <v-stat-card
              title="Total Maintenance"
              :value="maintenances.length"
              color="blue"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="Scheduled"
              :value="maintenances.filter(m => m.status === 'SCHEDULED').length"
              color="orange"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="Completed"
              :value="maintenances.filter(m => m.status === 'COMPLETED').length"
              color="green"
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-stat-card
              title="Total Cost"
              :value="formatCurrency(totalCost)"
              color="purple"
            />
          </v-col>
        </v-row>

        <!-- Data Table -->
        <v-data-table
          :headers="headers"
          :items="filteredMaintenances"
          :search="searchQuery"
          class="elevation-1"
        >
          <template v-slot:item.date="{ item }">
            {{ formatDate(item.date) }}
          </template>
          <template v-slot:item.nextDate="{ item }">
            {{ formatDate(item.nextDate) }}
          </template>
          <template v-slot:item.type="{ item }">
            <v-chip :color="getTypeColor(item.type)" text-color="white" small>
              {{ item.type }}
            </v-chip>
          </template>
          <template v-slot:item.cost="{ item }">
            {{ formatCurrency(item.cost) }}
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn
              icon
              small
              color="info"
              @click="editMaintenance(item)"
            >
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn
              icon
              small
              color="success"
              @click="completeMaintenance(item.id)"
            >
              <v-icon>mdi-check</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Add/Edit Dialog -->
    <v-dialog v-model="showDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ editingMaintenance.id ? 'Edit Maintenance' : 'Schedule Maintenance' }}
        </v-card-title>
        <v-form ref="form" @submit.prevent="saveMaintenance">
          <v-card-text>
            <v-select
              v-model="editingMaintenance.articleId"
              :items="vehicles"
              item-text="registration"
              item-value="id"
              label="Vehicle/Equipment"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-select
              v-model="editingMaintenance.type"
              :items="['ROUTINE', 'PREVENTIVE', 'CORRECTIVE']"
              label="Maintenance Type"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-text-field
              v-model="editingMaintenance.date"
              label="Maintenance Date"
              type="date"
              outlined
              dense
              class="mb-3"
              required
            />
            <v-text-field
              v-model.number="editingMaintenance.cost"
              label="Cost (DA)"
              type="number"
              step="0.01"
              outlined
              dense
              class="mb-3"
            />
            <v-text-field
              v-model="editingMaintenance.nextDate"
              label="Next Maintenance Date"
              type="date"
              outlined
              dense
              class="mb-3"
            />
            <v-textarea
              v-model="editingMaintenance.description"
              label="Description"
              outlined
              dense
              rows="3"
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
  </v-container>
</template>

<script>
import VehicleService from '../services/VehicleService'

export default {
  name: 'MaintenanceManagement',
  data() {
    return {
      headers: [
        { text: 'Vehicle/Equipment', value: 'articleId' },
        { text: 'Type', value: 'type' },
        { text: 'Date', value: 'date' },
        { text: 'Cost (DA)', value: 'cost' },
        { text: 'Next Date', value: 'nextDate' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      maintenances: [],
      vehicles: [],
      searchQuery: '',
      filterType: 'All',
      filterStatus: 'All',
      showDialog: false,
      editingMaintenance: {
        articleId: null,
        date: new Date().toISOString().split('T')[0],
        type: 'ROUTINE',
        description: '',
        cost: 0,
        nextDate: null
      }
    }
  },
  computed: {
    filteredMaintenances() {
      return this.maintenances.filter(maintenance => {
        const matchType = this.filterType === 'All' || maintenance.type === this.filterType
        return matchType
      })
    },
    totalCost() {
      return this.maintenances.reduce((sum, m) => sum + (m.cost || 0), 0)
    }
  },
  methods: {
    async loadMaintenances() {
      try {
        this.maintenances = await VehicleService.getAllMaintenance()
        this.loadVehicles()
      } catch (error) {
        console.error('Error loading maintenances:', error)
      }
    },
    async loadVehicles() {
      try {
        this.vehicles = await VehicleService.getAllVehicles()
      } catch (error) {
        console.error('Error loading vehicles:', error)
      }
    },
    openAddDialog() {
      this.editingMaintenance = {
        articleId: null,
        date: new Date().toISOString().split('T')[0],
        type: 'ROUTINE',
        description: '',
        cost: 0,
        nextDate: null
      }
      this.showDialog = true
    },
    editMaintenance(maintenance) {
      this.editingMaintenance = { ...maintenance }
      this.showDialog = true
    },
    async saveMaintenance() {
      try {
        if (this.editingMaintenance.id) {
          // Update not implemented in service yet
        } else {
          const m = this.editingMaintenance;
          // Format nextDate to yyyy-MM-ddTHH:mm:ss for LocalDateTime.parse compatibility
          const nextDateParam = m.nextDate ? `${m.nextDate}T00:00:00` : new Date().toISOString().split('.')[0];
          await VehicleService.scheduleMaintenance(m.articleId, m.type, m.cost || 0, nextDateParam);
        }
        this.closeDialog()
        this.loadMaintenances()
      } catch (error) {
        console.error('Error saving maintenance:', error)
      }
    },
    async completeMaintenance(id) {
      try {
        await VehicleService.recordMaintenance(id, new Date().toISOString())
        this.loadMaintenances()
      } catch (error) {
        console.error('Error completing maintenance:', error)
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
    getTypeColor(type) {
      const colors = {
        'ROUTINE': 'blue',
        'PREVENTIVE': 'orange',
        'CORRECTIVE': 'red'
      }
      return colors[type] || 'gray'
    }
  },
  mounted() {
    this.loadMaintenances()
  }
}
</script>
