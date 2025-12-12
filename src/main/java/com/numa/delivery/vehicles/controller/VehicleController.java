package com.numa.delivery.vehicles.controller;

import com.numa.delivery.vehicles.dto.VehicleRequest;
import com.numa.delivery.vehicles.entity.Vehicle;
import com.numa.delivery.vehicles.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> create(@RequestBody VehicleRequest request) {

        Vehicle saved = vehicleService.create(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> update(@PathVariable Long id,
                                          @RequestBody VehicleRequest request) {

        Vehicle updated = vehicleService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
