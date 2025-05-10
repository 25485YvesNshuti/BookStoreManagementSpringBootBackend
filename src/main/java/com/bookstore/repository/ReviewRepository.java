package com.bookstore.repository;

import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find all reviews for a specific book
    List<Review> findByBook(Book book);

    // Find all reviews by a specific user
    List<Review> findByUser(User user);

    // Find a specific review by user and book
    Optional<Review> findByUserAndBook(User user, Book book);

    // Check if a user has already reviewed a specific book
    boolean existsByUserAndBook(User user, Book book);

    // Pagination
    Page<Review> findAll(Pageable pageable);

    Page<Review> findByBook(Book book, Pageable pageable);

    Page<Review> findByUser(User user, Pageable pageable);

    // Global Search
    @Query("SELECT r FROM Review r WHERE " +
           "CAST(r.id AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(r.rating AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.user.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " + // Search by user email
           "LOWER(r.book.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")    // Search by book title
    Page<Review> searchGlobal(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count
    long count();
}