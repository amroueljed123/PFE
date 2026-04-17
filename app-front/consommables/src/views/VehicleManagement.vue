<template>
  <div class="vehicle-management">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-card-title>
              <v-icon left>mdi-car</v-icon>
              Gestion des Véhicules
            </v-card-title>
            <v-divider></v-divider>
            
            <v-card-text>
              <v-row class="mb-4">
                <v-col cols="12" md="8">
                  <v-text-field v-model="searchQuery" label="Rechercher un véhicule" prepend-icon="mdi-magnify"></v-text-field>
                </v-col>
                <v-col cols="12" md="4">
                  <v-select v-model="statusFilter" :items="['Actif', 'En maintenance', 'Hors service']" label="Filtrer par statut" clearable></v-select>
                </v-col>
              </v-row>

              <v-row class="mb-4">
                <v-col cols="12" md="12">
                  <v-btn color="primary" @click="dialogOpen = true">
                    <v-icon left>mdi-plus</v-icon>
                    Ajouter Véhicule
                  </v-btn>
                </v-col>
              </v-row>

              <v-data-table
                :headers="headers"
                :items="filteredVehicles"
                class="elevation-1"
              >
                <template v-slot:item.status="{ item }">
                  <v-chip :color="getStatusColor(item.status)" text-color="white" small>
                    {{ item.status }}
                  </v-chip>
                </template>
                <template v-slot:item.actions="{ item }">
                  <v-btn icon small color="primary" @click="editVehicle(item)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn icon small color="info" @click="viewMaintenance(item.id)">
                    <v-icon>mdi-wrench</v-icon>
                  </v-btn>
                  <v-btn icon small color="red" @click="deleteVehicle(item.id)">
                    <v-icon>mdi-trash</v-icon>
                  </v-btn>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Dialog for adding/editing vehicles -->
      <v-dialog v-model="dialogOpen" max-width="600px">
        <v-card>
          <v-card-title>{{ editingVehicle ? 'Modifier Véhicule' : 'Ajouter Véhicule' }}</v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-form ref="form" v-model="formValid">
              <v-text-field v-model="form.registration" label="Immatriculation" required></v-text-field>
              <v-text-field v-model="form.brand" label="Marque"></v-text-field>
              <v-text-field v-model="form.model" label="Modèle"></v-text-field>
              <v-text-field v-model.number="form.year" label="Année" type="number"></v-text-field>
              <v-select v-model="form.type" :items="['Voiture', 'Camion', 'Fourgon', 'Moto', 'Autre']" label="Type" required></v-select>
              <v-text-field v-model="form.fuelType" label="Type de Carburant"></v-text-field>
              <v-select v-model="form.status" :items="['Actif', 'En maintenance', 'Hors service']" label="Statut"></v-select>
              <v-text-field v-model.number="form.mileage" label="Kilométrage" type="number"></v-text-field>
            </v-form>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="secondary" @click="dialogOpen = false">Annuler</v-btn>
            <v-btn color="primary" @click="saveVehicle" :loading="saving">Enregistrer</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </div>
</template>

<script>
import VehicleService from '@/services/VehicleService';

export default {
  name: 'VehicleManagement',
  data() {
    return {
      vehicles: [],
      searchQuery: '',
      statusFilter: '',
      dialogOpen: false,
      editingVehicle: null,
      formValid: false,
      saving: false,
      headers: [
        { text: 'Immatriculation', value: 'registration' },
        { text: 'Marque', value: 'brand' },
        { text: 'Modèle', value: 'model' },
        { text: 'Année', value: 'year' },
        { text: 'Kilométrage', value: 'mileage' },
        { text: 'Carburant', value: 'fuelType' },
        { text: 'Statut', value: 'status' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],
      form: {
        registration: '',
        brand: '',
        model: '',
        year: new Date().getFullYear(),
        type: 'Voiture',
        fuelType: '',
        status: 'Actif',
        mileage: 0
      }
    };
  },
  computed: {
    filteredVehicles() {
      return this.vehicles.filter(v => {
        const matchesSearch = v.registration.includes(this.searchQuery) || v.brand.includes(this.searchQuery);
        const matchesStatus = !this.statusFilter || v.status === this.statusFilter;
        return matchesSearch && matchesStatus;
      });
    }
  },
  methods: {
    async loadVehicles() {
      try {
        this.vehicles = await VehicleService.getAllVehicles();
      } catch (error) {
        console.error(error);
      }
    },
    async saveVehicle() {
      if (!this.$refs.form.validate()) return;
      this.saving = true;
      try {
        if (this.editingVehicle) {
          this.form.id = this.editingVehicle.id;
          await VehicleService.updateVehicle(this.form);
        } else {
          await VehicleService.createVehicle(this.form);
        }
        this.dialogOpen = false;
        this.resetForm();
        this.loadVehicles();
      } catch (error) {
        console.error(error);
      } finally {
        this.saving = false;
      }
    },
    editVehicle(vehicle) {
      this.editingVehicle = vehicle;
      this.form = { ...vehicle };
      this.dialogOpen = true;
    },
    viewMaintenance(vehicleId) {
      this.$router.push(`/admin/vehicles/${vehicleId}/maintenance`);
    },
    async deleteVehicle(id) {
      if (!confirm('Êtes-vous sûr?')) return;
      try {
        await VehicleService.deleteVehicle(id);
        this.loadVehicles();
      } catch (error) {
        console.error(error);
      }
    },
    getStatusColor(status) {
      const colors = { 'Actif': 'green', 'En maintenance': 'orange', 'Hors service': 'red' };
      return colors[status] || 'gray';
    },
    resetForm() {
      this.form = {
        registration: '', brand: '', model: '', year: new Date().getFullYear(),
        type: 'Voiture', fuelType: '', status: 'Actif', mileage: 0
      };
      this.editingVehicle = null;
    }
  },
  mounted() {
    this.loadVehicles();
  }
};
</script>

<style scoped>
.vehicle-management {
  padding: 20px;
}
</style>
