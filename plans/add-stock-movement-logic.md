# Plan: Add Stock Movement Functionality

## Objective
Enable adding new Entrée (In) and Sortie (Out) movements, which will both record the history and update the actual article stock quantity.

## Backend Changes

### 1. `InventoryService.java`
- Update `recordStockMovement` to:
    - Accept `StockMovementDTO` or individual fields (Reference, Fournisseur, Document, Cost).
    - Find the `Article` by ID.
    - Update the article's `quantite` (add for IN, subtract for OUT).
    - Save the enriched `StockMovement`.
    - Use `@Transactional` to ensure atomicity.

### 2. `InventoryController.java`
- Add a new `@PostMapping("/movements/create")` endpoint that takes a `StockMovementDTO`.

## Frontend Changes

### 1. `MouvementsStock.vue`
- Implement `movementDialog` (True/False).
- Create a form in the dialog:
    - **Search/Select Article**: An autocomplete field to pick from existing articles.
    - **Type**: Radio toggle (Entrée / Sortie).
    - **Quantity**: Numeric input.
    - **Metadata**: Fields for Reference, Fournisseur (if IN), Document, Description.
    - **Total Cost**: Auto-calculate or manual input.
- Method `saveNewMovement()`:
    - Call the new backend endpoint.
    - Refresh the table on success.
    - Show success snackbar.

## Verification
- Record an "Entrée" of 10 units for an article and check if its stock increases.
- Record a "Sortie" of 5 units and check if its stock decreases.
- Verify both appear in the "Flux de Stock" history table.
