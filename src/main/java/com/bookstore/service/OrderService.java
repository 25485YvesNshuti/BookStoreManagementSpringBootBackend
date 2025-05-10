package com.bookstore.service;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import com.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Create a new order.
     */
    public Order createOrder(Order order) {
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(item -> item.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        return orderRepository.save(order);
    }


    /**
     * Retrieve all orders for a specific user with pagination.
     */
    public Page<Order> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    /**
     * Retrieve all orders for a specific user.
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * Retrieve all orders by status with pagination.
     */
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    /**
     * Retrieve all orders by status.
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Retrieve an order by its ID.
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Update an order's status and payment status.
     */
    public Order updateOrder(Long id, Order.OrderStatus status, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
        order.setStatus(status);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    /**
     * Delete an order by its ID.
     */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    /**
     * Retrieve all orders (for admin dashboard) with pagination.
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Retrieve all orders (for admin dashboard).
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Calculate the total revenue from all orders.
     */
    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    /**
     * Get all orders created today.
     */
    public List<Order> getNewOrdersToday() {
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Kigali")); // Using Kigali time zone
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    /**
     * Global search for orders with pagination.
     */
    public Page<Order> searchGlobal(String searchTerm, Pageable pageable) {
        return orderRepository.searchGlobal(searchTerm, pageable);
    }

    /**
     * Get total order count
     * @return
     */
    public long getTotalOrderCount(){
        return orderRepository.count();
    }
}