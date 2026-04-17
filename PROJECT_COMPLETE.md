# ETAP Asset Management System - Complete Implementation

## Project Overview

This is a comprehensive full-stack inventory and asset management system for the Algerian Petroleum Company (ETAP). The system handles consumable tracking, supply chain management, vehicle maintenance, anomaly detection, and advanced analytics.

**Technology Stack:**
- **Backend:** Spring Boot 4.0.2, Java 21, PostgreSQL, Spring Data JPA
- **Frontend:** Vue 3.5.27, Vuetify 4.0.0-beta.1, Vite
- **Analytics:** Python (OCR, Anomaly Detection, Forecasting, Stock Optimization)

## Project Structure

```
app/
├── app-back/                    # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/esb/appback/
│   │       ├── AppBackApplication.java
│   │       ├── config/          # Configuration classes (CORS, Security)
│   │       ├── controller/      # REST API endpoints (6 controllers)
│   │       ├── service/         # Business logic (6+ services)
│   │       ├── repository/      # Spring Data JPA repositories (25+)
│   │       ├── model/           # JPA entities (25+)
│   │       ├── dto/             # Data transfer objects
│   │       ├── exception/       # Custom exceptions
│   │       ├── filter/          # JWT authentication filters
│   │       └── util/            # Utility classes (OCR, Python integration)
│   ├── src/main/resources/
│   │   └── application.properties    # Database & configuration
│   └── pom.xml                  # Maven dependencies
│
├── app-front/consommables/      # Vue.js Frontend
│   ├── src/
│   │   ├── views/               # Page components (15+ views)
│   │   ├── components/          # Reusable components
│   │   ├── services/            # API clients (5 services)
│   │   ├── router/index.js      # Route configuration
│   │   └── assets/              # CSS, images
│   ├── package.json
│   └── vite.config.js
│
├── ocr_module.py                # OCR text extraction
├── anomaly_detection_module.py  # Anomaly detection algorithms
├── forecasting_module.py        # Time series forecasting
└── stock_optimization_module.py # Stock optimization algorithms
```

## Backend Implementation

### Entities (25 JPA Models)

**User & Audit:**
- `Role` - User roles and permissions
- `AuditLog` - Complete operation audit trail

**Organization:**
- `Service` - Department/service management
- `Vehicle` - Asset vehicles with tracking
- `Equipment` - Facility equipment
- `Maintenance` - Maintenance records

**Inventory:**
- `ConsumableCategory` - Hierarchical categories
- `StockMovement` - IN/OUT stock transactions
- `StockAlert` - Stock level alerts (LOW_STOCK, REORDER, EXCESS)

**Supply Chain:**
- `Supplier` - Vendor management
- `PurchaseOrder` - Purchase workflow (DRAFT → SUBMITTED → APPROVED → COMPLETED)
- `OrderItem` - Line items
- `OrderValidation` - Multi-level approval (4 levels based on amount)
- `Reception` - Goods reception
- `ReceptionItem` - Reception lines with quality checks

**Finance:**
- `Invoice` - Supplier invoices with OCR confidence score
- `InvoiceItem` - Invoice line items

**Physical Inventory:**
- `Inventory` - Physical counts
- `InventoryItem` - Individual variance tracking

**Analytics:**
- `ConsumptionForecast` - ML predictions (ARIMA/PROPHET/EMA)
- `AnomalyDetection` - Detected anomalies (score 0-100)
- `OptimizationRecommendation` - AI recommendations
- `PerformanceKPI` - KPI tracking

### REST API Endpoints

