package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Add or update a review for a book by a user.
     */
    @PostMapping
    public ResponseEntity<Review> createOrUpdateReview(@RequestParam Long userId,
                                                     @RequestParam Long bookId,
                                                     @RequestParam Integer rating,
                                                     @RequestParam(required = false) String comment) {
        User user = new User(); // Simplified, replace with actual user retrieval
        user.setId(userId);
        Book book = new Book(); // Simplified, replace with actual book retrieval
        book.setId(bookId);
        Review review = reviewService.createOrUpdateReview(user, book, rating, comment);
        return ResponseEntity.ok(review);
    }

    /**
     * Retrieve all reviews for a specific book with pagination.
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getReviewsByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Book book = new Book(); // Simplified, replace with actual book retrieval
        book.setId(bookId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewService.getReviewsByBook(book, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("totalPages", reviewPage.getTotalPages());
        response.put("totalElements", reviewPage.getTotalElements());
        response.put("currentPage", reviewPage.getNumber());
        response.put("pageSize", reviewPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all reviews by a specific user with pagination.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = new User(); // Simplified, replace with actual user retrieval
        user.setId(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewService.getReviewsByUser(user, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("totalPages", reviewPage.getTotalPages());
        response.put("totalElements", reviewPage.getTotalElements());
        response.put("currentPage", reviewPage.getNumber());
        response.put("pageSize", reviewPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a review for a book by a user.
     */
    @DeleteMapping
    public ResponseEntity<String> deleteReview(@RequestParam Long userId, @RequestParam Long bookId) {
        User user = new User(); // Simplified, replace with actual user retrieval
        user.setId(userId);
        Book book = new Book(); // Simplified, replace with actual book retrieval
        book.setId(bookId);
        reviewService.deleteReview(user, book);
        return ResponseEntity.ok("Review deleted successfully.");
    }

    /**
     * Global search for reviews with pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchReviewsGlobal(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewService.searchGlobal(searchTerm, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewPage.getContent());
        response.put("totalPages", reviewPage.getTotalPages());
        response.put("totalElements", reviewPage.getTotalElements());
        response.put("currentPage", reviewPage.getNumber());
        response.put("pageSize", reviewPage.getSize());
        response.put("searchTerm", searchTerm);
        return ResponseEntity.ok(response);
    }
}