<template>
  <div class="ai-dashboard">
    <v-container fluid>
      <!-- Header -->
      <div class="dashboard-header mb-6">
        <div class="header-left">
          <div class="header-icon">
            <v-icon size="32" color="white">mdi-brain</v-icon>
          </div>
          <div>
            <h1 class="header-title">Intelligence Artificielle</h1>
            <p class="header-subtitle">Détection d'anomalies & Prédictions Chronos</p>
          </div>
        </div>
        <div class="header-actions">
          <v-btn
            color="indigo"
            variant="elevated"
            @click="seedData"
            :loading="seeding"
            prepend-icon="mdi-database-plus"
            rounded="lg"
            class="mr-2"
          >
            Générer Données (3 mois)
          </v-btn>
          <v-btn
            color="error"
            variant="elevated"
            @click="triggerAI"
            :loading="triggering"
            prepend-icon="mdi-alert-decagram"
            rounded="lg"
            class="mr-2"
          >
            Analyser Anomalies
          </v-btn>
          <v-btn
            variant="elevated"
            @click="runChronos"
            :loading="forecasting"
            prepend-icon="mdi-chart-timeline-variant-shimmer"
            rounded="lg"
            class="chronos-btn"
          >
            <v-icon class="mr-1">mdi-clock-fast</v-icon>
            Chronos Forecast
          </v-btn>
        </div>
      </div>

      <!-- Status Bar -->
      <div class="status-bar mb-6" v-if="movementCount > 0">
        <div class="status-item">
          <v-icon color="primary" size="20" class="mr-2">mdi-database</v-icon>
          <span class="status-value">{{ movementCount }}</span>
          <span class="status-label">mouvements</span>
        </div>
        <div class="status-divider"></div>
        <div class="status-item">
          <v-icon color="success" size="20" class="mr-2">mdi-trending-up</v-icon>
          <span class="status-value text-success">{{ chronosData.trendsUp || 0 }}</span>
          <span class="status-label">en hausse</span>
        </div>
        <div class="status-divider"></div>
        <div class="status-item">
          <v-icon color="error" size="20" class="mr-2">mdi-trending-down</v-icon>
          <span class="status-value text-error">{{ chronosData.trendsDown || 0 }}</span>
          <span class="status-label">en baisse</span>
        </div>
        <div class="status-divider"></div>
        <div class="status-item">
          <v-icon color="grey" size="20" class="mr-2">mdi-minus</v-icon>
          <span class="status-value">{{ chronosData.stable || 0 }}</span>
          <span class="status-label">stables</span>
        </div>
        <div class="status-divider"></div>
        <div class="status-item">
          <v-icon color="red" size="20" class="mr-2">mdi-alert</v-icon>
          <span class="status-value text-error">{{ anomalies.length }}</span>
          <span class="status-label">anomalies</span>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="movementCount === 0 && !seeding" class="empty-state mb-6">
        <div class="empty-icon">
          <v-icon size="64" color="grey-lighten-2">mdi-database-off</v-icon>
        </div>
        <h3 class="text-h6 text-grey-darken-1 mt-4">Aucune donnée de mouvement</h3>
        <p class="text-body-2 text-grey mt-1">Cliquez sur "Générer Données" pour créer 3 mois de données de test</p>
      </div>

      <!-- Tabs -->
      <v-tabs v-model="activeTab" bg-color="transparent" class="mb-4" color="primary">
        <v-tab value="chronos" prepend-icon="mdi-chart-timeline-variant-shimmer">Chronos Prédictions</v-tab>
        <v-tab value="anomalies" prepend-icon="mdi-alert-box">Anomalies ({{ anomalies.length }})</v-tab>
      </v-tabs>

      <!-- Tab: Chronos Forecasts -->
      <div v-if="activeTab === 'chronos'">
        <div v-if="chronosForecasts.length === 0 && !forecasting" class="empty-section">
          <v-icon size="48" color="grey-lighten-2">mdi-chart-line</v-icon>
          <p class="text-grey mt-2">Lancez l'analyse Chronos pour voir les prédictions</p>
        </div>

        <div v-if="forecasting" class="loading-section">
          <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
          <p class="text-grey mt-3">Chronos analyse les tendances...</p>
        </div>

        <!-- Forecast Cards Grid -->
        <div class="forecast-grid" v-if="chronosForecasts.length > 0">
          <div
            v-for="fc in chronosForecasts"
            :key="fc.articleId"
            class="forecast-card"
            :class="'trend-' + fc.trend.toLowerCase()"
            @click="selectedForecast = fc"
          >
            <!-- Trend Indicator -->
            <div class="fc-trend-badge" :class="'badge-' + fc.trend.toLowerCase()">
              <v-icon size="16" color="white">{{ getTrendIcon(fc.trend) }}</v-icon>
              <span>{{ getTrendLabel(fc.trend) }}</span>
            </div>

            <div class="fc-name">{{ fc.articleName }}</div>
            <div class="fc-metrics">
              <div class="fc-metric">
                <span class="fc-metric-value">{{ fc.currentStock }}</span>
                <span class="fc-metric-label">Stock actuel</span>
              </div>
              <div class="fc-metric">
                <span class="fc-metric-value">{{ fc.dailyAvgConsumption }}</span>
                <span class="fc-metric-label">Moy./jour</span>
              </div>
              <div class="fc-metric">
                <span class="fc-metric-value" :class="fc.daysUntilDepletion < 14 ? 'text-error' : ''">{{ fc.daysUntilDepletion > 365 ? '∞' : fc.daysUntilDepletion + 'j' }}</span>
                <span class="fc-metric-label">Épuisement</span>
              </div>
            </div>

            <!-- Mini trend spark -->
            <div class="fc-spark">
              <div class="fc-confidence">
                <v-icon size="12" class="mr-1">mdi-shield-check</v-icon>
                {{ fc.confidence }}% confiance
              </div>
              <div class="fc-slope" :class="fc.slope > 0 ? 'text-error' : fc.slope < 0 ? 'text-success' : ''">
                <v-icon size="14">{{ fc.slope > 0 ? 'mdi-arrow-up' : fc.slope < 0 ? 'mdi-arrow-down' : 'mdi-minus' }}</v-icon>
                {{ Math.abs(fc.slope) }}/jour
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab: Anomalies -->
      <div v-if="activeTab === 'anomalies'">
        <div v-if="anomalies.length === 0" class="empty-section">
          <v-icon size="48" color="grey-lighten-2">mdi-check-circle</v-icon>
          <p class="text-grey mt-2">Aucune anomalie détectée</p>
        </div>

        <div class="anomaly-grid" v-if="anomalies.length > 0">
          <div
            v-for="anomaly in anomalies"
            :key="anomaly.id"
            class="anomaly-card"
            :class="'severity-' + (anomaly.severity || '').toLowerCase()"
          >
            <div class="anomaly-header">
              <v-chip :color="getSeverityColor(anomaly.severity)" size="small" variant="elevated" class="mr-2">
                {{ anomaly.severity }}
              </v-chip>
              <v-chip :color="getStatusColor(anomaly.status)" size="small" variant="tonal">
                {{ anomaly.status }}
              </v-chip>
            </div>
            <div class="anomaly-type">
              <v-icon size="18" class="mr-2">{{ getAnomalyIcon(anomaly.anomalyType) }}</v-icon>
              {{ formatAnomalyType(anomaly.anomalyType) }}
            </div>
            <div class="anomaly-desc">{{ anomaly.description }}</div>
            <div class="anomaly-footer">
              <div class="anomaly-score">
                <v-progress-linear
                  :model-value="anomaly.anomalyScore"
                  :color="getScoreColor(anomaly.anomalyScore)"
                  height="6"
                  rounded
                ></v-progress-linear>
                <span class="score-label">Score: {{ anomaly.anomalyScore }}%</span>
              </div>
              <v-btn
                size="small"
                color="primary"
                variant="tonal"
                @click="investigateAnomaly(anomaly)"
                prepend-icon="mdi-magnify"
              >
                Enquêter
              </v-btn>
            </div>
          </div>
        </div>
      </div>

      <!-- Forecast Detail Dialog -->
      <v-dialog v-model="showForecastDetail" width="800" scrollable>
        <v-card v-if="selectedForecast" rounded="xl">
          <div class="detail-hero" :class="'trend-bg-' + selectedForecast.trend.toLowerCase()">
            <v-btn icon="mdi-close" variant="text" color="white" size="small" @click="showForecastDetail = false" class="detail-close"></v-btn>
            <div class="detail-hero-content">
              <v-icon size="40" color="white" class="mb-2">{{ getTrendIcon(selectedForecast.trend) }}</v-icon>
              <h2 class="detail-hero-title">{{ selectedForecast.articleName }}</h2>
              <v-chip color="white" variant="elevated" size="small" class="mt-2">
                {{ getTrendLabel(selectedForecast.trend) }} — Confiance {{ selectedForecast.confidence }}%
              </v-chip>
            </div>
          </div>

          <v-card-text class="pa-6">
            <v-row>
              <v-col cols="6" md="3">
                <div class="detail-stat">
                  <div class="detail-stat-value">{{ selectedForecast.currentStock }}</div>
                  <div class="detail-stat-label">Stock actuel</div>
                </div>
              </v-col>
              <v-col cols="6" md="3">
                <div class="detail-stat">
                  <div class="detail-stat-value">{{ selectedForecast.dailyAvgConsumption }}</div>
                  <div class="detail-stat-label">Moy. conso/jour</div>
                </div>
              </v-col>
              <v-col cols="6" md="3">
                <div class="detail-stat">
                  <div class="detail-stat-value" :class="selectedForecast.daysUntilDepletion < 14 ? 'text-error' : ''">
                    {{ selectedForecast.daysUntilDepletion > 365 ? '∞' : selectedForecast.daysUntilDepletion }}
                  </div>
                  <div class="detail-stat-label">Jours avant épuisement</div>
                </div>
              </v-col>
              <v-col cols="6" md="3">
                <div class="detail-stat">
                  <div class="detail-stat-value">{{ selectedForecast.projected30DayConsumption }}</div>
                  <div class="detail-stat-label">Conso prévisionnelle (30j)</div>
                </div>
              </v-col>
            </v-row>

            <v-divider class="my-4"></v-divider>

            <!-- Projections Table -->
            <!-- Projections Line Chart -->
            <h4 class="text-subtitle-1 font-weight-bold mb-3">
              <v-icon size="18" class="mr-2">mdi-chart-line</v-icon>
              Historique et Prévisions (Prochains 7 jours)
            </h4>
            <div class="line-chart-wrapper">
              <svg viewBox="0 0 700 200" class="line-chart-svg" preserveAspectRatio="none">
                <!-- Grid Lines -->
                <line x1="0" y1="40" x2="700" y2="40" stroke="#f0f0f0" stroke-width="1" />
                <line x1="0" y1="90" x2="700" y2="90" stroke="#f0f0f0" stroke-width="1" />
                <line x1="0" y1="140" x2="700" y2="140" stroke="#f0f0f0" stroke-width="1" />
                <line x1="0" y1="190" x2="700" y2="190" stroke="#e0e0e0" stroke-width="2" />
                
                <!-- History Line -->
                <polyline :points="chartData.historyLines" fill="none" stroke="#2196f3" stroke-width="3" stroke-linejoin="round" />
                
                <!-- Projection Line -->
                <polyline :points="chartData.projectionLines" fill="none" stroke="#e53935" stroke-width="3" stroke-dasharray="6,4" stroke-linejoin="round" />
                
                <!-- Connection Line (History to Projection) -->
                <line v-if="chartData.connectionLine" :x1="chartData.connectionLine.x1" :y1="chartData.connectionLine.y1" :x2="chartData.connectionLine.x2" :y2="chartData.connectionLine.y2" stroke="#e53935" stroke-width="3" stroke-dasharray="6,4" />

                <!-- Data Points (History) -->
                <circle v-for="pt in chartData.historyPoints" :key="'h'+pt.index" :cx="pt.x" :cy="pt.y" r="4" fill="#2196f3" />
                
                <!-- Data Points (Projection) -->
                <circle v-for="pt in chartData.projectionPoints" :key="'p'+pt.index" :cx="pt.x" :cy="pt.y" r="4" fill="#e53935" />
                
                <!-- X-Axis Labels -->
                <text v-for="label in chartData.labels" :key="'l'+label.index" :x="label.x" y="205" font-size="10" fill="#888" text-anchor="middle">
                  {{ label.text }}
                </text>
              </svg>
            </div>
            
            <div class="chart-legend mt-2 d-flex justify-center">
              <div class="legend-item mr-4"><span class="legend-color bg-blue"></span> Historique réel</div>
              <div class="legend-item"><span class="legend-color bg-red dashed"></span> Prévision (7 jours)</div>
            </div>

            <v-divider class="my-4"></v-divider>

            <!-- Weekly Pattern -->
            <h4 class="text-subtitle-1 font-weight-bold mb-3">
              <v-icon size="18" class="mr-2">mdi-calendar-week</v-icon>
              Saisonnalité hebdomadaire
            </h4>
            <div class="weekly-pattern" v-if="selectedForecast.weeklyPattern">
              <div
                v-for="(val, idx) in selectedForecast.weeklyPattern"
                :key="idx"
                class="weekly-bar-container"
              >
                <div class="weekly-bar" :style="{ height: (val * 50) + 'px', background: val > 1 ? '#e53935' : '#43a047' }"></div>
                <span class="weekly-label">{{ ['Lun','Mar','Mer','Jeu','Ven','Sam','Dim'][idx] }}</span>
                <span class="weekly-value">{{ val }}x</span>
              </div>
            </div>

            <!-- Technical Details -->
            <v-divider class="my-4"></v-divider>
            <h4 class="text-subtitle-1 font-weight-bold mb-3">
              <v-icon size="18" class="mr-2">mdi-cog</v-icon>
              Métriques techniques
            </h4>
            <v-row>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">Pente (slope)</span>
                  <span class="tech-value">{{ selectedForecast.slope }}</span>
                </div>
              </v-col>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">R² (fit)</span>
                  <span class="tech-value">{{ selectedForecast.rSquared }}</span>
                </div>
              </v-col>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">Écart-type</span>
                  <span class="tech-value">{{ selectedForecast.stdDev }}</span>
                </div>
              </v-col>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">MA(7)</span>
                  <span class="tech-value">{{ selectedForecast.movingAvg7 }}</span>
                </div>
              </v-col>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">MA(14)</span>
                  <span class="tech-value">{{ selectedForecast.movingAvg14 }}</span>
                </div>
              </v-col>
              <v-col cols="6" md="4">
                <div class="tech-metric">
                  <span class="tech-label">Total mouvements</span>
                  <span class="tech-value">{{ selectedForecast.totalOutMovements + selectedForecast.totalInMovements }}</span>
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-dialog>

      <!-- Investigation Dialog -->
      <v-dialog v-model="investigationDialog" max-width="500">
        <v-card v-if="selectedAnomaly" rounded="xl">
          <v-card-title class="bg-primary text-white pa-4">
            <v-icon class="mr-2">mdi-magnify</v-icon>
            Enquête sur Anomalie
          </v-card-title>
          <v-card-text class="pa-6">
            <p class="mb-2"><strong>Type:</strong> {{ formatAnomalyType(selectedAnomaly.anomalyType) }}</p>
            <p class="mb-4"><strong>Description:</strong> {{ selectedAnomaly.description }}</p>
            <v-textarea v-model="investigationNotes" label="Notes d'investigation" rows="4" variant="outlined"></v-textarea>
          </v-card-text>
          <v-card-actions class="pa-4">
            <v-spacer></v-spacer>
            <v-btn variant="text" @click="investigationDialog = false">Annuler</v-btn>
            <v-btn color="primary" variant="elevated" @click="submitInvestigation" :loading="submitting">Enregistrer</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

    </v-container>

    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="4000" location="bottom right">
      <v-icon class="mr-2">{{ snackbarColor === 'success' ? 'mdi-check-circle' : 'mdi-information' }}</v-icon>
      {{ snackbarMessage }}
    </v-snackbar>
  </div>
