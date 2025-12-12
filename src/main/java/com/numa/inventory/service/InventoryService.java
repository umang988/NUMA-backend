package com.numa.inventory.service;

import com.numa.inventory.dto.InventoryDto;
import com.numa.inventory.entity.Inventory;
import org.springframework.data.domain.Page;

public interface InventoryService {

    Inventory getInventoryById(Long id);
    Page<Inventory> getAllInventory(int page, int size, String searchQuery, String sortBy, String sortOrder);
    Inventory updateInventory(Long id, InventoryDto inventoryDto);
    Inventory createInventory(InventoryDto inventoryDto);
    void deleteInventory(Long id);
}