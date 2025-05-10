package com.bookstore.repository;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Find all books by category
    List<Book> findByCategory(Category category);

    // Find all books with stock greater than a specified value
    List<Book> findByStockQuantityGreaterThan(Integer stockQuantity);

    // Find all books with a specific author
    List<Book> findByAuthor(String author);

    // Pagination
    Page<Book> findAll(Pageable pageable);

    Page<Book> findByCategory(Category category, Pageable pageable);

    // Global Search
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchGlobal(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count all books
    @Query("SELECT COUNT(b) FROM Book b")
    long countAllBooks();
}