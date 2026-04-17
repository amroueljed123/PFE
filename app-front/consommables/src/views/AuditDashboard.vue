<template>
  <v-container class="pa-4">
    <v-card>
      <v-card-title>
        <h2>Audit Trail Log</h2>
      </v-card-title>

      <v-card-text>
        <!-- Search & Filter -->
        <v-row class="mb-4">
          <v-col cols="12" md="3">
            <v-text-field
              v-model="searchQuery"
              label="Search by action"
              prepend-icon="mdi-magnify"
              outlined
              dense
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="filterUser"
              :items="users"
              item-text="username"
              item-value="id"
              label="Filter by User"
              outlined
              dense
              clearable
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-select
              v-model="filterEntity"
              :items="entityTypes"
              label="Filter by Entity"
              outlined
              dense
              clearable
            />
          </v-col>
          <v-col cols="12" md="3">
            <v-text-field
              v-model="dateRange"
              label="Date Range"
              type="date"
              outlined
              dense
            />
          </v-col>
        </v-row>

        <!-- Statistics -->
        <v-row class="mb-4">
          <v-col cols="12" md="4">
            <v-stat-card
              title="Total Actions"
              :value="auditLogs.length"
              color="blue"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-stat-card
              title="Active Users"
              :value="uniqueUsers"
              color="green"
            />
          </v-col>
          <v-col cols="12" md="4">
            <v-stat-card
              title="Monitored Entities"
              :value="uniqueEntities"
              color="purple"
            />
          </v-col>
        </v-row>

        <!-- Data Table -->
        <v-data-table
          :headers="headers"
          :items="filteredLogs"
          :search="searchQuery"
          :sort-by="['timestamp']"
          :sort-desc="true"
          class="elevation-1"
        >
          <template v-slot:item.timestamp="{ item }">
            {{ formatDateTime(item.timestamp) }}
          </template>
          <template v-slot:item.action="{ item }">
            <v-chip :color="getActionColor(item.action)" text-color="white" small>
              {{ item.action }}
            </v-chip>
          </template>
          <template v-slot:item.entity="{ item }">
            <v-chip color="blue" text-color="white" small>
              {{ item.entity }}
            </v-chip>
          </template>
          <template v-slot:item.details="{ item }">
            <v-btn
              icon
              x-small
              @click="showDetails(item)"
            >
              <v-icon>mdi-eye</v-icon>
            </v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Details Dialog -->
    <v-dialog v-model="showDetailsDialog" max-width="700px">
      <v-card>
        <v-card-title>Audit Log Details</v-card-title>
        <v-card-text v-if="selectedLog">
          <v-simple-table>
            <tbody>
              <tr>
                <td><strong>User ID:</strong></td>
                <td>{{ selectedLog.userId }}</td>
              </tr>
              <tr>
                <td><strong>Action:</strong></td>
                <td>{{ selectedLog.action }}</td>
              </tr>
              <tr>
                <td><strong>Entity:</strong></td>
                <td>{{ selectedLog.entity }}</td>
              </tr>
              <tr>
                <td><strong>Timestamp:</strong></td>
                <td>{{ formatDateTime(selectedLog.timestamp) }}</td>
              </tr>
              <tr v-if="selectedLog.ipAddress">
                <td><strong>IP Address:</strong></td>
                <td>{{ selectedLog.ipAddress }}</td>
              </tr>
              <tr v-if="selectedLog.oldValue">
                <td><strong>Old Value:</strong></td>
                <td>{{ selectedLog.oldValue }}</td>
              </tr>
              <tr v-if="selectedLog.newValue">
                <td><strong>New Value:</strong></td>
                <td>{{ selectedLog.newValue }}</td>
              </tr>
            </tbody>
          </v-simple-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="showDetailsDialog = false" text>Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
export default {
  name: 'AuditDashboard',
  data() {
    return {
      headers: [
        { text: 'Timestamp', value: 'timestamp' },
        { text: 'User ID', value: 'userId' },
        { text: 'Action', value: 'action' },
        { text: 'Entity', value: 'entity' },
        { text: 'Details', value: 'details', sortable: false }
      ],
      auditLogs: [],
      users: [],
      entityTypes: ['User', 'PurchaseOrder', 'Invoice', 'Vehicle', 'Inventory', 'StockMovement'],
      searchQuery: '',
      filterUser: null,
      filterEntity: null,
      dateRange: new Date().toISOString().split('T')[0],
      showDetailsDialog: false,
      selectedLog: null,
      sampleLogs: [
        { id: 1, userId: 1, action: 'CREATE', entity: 'PurchaseOrder', timestamp: new Date().toISOString(), oldValue: null, newValue: 'Order #001' },
        { id: 2, userId: 2, action: 'UPDATE', entity: 'Invoice', timestamp: new Date(Date.now() - 3600000).toISOString(), oldValue: 'PENDING', newValue: 'PROCESSED' },
        { id: 3, userId: 1, action: 'DELETE', entity: 'StockMovement', timestamp: new Date(Date.now() - 7200000).toISOString(), oldValue: 'Movement #100', newValue: null },
        { id: 4, userId: 3, action: 'CREATE', entity: 'Inventory', timestamp: new Date(Date.now() - 10800000).toISOString(), oldValue: null, newValue: 'Inventory #2024-01' },
        { id: 5, userId: 2, action: 'UPDATE', entity: 'Vehicle', timestamp: new Date(Date.now() - 14400000).toISOString(), oldValue: 'Status: Active', newValue: 'Status: Maintenance' }
      ]
    }
  },
  computed: {
    filteredLogs() {
      return this.auditLogs.filter(log => {
        const matchUser = !this.filterUser || log.userId === this.filterUser
        const matchEntity = !this.filterEntity || log.entity === this.filterEntity
        return matchUser && matchEntity
      })
    },
    uniqueUsers() {
      return new Set(this.auditLogs.map(log => log.userId)).size
    },
    uniqueEntities() {
      return new Set(this.auditLogs.map(log => log.entity)).size
    }
  },
  methods: {
    loadAuditLogs() {
      // Simulate loading - in real implementation, call API service
      this.auditLogs = this.sampleLogs
    },
    showDetails(log) {
      this.selectedLog = log
      this.showDetailsDialog = true
    },
    formatDateTime(datetime) {
      return new Date(datetime).toLocaleString('fr-DZ', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    getActionColor(action) {
      const colors = {
        'CREATE': 'green',
        'UPDATE': 'blue',
        'DELETE': 'red',
        'VIEW': 'gray'
      }
      return colors[action] || 'gray'
    }
  },
  mounted() {
    this.loadAuditLogs()
  }
}
</script>
