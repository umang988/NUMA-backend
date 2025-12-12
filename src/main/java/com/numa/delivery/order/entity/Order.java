package com.numa.delivery.order.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.numa.audit.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer Details
    @NotNull
    private String customerName;
    @NotNull
    private String email;
    @NotNull
    private String phone;

    // Delivery Information
    @NotNull
    private String deliveryAddress;
    @NotNull
    private LocalDate deliveryDate;
    @NotNull
    private LocalTime deliveryTime;

    // Special Instructions
    private String specialInstructions;

    //Order Detail
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalWeight;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double totalVolume;

    // Order Items
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> orderItems;

    @PrePersist
    @PreUpdate
    public void calculateTotals() {

        if (orderItems == null || orderItems.isEmpty()) {
            this.totalWeight = 0.0;
            this.totalVolume = 0.0;
            return;
        }

        this.totalWeight = orderItems.stream()
                .mapToDouble(item -> {
                    Double weight = item.getProduct().getWeight();
                    return (weight != null ? weight : 0.0) * item.getQuantity();
                })
                .sum();

        this.totalVolume = orderItems.stream()
                .mapToDouble(item -> {
                    Double volume = item.getProduct().getVolume();
                    return (volume != null ? volume : 0.0) * item.getQuantity();
                })
                .sum();
    }
}
