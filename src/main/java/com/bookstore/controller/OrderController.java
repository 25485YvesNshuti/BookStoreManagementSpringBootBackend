package com.bookstore.controller;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * Retrieve all orders for a specific user with pagination.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = new User(); // Simplified, replace with actual user retrieval
        user.setId(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderService.getOrdersByUser(user, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("totalElements", orderPage.getTotalElements());
        response.put("currentPage", orderPage.getNumber());
        response.put("pageSize", orderPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all orders by status with pagination.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderService.getOrdersByStatus(status, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("totalElements", orderPage.getTotalElements());
        response.put("currentPage", orderPage.getNumber());
        response.put("pageSize", orderPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve an order by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an order's status and payment status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @RequestParam Order.OrderStatus status,
                                             @RequestParam Order.PaymentStatus paymentStatus) {
        Order updatedOrder = orderService.updateOrder(id, status, paymentStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Delete an order by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully.");
    }

    /**
     * Global search for orders with pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchOrdersGlobal(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderService.searchGlobal(searchTerm, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("totalElements", orderPage.getTotalElements());
        response.put("currentPage", orderPage.getNumber());
        response.put("pageSize", orderPage.getSize());
        response.put("searchTerm", searchTerm);
        return ResponseEntity.ok(response);
    }
}