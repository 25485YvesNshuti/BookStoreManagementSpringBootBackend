package com.bookstore.repository;

import com.bookstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Find a category by its name
    Optional<Category> findByName(String name);

    // Check if a category exists by name
    boolean existsByName(String name);

    // Pagination
    Page<Category> findAll(Pageable pageable);

    // Global Search
    @Query("SELECT c FROM Category c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Category> searchGlobal(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count all categories
    long count();
}