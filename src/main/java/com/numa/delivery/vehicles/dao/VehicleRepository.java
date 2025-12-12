package com.numa.delivery.vehicles.dao;

import com.numa.delivery.vehicles.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v.code FROM Vehicle v ORDER BY v.code DESC LIMIT 1")
    String findLastVehicleCode();

}