**SupplyChainController** (`/api/supply-chain/`)
```
POST   /suppliers                    # Create supplier
GET    /suppliers                    # Get all suppliers
GET    /suppliers/{id}               # Get supplier
PUT    /suppliers/{id}               # Update supplier
DELETE /suppliers/{id}               # Delete supplier

POST   /orders                       # Create purchase order
GET    /orders                       # Get all orders
GET    /orders/{id}                  # Get order
GET    /orders/status/{status}       # Filter by status
PUT    /orders/{id}                  # Update order

POST   /validations/submit/{orderId}/...  # Submit for validation
POST   /validations/{id}/approve         # Approve order
GET    /validations/pending              # Get pending validations

POST   /receptions/record/{orderId}/...  # Record reception
POST   /receptions/{receptionId}/items   # Add reception items
```

**InventoryController** (`/api/inventory/`)
```
POST   /movements/record/...        # Record stock movement
GET    /movements/consumable/{id}   # Get movements
POST   /alerts/create               # Create stock alert
GET    /alerts/active               # Get active alerts
POST   /alerts/{id}/resolve         # Resolve alert

POST   /inventories                 # Create inventory count
GET    /inventories                 # Get inventories
POST   /inventories/{id}/start      # Start inventory
POST   /inventories/{id}/complete   # Complete inventory
```

**AnalyticsController** (`/api/analytics/`)
```
POST   /forecasts/create            # Create forecast
GET    /forecasts/consumable/{id}   # Get forecasts

POST   /anomalies/detect            # Detect anomaly
GET    /anomalies/active            # Get active anomalies
GET    /anomalies/severity/{sev}    # Filter by severity
POST   /anomalies/{id}/investigate  # Investigate anomaly

POST   /recommendations/create      # Create recommendation
GET    /recommendations/status/{st} # Get by status
POST   /recommendations/{id}/approve# Approve recommendation

POST   /kpis/record                 # Record KPI
GET    /kpis/entity/{type}/{id}     # Get entity KPIs
```

**VehicleController** (`/api/vehicles/`)
```
POST   /vehicles                    # Create vehicle
GET    /vehicles                    # Get all vehicles
GET    /vehicles/{id}               # Get vehicle
GET    /vehicles/status/{status}    # Filter by status
PUT    /vehicles/{id}               # Update vehicle

POST   /maintenance/schedule        # Schedule maintenance
POST   /maintenance/record          # Record maintenance
GET    /maintenance/history/{id}    # Get maintenance history

POST   /services                    # Create service
GET    /services                    # Get all services
```

**AuditController** (`/api/audit/`)
```
POST   /log/{userId}/{action}/{entity}  # Log action
GET    /logs/user/{userId}              # Get user logs
GET    /logs/entity/{entity}            # Get entity logs
GET    /logs/between?start=...&end=...  # Date range query
GET    /logs                            # Get all logs
```

**InvoiceController** (`/api/invoices/`)
```
POST   /invoices                    # Create invoice
GET    /invoices                    # Get all invoices
GET    /invoices/{id}               # Get invoice
GET    /invoices/status/{status}    # Filter by status
PUT    /invoices/{id}               # Update invoice
DELETE /invoices/{id}               # Delete invoice

POST   /{invoiceId}/items           # Add line items
GET    /{invoiceId}/items           # Get line items
```

### Business Logic Services

1. **SupplyChainService** - Supplier, PurchaseOrder, OrderValidation, Reception management
2. **InventoryService** - StockMovement, StockAlert, Inventory management
3. **AnalyticsService** - Forecasts, Anomaly detection, Recommendations, KPIs
4. **VehicleService** - Vehicle, Equipment, Maintenance, Service management
5. **AuditService** - Comprehensive audit logging
6. **InvoiceService** - Invoice management with OCR support
7. **OCRService** - Invoice text extraction and validation
8. **PythonIntegration** - Python ML module integration

## Frontend Implementation

### Management Views (15+ Components)

**Article Management:**
- ArticlesDashboard.vue
- ConsommablesManagement.vue
- OrdinateursManagement.vue
- MontiteursManagement.vue
- PeripheriquesManagement.vue
- TelephonesManagement.vue
- CartouchesManagement.vue
- ImprimantesManagement.vue
- BureauxManagement.vue
- ChaisesManagement.vue
- MeublesManagement.vue
- EtageresManagement.vue

