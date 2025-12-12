package com.numa.delivery.vehicles.service;


import com.numa.delivery.vehicles.dto.VehicleRequest;
import com.numa.delivery.vehicles.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle create(VehicleRequest vehicle);
    Vehicle update(Long id, VehicleRequest vehicle);
    void delete(Long id);
    Vehicle getById(Long id);
    List<Vehicle> getAll();
}

