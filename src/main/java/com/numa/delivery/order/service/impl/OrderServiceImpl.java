package com.numa.delivery.order.service.impl;

import com.numa.delivery.order.dao.OrderRepository;
import com.numa.delivery.order.entity.Order;
import com.numa.delivery.order.service.OrderService;
import com.numa.generic.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GenericSpecification genericSpecification;



    @Override
    public Order getOrderById(Long id) {
        return genericSpecification.getEntityById(id, orderRepository, "Order");
    }

    @Override
    public Page<Order> getAllOrders(int page, int size, String searchQuery, String sortBy, String sortOrder) {
        return genericSpecification.getAllEntities(Order.class, orderRepository, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public Order createOrder(Order order) {
        if (order.getOrderItems() != null) {order.getOrderItems().forEach(item -> item.setOrder(order));}
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {

        Order existing = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        // ---------- UPDATE ONLY NON-NULL FIELDS ----------
        if (updatedOrder.getCustomerName() != null) existing.setCustomerName(updatedOrder.getCustomerName());
        if (updatedOrder.getEmail() != null) existing.setEmail(updatedOrder.getEmail());
        if (updatedOrder.getPhone() != null) existing.setPhone(updatedOrder.getPhone());
        if (updatedOrder.getDeliveryAddress() != null) existing.setDeliveryAddress(updatedOrder.getDeliveryAddress());
        if (updatedOrder.getDeliveryDate() != null) existing.setDeliveryDate(updatedOrder.getDeliveryDate());
        if (updatedOrder.getDeliveryTime() != null) existing.setDeliveryTime(updatedOrder.getDeliveryTime());
        if (updatedOrder.getSpecialInstructions() != null) existing.setSpecialInstructions(updatedOrder.getSpecialInstructions());

        // ---------- UPDATE ORDER ITEMS ----------
        if (updatedOrder.getOrderItems() != null) {
            existing.getOrderItems().clear();
            updatedOrder.getOrderItems().forEach(item -> {
                item.setOrder(existing);
                existing.getOrderItems().add(item);
            });
        }

        return orderRepository.save(existing);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}