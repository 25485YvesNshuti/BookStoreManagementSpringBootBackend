package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Create a new order item.
     */
    public OrderItem createOrderItem(Order order, Book book, Integer quantity, BigDecimal unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        return orderItemRepository.save(orderItem);
    }

    /**
     * Retrieve all order items for a specific order.
     */
    public List<OrderItem> getOrderItemsByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    /**
     * Retrieve an order item by its ID.
     */
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    /**
     * Update an existing order item.
     */
    public OrderItem updateOrderItem(Long id, Integer newQuantity, BigDecimal newUnitPrice) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        if (optionalOrderItem.isPresent()) {
            OrderItem orderItem = optionalOrderItem.get();
            orderItem.setQuantity(newQuantity);
            orderItem.setUnitPrice(newUnitPrice);
            orderItem.setSubtotal(newUnitPrice.multiply(BigDecimal.valueOf(newQuantity)));
            return orderItemRepository.save(orderItem);
        } else {
            throw new IllegalArgumentException("Order item not found with ID: " + id);
        }
    }

    /**
     * Delete an order item by its ID.
     */
    public void deleteOrderItem(Long id) {
        if (orderItemRepository.existsById(id)) {
            orderItemRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Order item not found with ID: " + id);
        }
    }

    /**
     * Get top selling books (for admin dashboard).
     */
    public List<Book> getTopSellingBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = orderItemRepository.findTopSellingBooks(pageable);
        return results.stream()
                .map(result -> (Book) result[0])
                .collect(Collectors.toList());
    }
}