**Supply Chain:**
- SupplierManagement.vue
- PurchaseOrderManagement.vue

**Operations:**
- VehicleManagement.vue
- MaintenanceManagement.vue
- InventoryManagement.vue
- InvoiceManagement.vue

**Analytics:**
- AnomalyDetectionDashboard.vue
- OptimizationDashboard.vue
- AuditDashboard.vue

### API Service Clients (5 Services)

1. **ArticleService** - Article CRUD operations
2. **SupplyChainService** - Supplier, Purchase Order, Reception operations
3. **InventoryService** - Stock management
4. **VehicleService** - Vehicle and maintenance operations
5. **AnalyticsService** - Forecasts, anomalies, recommendations, KPIs

### UI Components

- **Vuetify Data Tables** - Sortable, searchable data grids
- **Form Validation** - Required field checking, email validation
- **Modal Dialogs** - Add/Edit operations
- **Statistics Cards** - KPI displays
- **Color-coded Status Chips** - Visual status indicators
- **Currency Formatting** - Algerian Dinar (DZD) display
- **Date Formatting** - Localized date/time display

## Python Analytics Modules

### OCR Module (`ocr_module.py`)

Handles invoice document text extraction:
- **Extraction Methods:**
  - Invoice number pattern matching
  - Date parsing (multiple formats)
  - Supplier name detection
  - Line item extraction
  - Financial amount parsing (HT, TVA, TTC)

- **Validation:**
  - Confidence scoring (0-1.0)
  - Amount consistency checking
  - Required field validation

- **Usage:**
  ```python
  from ocr_module import process_invoice_text
  result = process_invoice_text(invoice_text)
  # Returns: {data, validation, status}
  ```

### Anomaly Detection Module (`anomaly_detection_module.py`)

Detects unusual patterns in consumable data:

- **Methods:**
  - **Z-Score Analysis** - Statistical outlier detection
  - **IQR (Interquartile Range)** - Robust outlier detection
  - **Trend Anomaly** - Abrupt change detection

- **Scoring (0-100):**
  - Z-Score deviation: up to 40 points
  - IQR outlier flag: 30 points
  - Trend component: up to 30 points

- **Classification:**
  - CRITICAL: score >= 70
  - HIGH: score >= 50
  - MEDIUM: score >= 30
  - LOW: score < 30

- **Usage:**
  ```python
  from anomaly_detection_module import detect_consumption_anomaly
  result = detect_consumption_anomaly(history, current_value)
  # Returns: {is_anomaly, score, severity, expected_range}
  ```

### Forecasting Module (`forecasting_module.py`)

Time series prediction for consumable consumption:

- **Algorithms:**
  - **Simple Moving Average (SMA)** - Basic averaging with trend
  - **Exponential Smoothing (ES)** - Weighted recent values
  - **Holt's Method** - ES with trend component

- **Reorder Point Calculation:**
  - Based on consumption rate and lead time
  - Includes safety stock (default 95% service level)
  - EOQ-based approach

- **Usage:**
  ```python
  from forecasting_module import forecast_consumption
  result = forecast_consumption(history, periods=30, method='sma')
  # Returns: {forecasts, method, confidence, next_30_days}
  ```

### Stock Optimization Module (`stock_optimization_module.py`)

Inventory optimization algorithms:

- **EOQ (Economic Order Quantity):**
  - Formula: sqrt(2*D*S / H)
  - Minimizes total cost (ordering + holding)
  - Calculates optimal order frequency

- **ABC Analysis (Pareto):**
  - A items: Top 20% by value (high priority)
  - B items: 30% by value (medium priority)
  - C items: 50% by value (low priority)

- **Safety Stock:**
  - Based on demand variability and service level
  - Z-score approach (default 95% service level = 1.65)

