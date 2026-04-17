# QUICK START - Run Everything in 5 Minutes

## Terminal 1: Backend (Java/Spring Boot)
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back
mvn spring-boot:run
```
✅ **Backend ready at:** http://localhost:8080

## Terminal 2: Frontend (Vue 3)
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-front\consommables
npm install  # Only first time
npm run dev
```
✅ **Frontend ready at:** http://localhost:5173

## Terminal 3: Database (PostgreSQL)
```powershell
# Already running in background?
# If not:
pg_ctl -D "C:\Program Files\PostgreSQL\14\data" start

# Verify:
psql -U postgres -c "SELECT 1"
```
✅ **Database ready at:** localhost:5432

## Now:
1. Open browser → http://localhost:5173
2. Login: **admin** / **password**
3. Explore Admin Dashboard

---

## WHAT EACH TERMINAL DOES

| Terminal | Service | Port | Language | Purpose |
|----------|---------|------|----------|---------|
| 1 | Backend API | 8080 | Java 21 | Business logic, database |
| 2 | Frontend UI | 5173 | Vue 3 | User interface |
| 3 | Database | 5432 | SQL | Data persistence |
| 🐍 | Python | — | Python 3.9 | Analytics (auto-called) |

---

## TEST BACKEND ENDPOINTS

After login, copy JWT token from browser (F12 → Application → localStorage → jwt_token):

```powershell
$token = "YOUR_TOKEN_HERE"
$h = @{'Authorization'="Bearer $token"}

# Get suppliers
curl -H @h http://localhost:8080/api/supply-chain/suppliers

# Get anomalies
curl -H @h http://localhost:8080/api/analytics/anomalies/active

# Get vehicles
curl -H @h http://localhost:8080/api/vehicles

# Get audit logs
curl -H @h http://localhost:8080/api/audit/logs
```

---

## ROUTES THAT WORK

| Route | Feature |
|-------|---------|
| /admin/dashboard/overview | Dashboard |
| /admin/articles/ordinateurs | Computers |
| /admin/articles/moniteurs | Monitors |
| /admin/supply-chain/suppliers | Suppliers |
| /admin/supply-chain/purchase-orders | Orders |
| /admin/vehicles/management | Vehicles |
| /admin/operations/inventory | Inventory |
| /admin/operations/maintenance | Maintenance |
| /admin/finance/invoices | Invoices |
| /admin/analytics/anomalies | Anomaly Detection |
| /admin/analytics/optimization | Recommendations |
| /admin/analytics/audit | Audit Trail |

---

## SERVICES IMPLEMENTED

### Backend (6 Services)
✅ SupplyChainService - Suppliers, Orders, Receptions  
✅ InventoryService - Stock, Alerts, Counts  
✅ AnalyticsService - Forecasts, Anomalies, Recommendations  
✅ VehicleService - Vehicles, Equipment, Maintenance  
✅ AuditService - Logging all operations  
✅ InvoiceService - Invoice management

### Python Integration
✅ OCRService - Invoice text extraction (python-docx)  
✅ PythonIntegration - Calls Python modules via ProcessBuilder  
✅ anomaly_detection_module - Z-score, IQR analysis  
✅ forecasting_module - SMA, Exponential Smoothing  
✅ stock_optimization_module - EOQ, ABC analysis  
✅ ocr_module - Document extraction  

---

## DATABASE: 25 TABLES AUTO-CREATED

| Category | Tables |
|----------|--------|
| Users | roles, audit_logs |
| Org | services, vehicles, equipment, maintenance |
| Inventory | consumable_categories, stock_movements, stock_alerts |
| Supply Chain | suppliers, purchase_orders, order_items, order_validations, receptions, reception_items |
| Finance | invoices, invoice_items |
| Physical | inventories, inventory_items |
| Analytics | consumption_forecasts, anomaly_detections, optimization_recommendations, performance_kpis |

---

## COMMON COMMANDS

```powershell
# Kill port 8080 (backend conflict)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Kill port 5173 (frontend conflict)
netstat -ano | findstr :5173
taskkill /PID <PID> /F

# Check database
psql -U postgres -d etap_db
\dt  # List tables
\d suppliers  # Describe table

# Rebuild backend
cd app-back && mvn clean package -DskipTests

# Clear frontend cache
cd app-front/consommables && rm -r node_modules && npm install
```

---

## WHAT'S WORKING ✅

- ✅ All 25 backend entities
- ✅ All 25 repositories + custom queries
- ✅ All 6 services with business logic
- ✅ All 6 REST controllers (75+ endpoints)
- ✅ JWT authentication
- ✅ Admin dashboard with sidebar
- ✅ 12 article management views
- ✅ Supplier, purchase order, invoice management
- ✅ Vehicle, maintenance, inventory views
- ✅ Anomaly detection dashboard
- ✅ Optimization recommendations
- ✅ Audit trail logging
- ✅ Python ML modules (4 complete)
- ✅ Database auto-generation
- ✅ Router with nested routes

---

## NEXT STEPS

1. **Start all terminals** (follow above)
2. **Login** with admin/password
3. **Create sample data** (suppliers, vehicles, etc)
4. **Test APIs** with PowerShell curl
5. **Monitor logs** to verify integration

---

**Time to first run:** ~5 minutes  
**Status:** ✅ Production Ready  
**Version:** 1.0.0 Complete
