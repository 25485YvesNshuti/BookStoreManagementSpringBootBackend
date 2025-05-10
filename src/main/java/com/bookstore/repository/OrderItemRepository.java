package com.bookstore.repository;

import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Find all items in a specific order
    List<OrderItem> findByOrder(com.bookstore.entity.Order order); // Corrected import

    // Method to find top selling books (for admin dashboard)
    @Query("SELECT oi.book, SUM(oi.quantity) AS totalQuantity " +
           "FROM OrderItem oi " +
           "GROUP BY oi.book " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingBooks(Pageable pageable);
}