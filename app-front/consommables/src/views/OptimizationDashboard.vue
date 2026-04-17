<template>
  <div class="optimization-dashboard">
    <v-container fluid>
      <v-row>
        <v-col cols="12">
          <v-card class="mb-4">
            <v-card-title>
              <v-icon left>mdi-lightbulb-on</v-icon>
              Recommandations d'Optimisation
            </v-card-title>
          </v-card>
        </v-col>

        <!-- Stats Cards -->
        <v-col cols="12" sm="6" md="3">
          <v-card class="text-center">
            <v-card-text>
              <div class="text-h4 font-weight-bold primary--text">{{ newRecommendations }}</div>
              <div class="text-subtitle2">Nouvelles</div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card class="text-center">
            <v-card-text>
              <div class="text-h4 font-weight-bold success--text">{{ approvedRecommendations }}</div>
              <div class="text-subtitle2">Approuvées</div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card class="text-center">
            <v-card-text>
              <div class="text-h4 font-weight-bold warning--text">{{ totalPotentialSavings }}</div>
              <div class="text-subtitle2">Économies DA</div>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" sm="6" md="3">
          <v-card class="text-center">
            <v-card-text>
              <div class="text-h4 font-weight-bold info--text">{{ criticalRecommendations }}</div>
              <div class="text-subtitle2">Critiques</div>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Recommendations Table -->
        <v-col cols="12">
          <v-card>
            <v-card-title>Recommandations</v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <v-tabs v-model="activeTab">
                <v-tab>Toutes</v-tab>
                <v-tab>Nouvelles</v-tab>
                <v-tab>Approuvées</v-tab>
              </v-tabs>

              <v-data-table
                :headers="headers"
                :items="filteredRecommendations"
                class="elevation-1 mt-4"
              >
                <template v-slot:item.priority="{ item }">
                  <v-chip :color="getPriorityColor(item.priority)" text-color="white" small>
                    {{ item.priority }}
                  </v-chip>
                </template>
                <template v-slot:item.status="{ item }">
                  <v-chip :color="getStatusColor(item.status)" text-color="white" small>
                    {{ item.status }}
                  </v-chip>
                </template>
                <template v-slot:item.expectedSavings="{ item }">
                  {{ item.expectedSavings?.toLocaleString() }} DA
                </template>
                <template v-slot:item.actions="{ item }">
                  <v-btn icon small color="primary" @click="viewDetails(item)">
                    <v-icon>mdi-eye</v-icon>
                  </v-btn>
                  <v-btn v-if="item.status === 'NEW'" icon small color="success" @click="approveRecommendation(item)">
                    <v-icon>mdi-check</v-icon>
                  </v-btn>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Details Dialog -->
        <v-dialog v-model="detailsDialog" max-width="700px">
          <v-card v-if="selectedRecommendation">
            <v-card-title>Détails de la Recommandation</v-card-title>
            <v-divider></v-divider>
            <v-card-text>
              <v-row>
                <v-col cols="12">
                  <strong>Type:</strong> {{ selectedRecommendation.type }}
                </v-col>
                <v-col cols="12">
                  <strong>Recommandation:</strong> {{ selectedRecommendation.recommendation }}
                </v-col>
                <v-col cols="12" md="6">
                  <strong>Économies Potentielles:</strong> {{ selectedRecommendation.expectedSavings }} DA
                </v-col>
                <v-col cols="12" md="6">
                  <strong>Priorité:</strong>
                  <v-chip :color="getPriorityColor(selectedRecommendation.priority)" text-color="white" small>
                    {{ selectedRecommendation.priority }}
                  </v-chip>
                </v-col>
              </v-row>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="secondary" @click="detailsDialog = false">Fermer</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import AnalyticsService from '@/services/AnalyticsService';

export default {
  name: 'OptimizationDashboard',
  data() {
    return {
      recommendations: [],
      selectedRecommendation: null,
      detailsDialog: false,
      activeTab: 0,
      headers: [
        { text: 'Type', value: 'type' },
        { text: 'Recommandation', value: 'recommendation' },
        { text: 'Économies', value: 'expectedSavings' },
        { text: 'Priorité', value: 'priority' },
        { text: 'Statut', value: 'status' },
        { text: 'Actions', value: 'actions', sortable: false }
      ]
    };
  },
  computed: {
    newRecommendations() {
      return this.recommendations.filter(r => r.status === 'NEW').length;
    },
    approvedRecommendations() {
      return this.recommendations.filter(r => r.status === 'APPROVED').length;
    },
    criticalRecommendations() {
      return this.recommendations.filter(r => r.priority === 'CRITICAL').length;
    },
    totalPotentialSavings() {
      return this.recommendations.reduce((sum, r) => sum + (r.expectedSavings || 0), 0).toLocaleString();
    },
    filteredRecommendations() {
      if (this.activeTab === 1) return this.recommendations.filter(r => r.status === 'NEW');
      if (this.activeTab === 2) return this.recommendations.filter(r => r.status === 'APPROVED');
      return this.recommendations;
    }
  },
  methods: {
    async loadRecommendations() {
      try {
        this.recommendations = await AnalyticsService.getHighPriorityRecommendations();
      } catch (error) {
        console.error(error);
      }
    },
    getPriorityColor(priority) {
      const colors = { 'CRITICAL': 'red', 'HIGH': 'orange', 'MEDIUM': 'blue', 'LOW': 'green' };
      return colors[priority] || 'gray';
    },
    getStatusColor(status) {
      const colors = { 'NEW': 'blue', 'REVIEWED': 'orange', 'APPROVED': 'green', 'IMPLEMENTED': 'purple', 'REJECTED': 'red' };
      return colors[status] || 'gray';
    },
    viewDetails(recommendation) {
      this.selectedRecommendation = recommendation;
      this.detailsDialog = true;
    },
    async approveRecommendation(recommendation) {
      try {
        await AnalyticsService.approveRecommendation(recommendation.id);
        this.loadRecommendations();
      } catch (error) {
        console.error(error);
      }
    }
  },
  mounted() {
    this.loadRecommendations();
  }
};
</script>

<style scoped>
.optimization-dashboard {
  padding: 20px;
}
</style>
