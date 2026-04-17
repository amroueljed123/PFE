# Complete Setup & Running Guide - ETAP System

## Quick Start (All at Once)

### Prerequisites Check
```powershell
# Check Java
java -version  # Should be 21

# Check Maven
mvn -version  # Should be 3.9+

# Check PostgreSQL
psql --version

# Check Node.js
node -v  # Should be 18+
npm -v

# Check Python
python --version  # Should be 3.9+
pip --version
```

---

## PART 1: DATABASE SETUP

### Step 1.1: Create Database
```powershell
# Start PostgreSQL service (if not running)
pg_ctl -D "C:\Program Files\PostgreSQL\14\data" start

# Create database
createdb -U postgres etap_db

# Verify
psql -U postgres -l  # Should see etap_db in list
```

### Step 1.2: Update Backend Configuration
File: `app-back/src/main/resources/application.properties`

```properties
# Server
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/etap_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate DDL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=86400000

# Logging
logging.level.root=INFO
logging.level.com.esb.appback=DEBUG
```

---

## PART 2: PYTHON MODULES SETUP

### Step 2.1: Install Python Dependencies
```powershell
# Navigate to project root
cd c:\Users\amrou\OneDrive\Desktop\PFE\app

# Install required packages
pip install python-docx

# Verify modules exist
dir *.py
```

Expected files:
- `ocr_module.py` - Invoice OCR extraction
- `anomaly_detection_module.py` - Anomaly detection
- `forecasting_module.py` - Time series forecasting
- `stock_optimization_module.py` - Stock optimization

### Step 2.2: Test Python Modules (Optional)
```powershell
# Test OCR Module
python -c "from ocr_module import process_invoice_text; print('OCR OK')"

# Test Anomaly Detection
python -c "from anomaly_detection_module import detect_consumption_anomaly; print('Anomaly OK')"

# Test Forecasting
python -c "from forecasting_module import forecast_consumption; print('Forecasting OK')"

# Test Optimization
python -c "from stock_optimization_module import optimize_stock_levels; print('Optimization OK')"
```

---

## PART 3: BACKEND SETUP (SPRING BOOT)

### Step 3.1: Build Backend
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back

# Clean and build
mvn clean compile
mvn package -DskipTests   # ~30-40 seconds

# Or just run directly (compiles on startup)
mvn spring-boot:run
```

### Step 3.2: Start Backend Server

**Option A: Using Maven**
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back
mvn spring-boot:run
```

**Option B: Using JAR (after build)**
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back
java -jar target/app-back-0.0.1-SNAPSHOT.jar
```

### Step 3.3: Verify Backend is Running
Look for these messages in console:
```
[main] com.esb.appback.AppBackApplication : Started AppBackApplication in X.XXX seconds
[main] org.hibernate.engine.transaction.internal.TransactionImpl : HHH000515: begin
[main] org.hibernate.engine.transaction.internal.TransactionImpl : Hibernate: create table
```

Backend URL: **http://localhost:8080**

### Backend API Endpoints
- Suppliers: `http://localhost:8080/api/supply-chain/suppliers`
- Purchase Orders: `http://localhost:8080/api/supply-chain/orders`
- Anomalies: `http://localhost:8080/api/analytics/anomalies/active`
- Forecasts: `http://localhost:8080/api/analytics/forecasts`
- Vehicles: `http://localhost:8080/api/vehicles`
- Audit: `http://localhost:8080/api/audit/logs`

---

## PART 4: FRONTEND SETUP (VUE 3 + VITE)

### Step 4.1: Install Frontend Dependencies
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-front\consommables

# Install all dependencies
npm install

# This may take 2-3 minutes
```

### Step 4.2: Start Frontend Dev Server
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-front\consommables

# Start development server
npm run dev

# You should see:
# VITE v... dev server running at:
# > Local: http://localhost:5173/
```

Frontend URL: **http://localhost:5173**

### Step 4.3: Verify Frontend is Running
Open browser and go to `http://localhost:5173`

You should see:
- Home page with login option
- Navigation to "Admin Dashboard"

---

## PART 5: COMPLETE TESTING

### Test 5.1: User Login (Frontend)
1. Go to `http://localhost:5173`
2. Click "Login" (or go directly to `/login`)
3. Credentials:
   - **Username:** admin
   - **Password:** password
4. You should be redirected to `/admin/dashboard/overview`

### Test 5.2: Dashboard Access
Navigate via sidebar to:
- **ADMINISTRATION** → Tableau de bord
- **INFORMATIQUE** → Ordinateurs, Moniteurs, etc.
- **APPROVISIONNEMENT** → Fournisseurs, Bons de commande
- **OPÉRATIONS** → Inventaire Physique, Maintenance
- **COMPTABILITÉ** → Factures
- **ANALYTIQUE** → Anomalies, Optimisation, Audit

