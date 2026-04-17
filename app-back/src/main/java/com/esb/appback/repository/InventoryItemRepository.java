package com.esb.appback.repository;

import com.esb.appback.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByInventoryId(Long inventoryId);
    List<InventoryItem> findByConsumableId(Long consumableId);
}
