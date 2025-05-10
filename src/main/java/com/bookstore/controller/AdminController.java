package com.bookstore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard-data")
    public ResponseEntity<String> getAdminDashboardData() {
        // Only accessible by users with ROLE_ADMIN
        return ResponseEntity.ok("Admin dashboard data");
    }

    // Add other admin-specific endpoints here (e.g., user management, etc.)
}