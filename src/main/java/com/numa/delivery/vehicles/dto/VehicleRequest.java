package com.numa.delivery.vehicles.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class VehicleRequest {
    private String code;
    private String type;
    private String licensePlate;
    private String makeAndModel;
    private Double weightCapacity;
    private Double volumeCapacity;
    private String imageUrl;
    private Long assignedDriverId;
    private LocalDate nextMaintenanceDate;
    private String status;
}

