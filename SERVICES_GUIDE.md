# Backend Services Guide - How to Use Each Service

## Overview
6 main services handle all business logic in the system. Each is auto-available via Spring dependency injection.

---

## 1. SupplyChainService

**Location:** `app-back/src/main/java/com/esb/appback/service/SupplyChainService.java`

**Purpose:** Manage suppliers, purchase orders, validations, and receptions

### Usage Examples

```java
@Autowired
private SupplyChainService supplyChainService;

// Create supplier
Supplier supplier = new Supplier();
supplier.setName("Supplier Name");
supplier.setEmail("supplier@example.com");
supplier.setPhone("+213 555 123456");
supplier.setAddress("Algiers");
supplier.setRating(4.5);
supplier.setPaymentTerms(30);
supplier.setActive(true);

Supplier created = supplyChainService.createSupplier(supplier);
```

### Available Methods

```java
// Suppliers
createSupplier(Supplier)
getAllSuppliers()
getSupplier(Long id)
updateSupplier(Long id, Supplier)
deleteSupplier(Long id)
getSuppliersByRating(Double minRating)

// Purchase Orders
createPurchaseOrder(PurchaseOrder)
getPurchaseOrder(Long id)
getPurchaseOrdersByStatus(String status)  // DRAFT, SUBMITTED, APPROVED, etc
getPurchaseOrdersBySupplier(Long supplierId)
updatePurchaseOrder(Long id, PurchaseOrder)

// Order Items
addOrderItem(Long orderId, OrderItem)
getOrderItems(Long orderId)

// Validations (Multi-level approval)
submitForValidation(Long orderId, Integer level, Long validatorId)
approveOrder(Long validationId, String comments)
getPendingValidations()
getValidationsByLevel(Integer level)

// Receptions
recordReception(Long orderId, LocalDateTime date, String receivedBy)
addReceptionItem(Long receptionId, ReceptionItem)
getReceptionsByOrder(Long orderId)
```

---

## 2. InventoryService

**Location:** `app-back/src/main/java/com/esb/appback/service/InventoryService.java`

**Purpose:** Manage stock movements, inventory counts, alerts

### Usage Examples

```java
@Autowired
private InventoryService inventoryService;

// Record stock movement (e.g., receive new items)
StockMovement movement = inventoryService.recordStockMovement(
    consumableId = 1L,
    type = "IN",           // IN or OUT
    quantity = 50,
    userId = 1L
);

// Create low stock alert
StockAlert alert = new StockAlert();
alert.setConsumableId(1L);
alert.setType("LOW_STOCK");
alert.setLevel(10);       // Alert when <= 10 items
alert.setStatus("ACTIVE");
inventoryService.createStockAlert(alert);
```

### Available Methods

```java
// Stock Movements
recordStockMovement(Long consumableId, String type, Integer qty, Long userId)
getStockMovementsByConsumable(Long consumableId)
getStockMovementsBetween(LocalDateTime start, LocalDateTime end)
getStockMovementsByType(String type)

// Stock Alerts
createStockAlert(StockAlert)
getActiveStockAlerts()
getStockAlertsByConsumable(Long consumableId)
resolveStockAlert(Long alertId, String resolution)
getAlertsByType(String type)  // LOW_STOCK, REORDER, EXCESS

// Inventory Counts (Physical)
createInventory(Inventory)
startInventory(Long id)
completeInventory(Long id)
addInventoryItem(Long inventoryId, InventoryItem)
getInventoryItems(Long inventoryId)
getInventoriesByStatus(String status)  // PLANNED, IN_PROGRESS, etc

// Categories
createCategory(ConsumableCategory)
getAllCategories()
getCategoryByName(String name)
```

---

## 3. AnalyticsService

**Location:** `app-back/src/main/java/com/esb/appback/service/AnalyticsService.java`

**Purpose:** Analytics, forecasting, anomaly detection, recommendations

### Usage Examples

```java
@Autowired
private AnalyticsService analyticsService;

// Detect anomalies (calls Python module)
AnomalyDetection anomaly = new AnomalyDetection();
anomaly.setEntityType("CONSUMABLE");
anomaly.setEntityId(1L);
anomaly.setAnomalyType("UNUSUAL_CONSUMPTION");
anomaly.setDescription("Consumption 5x higher than average");
anomaly.setAnomalyScore(89);  // 0-100
AnomalyDetection detected = analyticsService.detectAnomaly(anomaly);

// Create forecast
ConsumptionForecast forecast = new ConsumptionForecast();
forecast.setConsumableId(1L);
forecast.setAlgorithmUsed("PROPHET");
forecast.setHorizonDays(30);
forecast.setConfidence(85.0);
ConsumptionForecast saved = analyticsService.createForecast(forecast);

// Create recommendation
OptimizationRecommendation rec = new OptimizationRecommendation();
rec.setType("STOCK_OPTIMIZATION");
rec.setRecommendation("Increase reorder point to 100 units");
rec.setExpectedSavings(5000.0);
rec.setPriority("HIGH");
OptimizationRecommendation created = analyticsService.createRecommendation(rec);
```