### Test 5.3: API Calls (Backend)
Get JWT token from browser localStorage (F12 → Application → localStorage → jwt_token)

**Test Supplier API:**
```powershell
# In PowerShell, set your token
$token = "YOUR_JWT_TOKEN_HERE"
$headers = @{ 'Authorization' = "Bearer $token" }

# GET all suppliers
Invoke-RestMethod -Uri http://localhost:8080/api/supply-chain/suppliers `
  -Headers $headers `
  -Method GET

# Output: Empty array [] (no suppliers yet)
```

**Create a Supplier:**
```powershell
$body = @{
  name = "Test Supplier Inc"
  contact = "Contact Person"
  email = "supplier@example.com"
  phone = "+213 555 123456"
  address = "Algiers"
  rating = 4.5
  paymentTerms = 30
  active = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8080/api/supply-chain/suppliers `
  -Headers $headers `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

**Test Anomaly Detection:**
```powershell
$body = @{
  consumableId = 1
  values = @(100, 102, 98, 105, 103)
  currentValue = 500
} | ConvertTo-Json

Invoke-RestMethod -Uri http://localhost:8080/api/analytics/anomalies/detect `
  -Headers $headers `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

# Should return anomaly score and severity
```

### Test 5.4: Verify Database Tables
```powershell
# Connect to database
psql -U postgres -d etap_db

# List all tables
\dt

# Should show 25+ tables:
# - audit_logs
# - suppliers
# - purchase_orders
# - order_validations
# - invoices
# - vehicles
# - maintenance
# - inventory
# - etc.

# Check table structure
\d suppliers
```

---

## BUILDING FOR PRODUCTION

### Frontend Build
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-front\consommables

# Build production bundle
npm run build

# Output: dist/ folder created
# Then deploy dist/ folder to web server
```

### Backend Build
```powershell
cd c:\Users\amrou\OneDrive\Desktop\PFE\app\app-back

# Build production JAR
mvn clean package -DskipTests

# JAR file: target/app-back-0.0.1-SNAPSHOT.jar
# Deploy to server or Docker container
```

---

## COMMON ISSUES & FIXES

### Issue 1: Port Already in Use
**Error:** `Address already in use`

**Solution:**
```powershell
# Kill process on port 8080 (backend)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Kill process on port 5173 (frontend)
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

### Issue 2: Database Connection Failed
**Error:** `Connection refused` or `No suitable driver`

**Solution:**
- Make sure PostgreSQL is running: `pg_ctl status`
- Verify database exists: `psql -U postgres -l`
- Check application.properties credentials
- Check firewall allows localhost:5432

### Issue 3: Python Module Not Found
**Error:** `ModuleNotFoundError: No module named 'xxx'`

**Solution:**
```powershell
# Reinstall python-docx
pip uninstall python-docx
pip install python-docx

# Verify Python in PATH
where python

# Make sure you're in the right directory
cd c:\Users\amrou\OneDrive\Desktop\PFE\app
ls *.py  # Should list all Python modules
```

### Issue 4: Frontend Shows Blank Page
**Error:** White/blank page, no errors

**Solution:**
- Check browser console (F12) for errors
- Verify backend is running: `http://localhost:8080`
- Clear cache: Ctrl+Shift+Delete
- Check network tab to see API calls
- Try incognito window

### Issue 5: Routes Not Working
**Error:** `404 Not Found` when accessing routes

**Solution:**
- Verify route exists in router/index.js
- Check component path is correct
- Verify you're logged in (requiresAdmin meta)
- Frontend must be running on http://localhost:5173
- Backend should be on http://localhost:8080

### Issue 6: API Endpoints Returning 401
**Error:** `Unauthorized` response

**Solution:**
- Check JWT token is valid and not expired
- Verify token is being sent in Authorization header
- Check token in localStorage: F12 → Application → localStorage
- Re-login to get new token: Go to /login

---

## TROUBLESHOOTING COMMANDS

### Check All Services Running
```powershell
# Check if backend is responding
curl http://localhost:8080/api/supply-chain/suppliers
# This will likely give 401 (Unauthorized) which means backend is up

# Check if frontend is responding
curl http://localhost:5173
# Should return HTML

# Check if PostgreSQL is running
psql -U postgres -c "SELECT 1"
# Should return: 1

# Check if Python modules are accessible
python -c "import sys; print(sys.path)"
```

### Enable Debug Logging
In `application.properties`:
```properties
logging.level.com.esb.appback=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Monitor Database Queries
```powershell
# In PostgreSQL
psql -U postgres -d etap_db

# Enable query logging
ALTER DATABASE etap_db SET log_statement = 'all';

