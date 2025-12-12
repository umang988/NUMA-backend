package com.numa.delivery.order.service;

import com.numa.delivery.order.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderService {

    Order getOrderById(Long id);
    Page<Order> getAllOrders(int page, int size, String searchQuery, String sortBy, String sortOrder);
    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    void deleteOrder(Long id);
}
