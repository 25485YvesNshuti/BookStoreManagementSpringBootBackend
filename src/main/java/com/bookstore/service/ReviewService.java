package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    /**
     * Create or update a review for a book by a user.
     */
    public Review createOrUpdateReview(User user, Book book, Integer rating, String comment) {
        Optional<Review> optionalReview = reviewRepository.findByUserAndBook(user, book);
        Review review = optionalReview.orElse(new Review());
        review.setUser(user);
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    /**
     * Retrieve all reviews for a specific book with pagination.
     */
    public Page<Review> getReviewsByBook(Book book, Pageable pageable) {
        return reviewRepository.findByBook(book, pageable);
    }

    /**
     * Retrieve all reviews for a specific book.
     */
    public List<Review> getReviewsByBook(Book book) {
        return reviewRepository.findByBook(book);
    }

    /**
     * Retrieve all reviews by a specific user with pagination.
     */
    public Page<Review> getReviewsByUser(User user, Pageable pageable) {
        return reviewRepository.findByUser(user, pageable);
    }

    /**
     * Retrieve all reviews by a specific user.
     */
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    /**
     * Retrieve a specific review by user and book.
     */
    public Optional<Review> getReviewByUserAndBook(User user, Book book) {
        return reviewRepository.findByUserAndBook(user, book);
    }

    /**
     * Delete a specific review by user and book.
     */
    public void deleteReview(User user, Book book) {
        Optional<Review> optionalReview = reviewRepository.findByUserAndBook(user, book);
        if (optionalReview.isPresent()) {
            reviewRepository.delete(optionalReview.get());
        } else {
            throw new IllegalArgumentException("Review not found for the given user and book.");
        }
    }

    /**
     * Global search for reviews with pagination.
     */
    public Page<Review> searchGlobal(String searchTerm, Pageable pageable) {
        return reviewRepository.searchGlobal(searchTerm, pageable);
    }

    /**
     * Get total review count.
     * @return
     */
    public long getTotalReviewCount(){
        return reviewRepository.count();
    }
}