- **Usage:**
  ```python
  from stock_optimization_module import optimize_stock_levels
  result = optimize_stock_levels(
      annual_demand=50000,
      ordering_cost=100,
      holding_cost=5,
      lead_time=14,
      std_dev_demand=500,
      service_level='95%'
  )
  # Returns: {eoq, safety_stock, reorder_point, max_stock, min_stock}
  ```

## Database Schema

### DDL Auto Configuration

Set in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

This automatically generates 25 tables on Spring Boot startup:
- 3 user/audit tables
- 4 organization/asset tables
- 3 inventory tables
- 6 supply chain tables
- 2 finance tables
- 2 physical inventory tables
- 4 analytics tables

### Column Notes

- **Timestamps:** `LocalDateTime` (auto-set on insert)
- **Status Fields:** Enums mapped as VARCHAR(20)
- **Amounts:** DECIMAL(19,2) for financial values
- **Confidence Scores:** DOUBLE (0.0-1.0 or 0-100)
- **Audit Fields:** userId, action, oldValue, newValue stored as TEXT

## Running the Application

### Prerequisites

- Java Development Kit (JDK) 21
- Maven 3.9+
- Python 3.9+ (with pip)
- Node.js 18+ (for npm)
- PostgreSQL 14+ (database)

### Database Setup

```bash
# Create database
createdb etap_db

# Update application.properties with connection string:
spring.datasource.url=jdbc:postgresql://localhost:5432/etap_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Backend Setup & Run

```bash
cd app-back

# Install Python ML dependencies
pip install python-docx

# Build project
mvn clean package

# Run Spring Boot
mvn spring-boot:run

# Application starts at http://localhost:8080
# Default port can be configured in application.properties:
# server.port=8080
```

### Frontend Setup & Run

```bash
cd app-front/consommables

# Install Node dependencies
npm install

# Run development server
npm run dev

# Application starts at http://localhost:5173
# Access dashboard at http://localhost:5173/admin after login
```

### Python ML Modules

Modules are called from Java backend via `PythonIntegration` class:
- Ensure Python executable is accessible in system PATH
- Modules are relative to backend directory
- Results returned as JSON strings

## Authentication & Authorization

### JWT Authentication

1. **Login Flow:**
   - User submits credentials to `/login` endpoint
   - Backend validates and returns JWT token
   - Token stored in `localStorage` as `jwt_token`

2. **Token Usage:**
   - All API requests include `Authorization: Bearer {token}` header
   - JWT filter (`JwtAuthenticationFilter.java`) validates on each request

3. **User Roles:**
   - `admin` - Full system access
   - `user` - Limited read access
   - `manager` - Department access

4. **Route Protection:**
   - Frontend router guards check `requiresAdmin` meta flag
   - Backend controller methods can add `@PreAuthorize` annotations

## Configuration Files

### Backend Configuration (`application.properties`)

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/etap_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Logging
logging.level.com.esb.appback=DEBUG
```

### Frontend Configuration (`vite.config.js`)

```javascript
export default {
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

## API Integration Examples

### From Frontend (JavaScript)

```javascript
// Create Supplier
import SupplyChainService from '@/services/SupplyChainService'

const newSupplier = {
  name: 'Supplier Name',
  email: 'supplier@example.com',
  phone: '+213 555 123456',
  address: 'Algiers',
  rating: 4.5,
  paymentTerms: 30
}

try {
  const supplier = await SupplyChainService.createSupplier(newSupplier)
  console.log('Supplier created:', supplier)
} catch (error) {
  console.error('Error:', error)
}
```

### From Backend (Java)

```java
@Autowired
private SupplyChainService supplyChainService;

// Create purchase order
PurchaseOrder order = new PurchaseOrder();
order.setOrderNumber("ORD-2024-001");
order.setSupplierId(1L);
order.setStatus("DRAFT");
order.setDate(LocalDateTime.now());

PurchaseOrder saved = supplyChainService.createPurchaseOrder(order);
```

### From Python (ML Modules)

```python
# Detect anomalies
from anomaly_detection_module import detect_consumption_anomaly

