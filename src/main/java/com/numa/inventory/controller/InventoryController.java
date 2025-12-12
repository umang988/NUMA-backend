package com.numa.inventory.controller;

import com.numa.inventory.dto.InventoryDto;
import com.numa.inventory.entity.Inventory;
import com.numa.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService service;



    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInventoryById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<Page<Inventory>> getAllInventory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String searchQuery,
            @RequestParam(value = "sort", defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(service.getAllInventory(page, size, searchQuery, sortBy, sortOrder));
    }

    @PostMapping("/create")
    public ResponseEntity<Inventory> createProduct(@Valid @RequestBody InventoryDto inventoryDto) {
        return ResponseEntity.ok(service.createInventory(inventoryDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Inventory> updateProduct(@PathVariable Long id, @RequestBody InventoryDto inventoryDto) {
        return ResponseEntity.ok(service.updateInventory(id, inventoryDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
