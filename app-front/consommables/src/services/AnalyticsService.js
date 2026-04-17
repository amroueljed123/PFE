import api from './api';

const AnalyticsService = {
  // Consumption Forecasts
  createForecast: (consumableId, quantity, confidence, algorithm) => 
    api.post(`/analytics/forecasts/create/${consumableId}/${quantity}/${confidence}/${algorithm}`),
  getForecastsForConsumable: (consumableId) => 
    api.get(`/analytics/forecasts/consumable/${consumableId}`).then(res => res.data),

  // Anomaly Detection
  triggerAnomalyEngine: () => api.post('/analytics/anomalies/trigger-engine'),
  detectAnomaly: (params) => api.post('/analytics/anomalies/detect', params),
  getActiveAnomalies: () => api.get('/analytics/anomalies/active').then(res => res.data),
  getAnomaliesBySeverity: (severity) => api.get(`/analytics/anomalies/severity/${severity}`).then(res => res.data),
  investigateAnomaly: (anomalyId, investigator, notes) => 
    api.post(`/analytics/anomalies/${anomalyId}/investigate/${investigator}?notes=${notes}`),

  // Optimization Recommendations
  createRecommendation: (params) => api.post('/analytics/recommendations/create', params),
  getRecommendationsByStatus: (status) => api.get(`/analytics/recommendations/status/${status}`).then(res => res.data),
  getHighPriorityRecommendations: () => api.get('/analytics/recommendations/high-priority').then(res => res.data),
  approveRecommendation: (recId) => api.post(`/analytics/recommendations/${recId}/approve`),

  // Performance KPIs
  recordKPI: (entityType, entityId, period, kpiName, value, benchmark) => 
    api.post(`/analytics/kpis/record/${entityType}/${entityId}/${period}/${kpiName}/${value}?benchmark=${benchmark}`),
  getKPIsForEntity: (entityType, entityId) => 
    api.get(`/analytics/kpis/${entityType}/${entityId}`).then(res => res.data),
  getKPIsByPeriod: (period) => api.get(`/analytics/kpis/period/${period}`).then(res => res.data),

  // ══════════════════════════════════════════
  // CHRONOS Time-Series Forecasting
  // ══════════════════════════════════════════
  runChronosAnalysis: () => api.post('/analytics/chronos/run').then(res => res.data),
  getArticleForecast: (articleId) => api.get(`/analytics/chronos/article/${articleId}`).then(res => res.data),

  // ══════════════════════════════════════════
  // Data Seeding (Demo/Testing)
  // ══════════════════════════════════════════
  seedStockMovements: () => api.post('/analytics/seed/movements'),
  clearSeededData: () => api.delete('/analytics/seed/movements'),
  getRecentMovements: () => api.get('/analytics/movements/recent').then(res => res.data),
  getAllDetailedMovements: () => api.get('/analytics/movements/all-detailed').then(res => res.data),
  getMovementCount: () => api.get('/analytics/movements/count').then(res => res.data)
};

export default AnalyticsService;