### Available Methods

```java
// Forecasts
createForecast(ConsumptionForecast)
getForecastsForConsumable(Long consumableId)
getForecastsByAlgorithm(String algorithm)  // ARIMA, PROPHET, EMA
updateForecast(Long id, ConsumptionForecast)

// Anomalies
detectAnomaly(AnomalyDetection)
getActiveAnomalies()
getAnomaliesBySeverity(String severity)  // LOW, MEDIUM, HIGH, CRITICAL
getAnomaliesByType(String type)
investigateAnomaly(Long id, Long userId, String notes)
getInvestigatedAnomalies()

// Recommendations
createRecommendation(OptimizationRecommendation)
getRecommendationsByStatus(String status)  // NEW, REVIEWED, APPROVED, etc
getHighPriorityRecommendations()
approveRecommendation(Long id)
rejectRecommendation(Long id, String reason)
getRecommendationsByType(String type)

// KPIs
recordKPI(PerformanceKPI)
getKPIsForEntity(String entityType, Long entityId)
getKPIsByPeriod(String period)  // DAY, WEEK, MONTH, QUARTER, YEAR
getKPIsByName(String kpiName)
calculateAverageKPI(String kpiName)
```

---

## 4. VehicleService

**Location:** `app-back/src/main/java/com/esb/appback/service/VehicleService.java`

**Purpose:** Manage vehicles, equipment, and maintenance

### Usage Examples

```java
@Autowired
private VehicleService vehicleService;

// Create vehicle
Vehicle vehicle = new Vehicle();
vehicle.setRegistration("291-ALG-2024");
vehicle.setBrand("Toyota");
vehicle.setModel("Hilux");
vehicle.setYear(2024);
vehicle.setType("TRUCK");
vehicle.setStatus("ACTIVE");
vehicle.setFuelType("DIESEL");
vehicle.setMileage(0);
Vehicle created = vehicleService.createVehicle(vehicle);

// Schedule maintenance
Maintenance maintenance = new Maintenance();
maintenance.setArticleId(created.getId());
maintenance.setType("PREVENTIVE");
maintenance.setDate(LocalDateTime.now().plusMonths(3));
maintenance.setCost(1500.0);
Maintenance scheduled = vehicleService.scheduleMaintenance(maintenance);
```

### Available Methods

```java
// Vehicles
createVehicle(Vehicle)
getAllVehicles()
getVehicle(Long id)
getVehiclesByStatus(String status)  // ACTIVE, MAINTENANCE, OUT_OF_SERVICE
updateVehicle(Long id, Vehicle)
deleteVehicle(Long id)
getVehiclesByBrand(String brand)

// Equipment
createEquipment(Equipment)
getAllEquipment()
getEquipment(Long id)
getEquipmentByType(String type)
getEquipmentByService(Long serviceId)
updateEquipment(Long id, Equipment)

// Maintenance
scheduleMaintenance(Maintenance)
recordMaintenance(Long id, LocalDateTime date)
getMaintenanceHistory(Long articleId)
getMaintenanceByType(String type)  // ROUTINE, PREVENTIVE, CORRECTIVE
getUpcomingMaintenance()
getMaintenancesByDateRange(LocalDateTime start, LocalDateTime end)

// Services (Departments)
createService(Service)
getAllServices()
getService(Long id)
updateService(Long id, Service)
```

---

## 5. AuditService

**Location:** `app-back/src/main/java/com/esb/appback/service/AuditService.java`

**Purpose:** Log all operations for compliance and troubleshooting

### Usage Examples

```java
@Autowired
private AuditService auditService;

// Log user action
AuditLog log = auditService.logAction(
    userId = 1L,
    action = "CREATE",           // CREATE, UPDATE, DELETE, VIEW
    entity = "SUPPLIER",
    oldValue = null,
    newValue = "New Supplier Inc",
    ipAddress = "192.168.1.100"
);

// Query audit logs
List<AuditLog> userLogs = auditService.getAuditLogsForUser(1L);
List<AuditLog> supplierLogs = auditService.getAuditLogsForEntity("SUPPLIER");
List<AuditLog> lastWeek = auditService.getAuditLogsBetween(
    startDate,
    endDate
);
```

### Available Methods

```java
// Logging
logAction(Long userId, String action, String entity, String oldValue, String newValue, String ipAddress)

// Querying
getAuditLogsForUser(Long userId)
getAuditLogsForEntity(String entity)
getAuditLogsBetween(LocalDateTime start, LocalDateTime end)
getAllAuditLogs()
getAuditLogsByAction(String action)

// Reporting
countActionsByUser(Long userId)
countActionsByType(String action)
getLastActionForEntity(String entity, Long entityId)
```

---

## 6. InvoiceService

**Location:** `app-back/src/main/java/com/esb/appback/service/InvoiceService.java`

**Purpose:** Manage invoices with OCR integration

### Usage Examples