</template>

<script>
import AnalyticsService from '@/services/AnalyticsService';

export default {
  name: 'AnomalyDetectionDashboard',
  data() {
    return {
      activeTab: 'chronos',
      anomalies: [],
      chronosData: {},
      chronosForecasts: [],
      movementCount: 0,
      selectedForecast: null,
      showForecastDetail: false,
      selectedAnomaly: null,
      investigationDialog: false,
      investigationNotes: '',
      submitting: false,
      triggering: false,
      forecasting: false,
      seeding: false,
      snackbar: false,
      snackbarMessage: '',
      snackbarColor: 'success'
    };
  },
  watch: {
    selectedForecast(val) {
      if (val) this.showForecastDetail = true;
    }
  },
  computed: {
    chartData() {
      if (!this.selectedForecast) return {};
      
      const history = this.selectedForecast.history || [];
      const projections = this.selectedForecast.projections || [];
      
      // We'll plot last 14 days of history + 7 days forecast
      const plotHistory = history.slice(-14);
      const plotProj = projections.slice(0, 7);
      
      if (plotHistory.length === 0) return {};

      // Find min and max for Y scaling (add margin)
      const allVals = [...plotHistory.map(h => h.actual), ...plotProj.map(p => p.predicted)];
      const maxVal = Math.max(...allVals, 1) * 1.2;
      const minVal = 0; // Baseline 0
      
      const width = 700;
      const height = 150; // Use drawing area 150px (leaving bottom 50px for labels)
      const topOffset = 20;
      
      const totalPoints = plotHistory.length + plotProj.length;
      const xStep = width / Math.max(1, totalPoints - 1);
      
      const getY = (val) => topOffset + height - ((val / maxVal) * height);
      
      let currentX = 0;
      
      const historyPointsArr = [];
      plotHistory.forEach((h, i) => {
        historyPointsArr.push({ x: currentX, y: getY(h.actual), text: this.formatShortDate(h.date), index: i, value: h.actual });
        if (i < plotHistory.length - 1 || plotProj.length > 0) currentX += xStep;
      });
      
      const projPointsArr = [];
      plotProj.forEach((p, i) => {
        projPointsArr.push({ x: currentX, y: getY(p.predicted), text: this.formatShortDate(p.date), index: i, value: p.predicted });
        if (i < plotProj.length - 1) currentX += xStep;
      });

      const hLines = historyPointsArr.map(p => `${p.x},${p.y}`).join(' ');
      const pLines = projPointsArr.map(p => `${p.x},${p.y}`).join(' ');
      
      let connLine = null;
      if (historyPointsArr.length > 0 && projPointsArr.length > 0) {
        const lastH = historyPointsArr[historyPointsArr.length - 1];
        const firstP = projPointsArr[0];
        connLine = { x1: lastH.x, y1: lastH.y, x2: firstP.x, y2: firstP.y };
      }

      // Filter labels to not clutter (every 2nd or 3rd day)
      const allPoints = [...historyPointsArr, ...projPointsArr];
      const labels = allPoints.filter((_, i) => i % 2 === 0);

      return {
        historyLines: hLines,
        projectionLines: pLines,
        connectionLine: connLine,
        historyPoints: historyPointsArr,
        projectionPoints: projPointsArr,
        labels: labels
      };
    }
  },
  methods: {
    async loadData() {
      try {
        const count = await AnalyticsService.getMovementCount();
        this.movementCount = count.total || 0;
      } catch (e) { console.error(e); }

      try {
        this.anomalies = await AnalyticsService.getActiveAnomalies();
      } catch (e) { console.error(e); }
    },
    async seedData() {
      this.seeding = true;
      try {
        const res = await AnalyticsService.seedStockMovements();
        this.notify(res.data?.message || 'Données générées avec succès', 'success');
        this.loadData();
      } catch (e) {
        this.notify("Erreur lors de la génération", 'error');
      } finally {
        this.seeding = false;
      }
    },
    async triggerAI() {
      this.triggering = true;
      try {
        const res = await AnalyticsService.triggerAnomalyEngine();
        this.notify(res.data?.message || 'Analyse terminée', 'success');
        this.loadData();
      } catch (e) {
        this.notify("Erreur lors de l'analyse", 'error');
      } finally {
        this.triggering = false;
      }
    },
    async runChronos() {
      this.forecasting = true;
      try {
        const data = await AnalyticsService.runChronosAnalysis();
        this.chronosData = data || {};
        this.chronosForecasts = data.forecasts || [];
        this.notify(`Chronos: ${data.analyzedArticles} articles analysés`, 'success');
      } catch (e) {
        console.error(e);
        this.notify("Erreur Chronos", 'error');
      } finally {
        this.forecasting = false;
      }
    },
    investigateAnomaly(anomaly) {
      this.selectedAnomaly = anomaly;
      this.investigationNotes = '';
      this.investigationDialog = true;
    },
    async submitInvestigation() {
      this.submitting = true;
      try {
        const userId = JSON.parse(localStorage.getItem('user'))?.id;
        await AnalyticsService.investigateAnomaly(this.selectedAnomaly.id, userId, this.investigationNotes);
        this.investigationDialog = false;
        this.loadData();
        this.notify('Investigation enregistrée', 'success');
      } catch (e) { console.error(e); } finally { this.submitting = false; }
    },
    getTrendIcon(trend) {
      return { 'HAUSSE': 'mdi-trending-up', 'BAISSE': 'mdi-trending-down', 'STABLE': 'mdi-minus' }[trend] || 'mdi-help';
    },
    getTrendLabel(trend) {
      return { 'HAUSSE': 'Consommation en hausse', 'BAISSE': 'Consommation en baisse', 'STABLE': 'Stable', 'INSUFFICIENT_DATA': 'Données insuffisantes' }[trend] || trend;
    },
    getSeverityColor(s) {
      return { 'CRITICAL': 'red', 'HIGH': 'orange', 'MEDIUM': 'amber', 'LOW': 'blue' }[s] || 'grey';
    },
    getStatusColor(s) {
      return { 'ACTIVE': 'error', 'INVESTIGATED': 'warning', 'RESOLVED': 'success' }[s] || 'grey';
    },
    getScoreColor(score) {
      if (score >= 70) return 'red';
      if (score >= 40) return 'orange';
      return 'amber';
    },
    getAnomalyIcon(type) {
      return { 'HORS_HEURES': 'mdi-clock-alert', 'STAT_SUDDEN_SPIKE': 'mdi-chart-bell-curve-cumulative' }[type] || 'mdi-alert';
    },
    formatAnomalyType(type) {
      return { 'HORS_HEURES': 'Activité hors heures', 'STAT_SUDDEN_SPIKE': 'Pic statistique anormal' }[type] || type;
    },
    getBarHeight(val) {
      const max = Math.max(...(this.selectedForecast?.projections || []).map(p => p.predicted), 1);
      return Math.max(8, (val / max) * 120);
    },
    formatShortDate(dateStr) {
      if (!dateStr) return '';
      const d = new Date(dateStr);
      return d.getDate() + '/' + (d.getMonth() + 1);
    },
    notify(msg, color) {
      this.snackbarMessage = msg;
      this.snackbarColor = color;
      this.snackbar = true;
    }
  },
  mounted() {
    this.loadData();
  }
};
</script>

