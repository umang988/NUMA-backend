package com.numa.inventory.service.impl;

import com.numa.generic.GenericSpecification;
import com.numa.inventory.dao.InventoryRepository;
import com.numa.inventory.dto.InventoryDto;
import com.numa.inventory.entity.Inventory;
import com.numa.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private GenericSpecification genericSpecification;



    @Override
    public Inventory getInventoryById(Long id) {
        return genericSpecification.getEntityById(id, inventoryRepository, "Inventory");
    }

    @Override
    public Page<Inventory> getAllInventory(int page, int size, String searchQuery, String sortBy, String sortOrder) {
        return genericSpecification.getAllEntities(Inventory.class, inventoryRepository, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public Inventory createInventory(InventoryDto inventoryDto) {
        return genericSpecification.saveOrUpdateEntityWithResponse(null, inventoryDto, new Inventory(), inventoryRepository);
    }

    @Override
    public Inventory updateInventory(Long id, InventoryDto inventoryDto) {
        return genericSpecification.saveOrUpdateEntityWithResponse(id, inventoryDto, new Inventory(), inventoryRepository);
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}