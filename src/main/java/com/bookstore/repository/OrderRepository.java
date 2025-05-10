package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find all orders by a specific user
    List<Order> findByUser(User user);

    // Find all orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Find all orders by payment status
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);

    // Method to retrieve all orders (for admin dashboard)
    List<Order> findAll();

    // Method to calculate total revenue from all orders
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal getTotalRevenue();

    // Method to find orders created within a specific time range (for today's orders)
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Pagination
    Page<Order> findAll(Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);


    // Global Search
    @Query("SELECT o FROM `Order` o WHERE " +  // Changed to `Order`
            "CAST(o.id AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +  // Added cast
            "LOWER(o.status) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.paymentStatus) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Order> searchGlobal(@Param("searchTerm") String searchTerm, Pageable pageable);

    //count
    long count();
}