# View logs
SELECT query FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;
```

---

## SYSTEM ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Vue 3 + Vite)                  │
│              http://localhost:5173                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ AdminDashboard.vue (Layout with sidebar navigation)    │ │
│  ├────────────────────────────────────────────────────────┤ │
│  │ Management Views:                                      │ │
│  │ - Suppliers, Purchase Orders, Invoices                │ │
│  │ - Vehicles, Maintenance, Inventory                    │ │
│  │ - Articles (IT, Furniture, Supplies)                  │ │
│  │ - Dashboards (Anomalies, Optimization, Audit)         │ │
│  └────────────────────────────────────────────────────────┘ │
│                           ↓                                   │
│                     API Clients (JS)                          │
│   ┌─────────────────────────────────────────────────────┐   │
└─→│ http://localhost:8080/api/{resource}                │───┘
    │                                                       │
    └─────────────────────────────────────────────────────┘
                           ↓
    ┌─────────────────────────────────────────────────────┐
    │            BACKEND (Spring Boot + Java 21)          │
    │              http://localhost:8080                  │
    ├─────────────────────────────────────────────────────┤
    │ Controllers (REST API)                              │
    │ ├─ SupplyChainController                            │
    │ ├─ InventoryController                              │
    │ ├─ AnalyticsController (+ Python integration)       │
    │ ├─ VehicleController                                │
    │ ├─ AuditController                                  │
    │ └─ InvoiceController (+ OCR)                        │
    │                                                      │
    │ Services (Business Logic)                           │
    │ ├─ SupplyChainService                               │
    │ ├─ InventoryService                                 │
    │ ├─ AnalyticsService (calls Python)                  │
    │ ├─ VehicleService                                   │
    │ ├─ AuditService                                     │
    │ └─ InvoiceService                                   │
    │                                                      │
    │ Repositories (JPA)                                  │
    │ └─ 25+ Spring Data repositories                     │
    │                                                      │
    │ Entities (Models)                                   │
    │ └─ 25+ JPA entities                                 │
    └─────────────────────────────────────────────────────┘
                           ↓
    ┌─────────────────────────────────────────────────────┐
    │        DATABASE (PostgreSQL)                        │
    │            :5432                                    │
    ├─────────────────────────────────────────────────────┤
    │ Tables:                                             │
    │ - Users, Roles (access control)                    │
    │ - Suppliers, PurchaseOrders, Invoices              │
    │ - Vehicles, Equipment, Maintenance                 │
    │ - Stock, Inventory, Anomalies                      │
    │ - Forecasts, Recommendations, KPIs                │
    │ - AuditLogs (complete history)                     │
    └─────────────────────────────────────────────────────┘
                           ↓
    ┌─────────────────────────────────────────────────────┐
    │         PYTHON ML MODULES (Analytics)              │
    │      (Called via ProcessBuilder from Java)         │
    ├─────────────────────────────────────────────────────┤
    │ ocr_module.py                                       │
    │ ├─ Invoice text extraction                          │
    │ ├─ Supplier detection                              │
    │ └─ Amount parsing (HT, TVA, TTC)                   │
    │                                                      │
    │ anomaly_detection_module.py                        │
    │ ├─ Z-score analysis                                │
    │ ├─ IQR outlier detection                           │
    │ └─ Trend deviation (score 0-100)                   │
    │                                                      │
    │ forecasting_module.py                              │
    │ ├─ Simple Moving Average                           │
    │ ├─ Exponential Smoothing                           │
    │ └─ Holt's method with trend                        │
    │                                                      │
    │ stock_optimization_module.py                       │
    │ ├─ EOQ calculation                                 │
    │ ├─ ABC Pareto analysis                             │
    │ └─ Safety stock & reorder points                   │
    └─────────────────────────────────────────────────────┘
```

---

## NEXT STEPS

1. ✅ **Complete Setup:** Follow all steps above
2. ✅ **Run All Services:** Backend, Frontend, Database running
3. ✅ **Test APIs:** Via PowerShell or Postman
4. ⏳ **Deploy:** Docker or Cloud (future)
5. ⏳ **Optimize:** Caching, performance tuning
6. ⏳ **Extend:** Email notifications, advanced reporting

---

## SUPPORT

**Documentation:**
- See `PROJECT_COMPLETE.md` for detailed API docs
- Check `README.md` for project overview
- Review code comments in source files

**Tools:**
- API Testing: Use Postman or `Invoke-RestMethod` in PowerShell
- Database: Use `pgAdmin` or `psql` CLI
- Frontend Debugging: Browser DevTools (F12)
- Backend Debugging: IDE breakpoints or logs

---

**Status:** ✅ Ready to Run  
**Last Updated:** Feb 26, 2026
