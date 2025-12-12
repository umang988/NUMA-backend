package com.numa.delivery.vehicles.entity;

import com.numa.audit.entity.AuditableEntity;
import com.numa.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicle")
public class Vehicle extends AuditableEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="vehicle_seq")
    @SequenceGenerator(name="vehicle_seq", schema="dbo", sequenceName= "vehicle_seq",allocationSize=1)
    @Column(name="id")
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String makeAndModel;

    @Column(nullable = false)
    private String type;

    private Double weightCapacity;
    private Double volumeCapacity;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_driver_id")
    private User assignedDriver;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    private String status;
}