```java
@Autowired
private InvoiceService invoiceService;
@Autowired
private OCRService ocrService;

// Create invoice
Invoice invoice = new Invoice();
invoice.setInvoiceNumber("INV-2024-001");
invoice.setSupplierId(1L);
invoice.setDate(LocalDateTime.now());
invoice.setAmountHT(10000.0);
invoice.setTva(2000.0);
invoice.setAmountTTC(12000.0);
invoice.setStatus("PENDING");
Invoice created = invoiceService.createInvoice(invoice);

// Process OCR for invoice text extraction
OCRService.OCRResult ocrResult = ocrService.processInvoice(pdfText);
if (ocrResult.getValid()) {
    invoice.setOcrConfidence(ocrResult.getConfidence());
    invoice.setStatus("PROCESSED");
    invoiceService.updateInvoice(invoice.getId(), invoice);
}
```

### Available Methods

```java
// Invoices
createInvoice(Invoice)
getInvoice(Long id)
getAllInvoices()
getInvoicesByStatus(String status)  // PENDING, PROCESSED, VERIFIED
getInvoicesBySupplier(Long supplierId)
updateInvoice(Long id, Invoice)
deleteInvoice(Long id)
getInvoicesByDateRange(LocalDateTime start, LocalDateTime end)

// Invoice Items
addInvoiceItem(Long invoiceId, InvoiceItem)
getInvoiceItems(Long invoiceId)
updateInvoiceItem(Long id, InvoiceItem)
deleteInvoiceItem(Long id)

// OCR Processing (via OCRService)
processInvoiceText(String text)  // Calls Python OCR module
```

---

## 7. OCRService (Utility)

**Location:** `app-back/src/main/java/com/esb/appback/util/OCRService.java`

**Purpose:** Extract structured data from invoice documents

### Usage Example

```java
@Autowired
private OCRService ocrService;

// Process invoice PDF/image text
String invoiceText = "... extracted from PDF ...";
OCRService.OCRResult result = ocrService.processInvoice(invoiceText);

// Access extracted data
System.out.println("Invoice #: " + result.getInvoiceNumber());
System.out.println("Supplier: " + result.getSupplier());
System.out.println("Date: " + result.getDate());
System.out.println("Amount TTC: " + result.getAmountTTC());
System.out.println("Confidence: " + (result.getConfidence() * 100) + "%");
System.out.println("Valid: " + result.getValid());
```

---

## 8. PythonIntegration (Utility)

**Location:** `app-back/src/main/java/com/esb/appback/util/PythonIntegration.java`

**Purpose:** Call Python ML modules from Java

### Usage Example

```java
@Autowired
private PythonIntegration pythonIntegration;

// Detect anomalies using Python
Map<String, Object> result = pythonIntegration.detectAnomalies(
    List.of(100.0, 102.0, 98.0, 105.0, 103.0),  // history
    500.0  // current value
);

// Result contains: is_anomaly, score, severity, expected_range

// Forecast consumption
Map<String, Object> forecast = pythonIntegration.forecastConsumption(
    List.of(50.0, 55.0, 52.0, 58.0, 60.0),  // history
    30,      // periods to forecast
    "sma"    // method: sma, es
);

// Optimize stock
Map<String, Object> optimization = pythonIntegration.optimizeStock(
    50000.0,   // annual demand
    100.0,     // ordering cost
    5.0,       // holding cost
    14,        // lead time
    500.0,     // std dev demand
    "95%"      // service level
);
```

---

## How Services Work Together

```
REST Controller (API Layer)
    ↓
Validates input & gets JWT token
    ↓
Calls appropriate Service
    ↓
Service performs business logic
    ↓
Service calls Repository (JPA)
    ↓
Repository queries/updates Database
    ↓
May call Python Integration → Python ML Module
    ↓
Results returned to Controller
    ↓
Controller returns JSON response to Frontend
```

---

## Common Patterns

### Pattern 1: Create Entity
```java
// In Service method
Entity entity = new Entity();
entity.setField1(value1);
entity.setField2(value2);
return repository.save(entity);
```

### Pattern 2: Update Entity
```java
// In Service method
Entity existing = repository.findById(id).orElseThrow(...)
existing.setField1(newValue);
return repository.save(existing);
```

### Pattern 3: Query with Filter
```java
// In Service method
List<Entity> results = repository.findByStatus(status);
return results;
```

### Pattern 4: Python Integration
```java
// In Service method
Map<String, Object> result = pythonIntegration.someMethod(...);
// Process result and save to database
```

---

## Error Handling

All services throw checked exceptions:

```java
try {
    Supplier supplier = supplyChainService.createSupplier(data);
} catch (InvalidOperationException e) {
    // Handle invalid state
} catch (ResourceNotFoundException e) {
    // Handle not found
} catch (Exception e) {
    // Handle generic error
}
```

---

## Testing Services

```java
@SpringBootTest
class SupplyChainServiceTest {
    
    @Autowired
    private SupplyChainService service;
    
    @Autowired
    private SupplierRepository repository;
    
    @Test
    void testCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("Test");
        
        Supplier created = service.createSupplier(supplier);
        
        assertNotNull(created.getId());
        assertEquals("Test", created.getName());
    }
}
```

---

**Service Documentation Complete**

All 6 services + 2 utilities are fully functional and integrated!
