package com.numa.delivery.vehicles.service.impl;

import com.numa.delivery.vehicles.dao.VehicleRepository;
import com.numa.delivery.vehicles.dto.VehicleRequest;
import com.numa.delivery.vehicles.entity.Vehicle;
import com.numa.delivery.vehicles.service.VehicleService;
import com.numa.user.dao.UserInfoRepository;
import com.numa.user.dao.UserRoleRepository;
import com.numa.user.entity.User;
import com.numa.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserInfoRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public Vehicle create(VehicleRequest dto) {
        Vehicle vehicle = mapToEntity(dto);
        vehicle.setCode(generateNextVehicleCode());
        return vehicleRepository.save(vehicle);
    }


    @Override
    public Vehicle update(Long id, VehicleRequest dto) {
        Vehicle vehicle = mapToEntity(dto);
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));

        existing.setType(vehicle.getType());
        existing.setLicensePlate(vehicle.getLicensePlate());
        existing.setMakeAndModel(vehicle.getMakeAndModel());
        existing.setWeightCapacity(vehicle.getWeightCapacity());
        existing.setVolumeCapacity(vehicle.getVolumeCapacity());
        existing.setImageUrl(vehicle.getImageUrl());
        existing.setAssignedDriver(vehicle.getAssignedDriver());
        existing.setNextMaintenanceDate(vehicle.getNextMaintenanceDate());
        existing.setStatus(vehicle.getStatus());

        return vehicleRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    private Vehicle mapToEntity(VehicleRequest req) {

        Vehicle v = new Vehicle();
        v.setCode(req.getCode());
        v.setType(req.getType());
        v.setLicensePlate(req.getLicensePlate());
        v.setMakeAndModel(req.getMakeAndModel());
        v.setWeightCapacity(req.getWeightCapacity());
        v.setVolumeCapacity(req.getVolumeCapacity());
        v.setImageUrl(req.getImageUrl());
        v.setNextMaintenanceDate(req.getNextMaintenanceDate());
        v.setStatus(req.getStatus());

        if (req.getAssignedDriverId() != null) {
            User driver = userRepository.findById(req.getAssignedDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found with id: " + req.getAssignedDriverId()));

            UserRole role = userRoleRepository.getReferenceById(driver.getRoleId());

            if (!role.getName().equalsIgnoreCase("driver")) {
                throw new IllegalArgumentException("User with id " + req.getAssignedDriverId() + " is not a driver");
            }

            v.setAssignedDriver(driver);
        } else {
            v.setAssignedDriver(null);
        }

        return v;
    }

    private String generateNextVehicleCode() {
        String lastCode = vehicleRepository.findLastVehicleCode();

        if (lastCode == null) {
            return "V001";
        }

        int number = Integer.parseInt(lastCode.substring(1));
        number++;

        return String.format("V%03d", number);
    }

}

