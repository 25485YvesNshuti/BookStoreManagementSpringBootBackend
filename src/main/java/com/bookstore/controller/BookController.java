package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3000")

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Create a new book.
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.ok(createdBook);
    }

    /**
     * Retrieve all books with pagination.
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookService.getAllBooks(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        response.put("totalPages", bookPage.getTotalPages());
        response.put("totalElements", bookPage.getTotalElements());
        response.put("currentPage", bookPage.getNumber());
        response.put("pageSize", bookPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a book by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieve all books by category with pagination.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getBooksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Category category = new Category();
        category.setId(categoryId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookService.getBooksByCategory(category, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        response.put("totalPages", bookPage.getTotalPages());
        response.put("totalElements", bookPage.getTotalElements());
        response.put("currentPage", bookPage.getNumber());
        response.put("pageSize", bookPage.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing book.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Book book = bookService.updateBook(id, updatedBook);
        return ResponseEntity.ok(book);
    }

    /**
     * Delete a book by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully.");
    }

    /**
     * Global search for books with pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBooksGlobal(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookService.searchGlobal(searchTerm, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("books", bookPage.getContent());
        response.put("totalPages", bookPage.getTotalPages());
        response.put("totalElements", bookPage.getTotalElements());
        response.put("currentPage", bookPage.getNumber());
        response.put("pageSize", bookPage.getSize());
        response.put("searchTerm", searchTerm); // Optionally include the search term in the response
        return ResponseEntity.ok(response);
    }
}