history = [100, 102, 98, 105, 103, 500]  # Last value is anomaly
result = detect_consumption_anomaly(history[:-1], 500)

print(f"Is Anomaly: {result['is_anomaly']}")
print(f"Score: {result['score']}")
print(f"Severity: {result['severity']}")
```

## Error Handling

### Backend Exceptions

- `ResourceNotFoundException` - Entity not found (404)
- `InvalidOperationException` - Invalid state transition (400)
- `UnauthorizedException` - Authentication failed (401)
- `ForbiddenException` - Authorization failed (403)

### Frontend Error Handling

```javascript
try {
  const data = await api.get('/endpoint')
} catch (error) {
  if (error.response?.status === 401) {
    // Redirect to login
    router.push('/login')
  } else if (error.response?.status === 404) {
    console.error('Resource not found')
  } else {
    console.error('Error:', error.message)
  }
}
```

## Performance Optimization

1. **Database Indexing:**
   - Create indexes on frequently queried fields
   - Use pagination with `.pageable()` in repositories

2. **Caching:**
   - Spring @Cacheable on frequently accessed data
   - Frontend component caching via computed properties

3. **API Response:**
   - Target: < 500ms for 95% of requests
   - Use DataTables pagination (default 10 items/page)

4. **Frontend Optimization:**
   - Lazy load routes
   - Component-level code splitting
   - Asset compression

## File Upload & Document Processing

### Invoice Processing

1. **Upload Flow:**
   - User uploads PDF/image file
   - Backend converts to text via OCR
   - `process_invoice_text()` extracts structured data
   - Results validated and stored in `Invoice` table

2. **OCR Confidence:**
   - Displayed as progress bar in InvoiceManagement.vue
   - Threshold: 85% confidence for auto-acceptance
   - Manual correction for lower confidence

## Testing

### Backend Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SupplyChainServiceTest
```

### Frontend Unit Tests

```bash
# Run tests
npm run test

# With coverage
npm run test:coverage
```

## Deployment

### Docker

```dockerfile
# Backend Dockerfile
FROM openjdk:21
COPY target/app-back-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  db:
    image: postgres:14
    environment:
      POSTGRES_DB: etap_db
      POSTGRES_PASSWORD: password
  
  backend:
    build: ./app-back
    ports:
      - "8080:8080"
    depends_on:
      - db
  
  frontend:
    build: ./app-front/consommables
    ports:
      - "5173:5173"
```

## Troubleshooting

### Common Issues

1. **Database Connection Error:**
   - Verify PostgreSQL is running
   - Check `application.properties` credentials
   - Ensure database exists

2. **JWT Token Invalid:**
   - Check token expiration in `application.properties`
   - Verify secret key matches between generation and validation

3. **Python Module Not Found:**
   - Ensure Python is in system PATH
   - Install required packages: `pip install python-docx`
   - Check module file paths are correct

4. **Port Already in Use:**
   - Backend: Change `server.port` in `application.properties`
   - Frontend: Change port in `vite.config.js`

## Next Steps & Future Enhancements

1. **Unit & Integration Tests** - Add comprehensive test coverage
2. **API Documentation** - Generate Swagger/OpenAPI specs
3. **Email Notifications** - Alert system for low stock, order approvals
4. **Advanced Reporting** - PDF generation, Excel export
5. **Mobile Application** - React Native or Flutter app
6. **Real-time Updates** - WebSocket integration for live data
7. **Multi-language Support** - Internationalization (i18n)
8. **Performance Tuning** - Database query optimization, caching strategies

## Support & Documentation

For issues or questions:
1. Check application logs (console for backend, browser console for frontend)
2. Review database schema in `target/generated-sources/annotations`
3. Consult Swagger API documentation (once generated)
4. Review inline code comments in service and entity classes

---

**Project Version:** 1.0.0  
**Last Updated:** 2024  
**Team:** ETAP Development Team