<style scoped>
.ai-dashboard { padding: 20px; }

/* ── Header ── */
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}
.header-left { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 56px; height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #5c6bc0, #7c4dff);
  display: flex; align-items: center; justify-content: center;
}
.header-title { font-size: 24px; font-weight: 800; color: #1a1a2e; margin: 0; }
.header-subtitle { font-size: 14px; color: #888; margin: 0; }
.header-actions { display: flex; gap: 8px; flex-wrap: wrap; }
.chronos-btn {
  background: linear-gradient(135deg, #7c4dff, #536dfe) !important;
  color: white !important;
}

/* ── Status Bar ── */
.status-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 16px 24px;
  border-radius: 16px;
  border: 1px solid rgba(0,0,0,0.06);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  flex-wrap: wrap;
}
.status-item { display: flex; align-items: center; gap: 4px; }
.status-value { font-size: 20px; font-weight: 800; margin-right: 4px; }
.status-label { font-size: 13px; color: #888; }
.status-divider { width: 1px; height: 24px; background: rgba(0,0,0,0.1); }

/* ── Empty States ── */
.empty-state, .empty-section {
  text-align: center;
  padding: 48px 20px;
  background: white;
  border-radius: 16px;
  border: 2px dashed rgba(0,0,0,0.08);
}
.empty-icon {
  width: 100px; height: 100px; margin: 0 auto;
  display: flex; align-items: center; justify-content: center;
  background: #f5f5f5; border-radius: 50%;
}
.loading-section { text-align: center; padding: 48px; }

/* ── Forecast Cards ── */
.forecast-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.forecast-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  border: 1px solid rgba(0,0,0,0.06);
  transition: all 0.2s;
  position: relative;
}
.forecast-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}
.fc-trend-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  color: white;
  margin-bottom: 10px;
}
.badge-hausse { background: linear-gradient(135deg, #e53935, #ff5252); }
.badge-baisse { background: linear-gradient(135deg, #43a047, #66bb6a); }
.badge-stable { background: linear-gradient(135deg, #757575, #9e9e9e); }
.badge-insufficient_data { background: #bdbdbd; }
.fc-name { font-size: 15px; font-weight: 700; color: #1a1a2e; margin-bottom: 12px; }
.fc-metrics { display: flex; gap: 16px; margin-bottom: 12px; }
.fc-metric { display: flex; flex-direction: column; }
.fc-metric-value { font-size: 18px; font-weight: 800; color: #333; }
.fc-metric-label { font-size: 11px; color: #999; }
.fc-spark { display: flex; justify-content: space-between; align-items: center; }
.fc-confidence { font-size: 11px; color: #888; display: flex; align-items: center; }
.fc-slope { font-size: 12px; font-weight: 700; display: flex; align-items: center; }

/* ── Anomaly Cards ── */
.anomaly-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}
.anomaly-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  border-left: 4px solid #ccc;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.severity-critical { border-left-color: #e53935; }
.severity-high { border-left-color: #ff9800; }
.severity-medium { border-left-color: #ffc107; }
.severity-low { border-left-color: #2196f3; }
.anomaly-header { margin-bottom: 10px; }
.anomaly-type { font-size: 14px; font-weight: 700; color: #333; margin-bottom: 6px; display: flex; align-items: center; }
.anomaly-desc { font-size: 13px; color: #666; line-height: 1.4; margin-bottom: 12px; }
.anomaly-footer { display: flex; justify-content: space-between; align-items: center; }
.anomaly-score { flex: 1; margin-right: 12px; }
.score-label { font-size: 11px; color: #888; }

/* ── Detail Dialog ── */
.detail-hero {
  position: relative;
  padding: 40px 24px 24px;
  color: white;
  text-align: center;
}
.trend-bg-hausse { background: linear-gradient(135deg, #c62828, #e53935); }
.trend-bg-baisse { background: linear-gradient(135deg, #1b5e20, #43a047); }
.trend-bg-stable { background: linear-gradient(135deg, #424242, #757575); }
.trend-bg-insufficient_data { background: #bdbdbd; }
.detail-close { position: absolute; top: 8px; right: 8px; }
.detail-hero-title { font-size: 22px; font-weight: 800; }
.detail-stat { text-align: center; padding: 12px; background: #f8f9fb; border-radius: 12px; }
.detail-stat-value { font-size: 24px; font-weight: 800; color: #333; }
.detail-stat-label { font-size: 12px; color: #888; }

/* ── Projections Line Chart ── */
.line-chart-wrapper {
  overflow-x: auto;
  overflow-y: hidden;
  padding: 10px 0;
  width: 100%;
}
.line-chart-svg {
  min-width: 600px;
  width: 100%;
  height: auto;
  max-height: 200px;
}
.chart-legend {
  font-size: 12px;
  color: #666;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}
.legend-color {
  width: 16px;
  height: 4px;
  display: inline-block;
  border-radius: 2px;
}
.legend-color.dashed {
  background: repeating-linear-gradient(90deg, #e53935, #e53935 4px, transparent 4px, transparent 8px);
}
.bg-blue { background-color: #2196f3; }
.bg-red { background-color: #e53935; }

/* ── Weekly Pattern ── */
.weekly-pattern {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 12px;
  height: 100px;
}
.weekly-bar-container { display: flex; flex-direction: column; align-items: center; }
.weekly-bar { width: 32px; border-radius: 6px 6px 0 0; min-height: 4px; transition: height 0.3s; }
.weekly-label { font-size: 11px; color: #888; margin-top: 4px; }
.weekly-value { font-size: 10px; color: #666; font-weight: 600; }

/* ── Tech Metrics ── */
.tech-metric {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f8f9fb;
  border-radius: 8px;
}
.tech-label { font-size: 13px; color: #888; }
.tech-value { font-size: 13px; font-weight: 700; color: #333; }

@media (max-width: 600px) {
  .dashboard-header { flex-direction: column; align-items: stretch; }
  .header-actions { justify-content: center; }
  .forecast-grid { grid-template-columns: 1fr; }
}
</style>
