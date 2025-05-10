package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * Add an item to an order.
     */
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestParam Long orderId,
                                                     @RequestParam Long bookId,
                                                     @RequestParam Integer quantity,
                                                     @RequestParam BigDecimal unitPrice) {
        Order order = new Order(); // Simplified, replace with actual order retrieval logic
        order.setId(orderId);

        Book book = new Book(); // Simplified, replace with actual book retrieval logic
        book.setId(bookId);

        OrderItem orderItem = orderItemService.createOrderItem(order, book, quantity, unitPrice);
        return ResponseEntity.ok(orderItem);
    }

    /**
     * Retrieve all items for a specific order.
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrder(@PathVariable Long orderId) {
        Order order = new Order(); // Simplified, replace with actual order retrieval logic
        order.setId(orderId);

        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrder(order);
        return ResponseEntity.ok(orderItems);
    }

    /**
     * Retrieve an order item by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        return orderItemService.getOrderItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing order item.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id,
                                                     @RequestParam Integer quantity,
                                                     @RequestParam BigDecimal unitPrice) {
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(id, quantity, unitPrice);
        return ResponseEntity.ok(updatedOrderItem);
    }

    /**
     * Delete an order item by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok("Order item deleted successfully.");
    }
}