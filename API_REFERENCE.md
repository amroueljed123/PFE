# API Reference Guide - Test All Endpoints

## Prerequisites

Backend running on `http://localhost:8080`

Get JWT token first:
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"username":"admin","password":"password"}'

$token = ($response.Content | ConvertFrom-Json).token
```

---

## SupplyChain API (20+ endpoints)

### Create Supplier
```powershell
$headers = @{"Authorization"="Bearer $token"}

$body = @{
    name = "Supplier Corp"
    email = "contact@supplier.com"
    phone = "+213 555 123456"
    address = "Algiers, Algeria"
    rating = 4.5
    paymentTerms = 30
    active = $true
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/suppliers" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$supplierId = ($response.Content | ConvertFrom-Json).id
```

### Get All Suppliers
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/suppliers" `
  -Method Get `
  -Headers $headers

$suppliers = $response.Content | ConvertFrom-Json
$suppliers | Format-Table -Property id, name, rating, active
```

### Create Purchase Order
```powershell
$body = @{
    supplierId = $supplierId
    orderId = "PO-2024-001"
    orderDate = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
    requiredDeliveryDate = (Get-Date).AddMonths(1).ToString("yyyy-MM-dd'T'HH:mm:ss")
    status = "DRAFT"
    createdBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/purchase-orders" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$orderId = ($response.Content | ConvertFrom-Json).id
```

### Get Purchase Orders by Status
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/purchase-orders/status/DRAFT" `
  -Method Get `
  -Headers $headers

$orders = $response.Content | ConvertFrom-Json
$orders | Format-Table -Property id, orderNumber, status, totalAmount
```

### Submit Order for Approval (Level 1)
```powershell
$body = @{
    orderId = $orderId
    validationLevel = 1
    validatorId = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/orders/$orderId/submit-validation" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Approve Order
```powershell
$body = @{
    validationId = 1
    comments = "Approved - within budget"
    approvedBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/orders/validate" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Record Reception
```powershell
$body = @{
    orderId = $orderId
    receivedDate = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
    receivedBy = "John Doe"
    notes = "All items received in good condition"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/supply-chain/receptions" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$receptionId = ($response.Content | ConvertFrom-Json).id
```

---

## Inventory API (15+ endpoints)

### Record Stock Movement (Receive goods)
```powershell
$body = @{
    consumableId = 1
    type = "IN"
    quantity = 50
    userId = 1
    notes = "Receipt from PO-2024-001"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/stock-movements" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Record Stock Movement (Issue items)
```powershell
$body = @{
    consumableId = 1
    type = "OUT"
    quantity = 5
    userId = 1
    notes = "Issued to Vehicle 291-ALG-2024"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/stock-movements" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get Stock Movements for Consumable
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/consumable/1/movements" `
  -Method Get `
  -Headers $headers

$movements = $response.Content | ConvertFrom-Json
$movements | Format-Table -Property date, type, quantity, movementBy
```

### Create Stock Alert (Low stock warning)
```powershell
$body = @{
    consumableId = 1
    type = "LOW_STOCK"
    level = 10
    status = "ACTIVE"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/stock-alerts" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get Active Stock Alerts
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/stock-alerts/active" `
  -Method Get `
  -Headers $headers

$alerts = $response.Content | ConvertFrom-Json
$alerts | Format-Table -Property id, consumableId, type, level, status
```

### Create Physical Inventory
```powershell
$body = @{
    date = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
    status = "PLANNED"
    description = "Monthly inventory count"
    createdBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/inventories" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$inventoryId = ($response.Content | ConvertFrom-Json).id
```

### Start Inventory Count
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/inventories/$inventoryId/start" `
  -Method Put `
  -Headers $headers
```

### Add Items to Inventory Count
```powershell
$body = @{
    consumableId = 1
    physicalCount = 45
    countedBy = "John Doe"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/inventories/$inventoryId/items" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Complete Inventory Count
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/inventory/inventories/$inventoryId/complete" `
  -Method Put `
  -Headers $headers
```

---

## Analytics API (15+ endpoints)

### Detect Anomalies (Calls Python ML)
```powershell
$body = @{
    entityType = "CONSUMABLE"
    entityId = 1
    anomalyType = "UNUSUAL_CONSUMPTION"
    description = "Consumption spike detected"
    values = @(100.0, 102.0, 98.0, 105.0, 500.0)  # Last one is anomaly
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/anomalies/detect" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$anomaly = $response.Content | ConvertFrom-Json
Write-Host "Anomaly Score: $($anomaly.anomalyScore)"
Write-Host "Severity: $($anomaly.severity)"
```

### Get Active Anomalies
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/anomalies/active" `
  -Method Get `
  -Headers $headers

$anomalies = $response.Content | ConvertFrom-Json
$anomalies | Format-Table -Property id, anomalyType, anomalyScore, severity
```

### Get Anomalies by Severity
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/anomalies/severity/CRITICAL" `
  -Method Get `
  -Headers $headers

$critical = $response.Content | ConvertFrom-Json
```

### Create Forecast (Calls Python ML)
```powershell
$body = @{
    consumableId = 1
    algorithmUsed = "EXPONENTIAL_SMOOTHING"
    horizonDays = 30
    forecast = @(45, 48, 50, 52, 55, 56, 58, 59, 60)  # 9 data points for 30 days
    confidence = 85.0
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/forecasts" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get Forecasts for Consumable
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/consumable/1/forecasts" `
  -Method Get `
  -Headers $headers

$forecasts = $response.Content | ConvertFrom-Json
$forecasts | Format-Table -Property id, algorithmUsed, horizonDays, confidence
```

### Create Optimization Recommendation
```powershell
$body = @{
    consumableId = 1
    type = "STOCK_OPTIMIZATION"
    recommendation = "Increase reorder point to 100 units based on forecasts"
    expectedSavings = 5000.0
    priority = "HIGH"
    createdBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/recommendations" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get High Priority Recommendations
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/recommendations/priority/HIGH" `
  -Method Get `
  -Headers $headers

$recommendations = $response.Content | ConvertFrom-Json
```

### Record Performance KPI
```powershell
$body = @{
    entityType = "CONSUMABLE"
    entityId = 1
    kpiName = "STOCK_TURNOVER"
    kpiValue = 12.5
    period = "MONTH"
    recordedDate = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/analytics/kpis" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

---

## Vehicle API (12+ endpoints)

### Create Vehicle
```powershell
$body = @{
    registration = "291-ALG-2024"
    brand = "Toyota"
    model = "Hilux"
    year = 2024
    type = "TRUCK"
    status = "ACTIVE"
    fuelType = "DIESEL"
    mileage = 0
    purchaseDate = "2024-01-01"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$vehicleId = ($response.Content | ConvertFrom-Json).id
```

### Get All Vehicles
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles" `
  -Method Get `
  -Headers $headers

$vehicles = $response.Content | ConvertFrom-Json
$vehicles | Format-Table -Property id, registration, brand, model, status
```

### Get Vehicles by Status
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles/status/ACTIVE" `
  -Method Get `
  -Headers $headers
```

### Schedule Maintenance
```powershell
$body = @{
    articleId = $vehicleId
    type = "PREVENTIVE"
    date = (Get-Date).AddMonths(3).ToString("yyyy-MM-dd'T'HH:mm:ss")
    description = "Regular oil change and filter replacement"
    estimatedCost = 1500.0
    scheduledBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles/maintenance" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Record Maintenance Completion
```powershell
$body = @{
    date = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
    actualCost = 1500.0
    performedBy = "Mechanic John"
    notes = "Completed successfully"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles/maintenance/1/complete" `
  -Method Put `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get Upcoming Maintenance
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/vehicles/maintenance/upcoming" `
  -Method Get `
  -Headers $headers

$upcoming = $response.Content | ConvertFrom-Json
$upcoming | Format-Table -Property id, articleId, type, date, estimatedCost
```

---

## Invoice API (8+ endpoints)

### Create Invoice
```powershell
$body = @{
    invoiceNumber = "INV-2024-001"
    supplierId = 1
    date = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")
    dueDate = (Get-Date).AddDays(30).ToString("yyyy-MM-dd'T'HH:mm:ss")
    amountHT = 10000.0
    tva = 2000.0
    amountTTC = 12000.0
    status = "PENDING"
    createdBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$invoiceId = ($response.Content | ConvertFrom-Json).id
```

### Get All Invoices
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices" `
  -Method Get `
  -Headers $headers

$invoices = $response.Content | ConvertFrom-Json
$invoices | Format-Table -Property id, invoiceNumber, supplierId, amountTTC, status
```

### Get Invoices by Status
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/status/PENDING" `
  -Method Get `
  -Headers $headers
```

### Process Invoice OCR
```powershell
$body = @{
    invoiceId = $invoiceId
    documentText = "AMAZON INVOICE INV-2024-001 Date: 2024-01-15 Total: 12000 DA"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/$invoiceId/process-ocr" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body

$ocrResult = $response.Content | ConvertFrom-Json
Write-Host "OCR Confidence: $($ocrResult.confidence * 100)%"
Write-Host "Extracted Amount: $($ocrResult.extractedAmount) DA"
```

### Add Invoice Item
```powershell
$body = @{
    invoiceId = $invoiceId
    description = "Office Supplies"
    quantity = 10
    unitPrice = 1000.0
    lineTotal = 10000.0
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/$invoiceId/items" `
  -Method Post `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

### Get Invoice Items
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/$invoiceId/items" `
  -Method Get `
  -Headers $headers

$items = $response.Content | ConvertFrom-Json
$items | Format-Table -Property id, description, quantity, unitPrice, lineTotal
```

### Update Invoice Status
```powershell
$body = @{
    status = "PROCESSED"
    updatedBy = 1
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/$invoiceId/status" `
  -Method Put `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $body
```

---

## Audit API (5+ endpoints)

### Get Audit Logs
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/logs" `
  -Method Get `
  -Headers $headers

$logs = $response.Content | ConvertFrom-Json
$logs | Format-Table -Property timestamp, userId, action, entity
```

### Get Audit Logs for User
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/user/1/logs" `
  -Method Get `
  -Headers $headers
```

### Get Audit Logs for Entity
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/entity/SUPPLIER/logs" `
  -Method Get `
  -Headers $headers
```

### Get Audit Logs by Date Range
```powershell
$start = (Get-Date).AddDays(-7).ToString("yyyy-MM-dd'T'HH:mm:ss")
$end = (Get-Date).ToString("yyyy-MM-dd'T'HH:mm:ss")

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/logs/between?start=$start&end=$end" `
  -Method Get `
  -Headers $headers
```

### Get Audit Statistics
```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/statistics" `
  -Method Get `
  -Headers $headers

$stats = $response.Content | ConvertFrom-Json
```

---

## Error Handling

All endpoints return standard error format:

```json
{
  "error": "Resource not found",
  "status": 404,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/supply-chain/suppliers/999"
}
```

Common Status Codes:
- `200`: OK - Success
- `201`: Created - Resource created successfully
- `400`: Bad Request - Invalid input
- `401`: Unauthorized - Missing or invalid token
- `403`: Forbidden - Insufficient permissions
- `404`: Not Found - Resource not found
- `500`: Internal Server Error - Server error

---

## Testing Checklist

After starting backend on localhost:8080:

```powershell
# 1. Login and get token
Write-Host "1. Testing Login..."
$token = # ... (get token as shown above)

# 2. Test Supply Chain
Write-Host "2. Testing Supply Chain..."
# Create supplier -> Create order -> Submit for approval -> Approve

# 3. Test Inventory
Write-Host "3. Testing Inventory..."
# Record stock in -> Record stock out -> View movements

# 4. Test Analytics (Python integration)
Write-Host "4. Testing Analytics..."
# Detect anomalies -> Create forecast -> Create recommendation

# 5. Test Vehicles
Write-Host "5. Testing Vehicles..."
# Create vehicle -> Schedule maintenance -> View upcoming

# 6. Test Invoices
Write-Host "6. Testing Invoices..."
# Create invoice -> Process OCR -> Update status

# 7. Check Audit Log
Write-Host "7. Checking Audit Log..."
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/audit/logs" -Headers @{"Authorization"="Bearer $token"}
$logs = $response.Content | ConvertFrom-Json
Write-Host "Total actions logged: $($logs.Count)"
```

---

**All 75+ endpoints are ready for testing!**
