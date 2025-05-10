package com.bookstore.controller;

import com.bookstore.entity.Order;
import com.bookstore.service.BookService;
import com.bookstore.service.CategoryService;
import com.bookstore.service.OrderItemService;
import com.bookstore.service.OrderService;
import com.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRegisteredUsers", userService.getAllUsers().size());
        summary.put("totalBooks", bookService.getAllBooksCount()); // Use the new method here
        summary.put("totalCategories", categoryService.getAllCategories().size());

        List<Order> allOrders = orderService.getAllOrders();
        summary.put("totalOrders", allOrders.size());

        BigDecimal totalRevenue = orderService.getTotalRevenue();
        summary.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        summary.put("newOrdersToday", orderService.getNewOrdersToday().size());

        List<com.bookstore.entity.Book> topSellingBooks = orderItemService.getTopSellingBooks(5);
        summary.put("topSellingBooks", topSellingBooks.stream().map(com.bookstore.entity.Book::getTitle).collect(Collectors.toList()));

        long totalOrdersCount = allOrders.size();
        BigDecimal averageOrderValue = (totalRevenue != null && totalOrdersCount > 0)
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrdersCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        summary.put("averageOrderValue", averageOrderValue);

        return ResponseEntity.ok(summary);
    }
}