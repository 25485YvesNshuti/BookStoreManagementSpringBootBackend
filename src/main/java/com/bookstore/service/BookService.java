package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Category;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Create a new book.
     */
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Retrieve all books with pagination.
     */
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Retrieve a book by its ID.
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Retrieve all books by category with pagination.
     */
    public Page<Book> getBooksByCategory(Category category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }

    /**
     * Retrieve all books by category (without pagination).  Consider removing this.
     */
    public List<Book> getBooksByCategory(Category category) {
        return bookRepository.findByCategory(category);
    }

    /**
     * Update an existing book.
     */
    public Book updateBook(Long id, Book updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPrice(updatedBook.getPrice());
        book.setStockQuantity(updatedBook.getStockQuantity());
        book.setDescription(updatedBook.getDescription());
        book.setCategory(updatedBook.getCategory());
        book.setPublicationDate(updatedBook.getPublicationDate());
        book.setImageUrl(updatedBook.getImageUrl()); // Ensure you update other fields if necessary
        book.setPageCount(updatedBook.getPageCount());
        book.setIsbn(updatedBook.getIsbn());
        return bookRepository.save(book);
    }

    /**
     * Delete a book by its ID.
     */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Global search for books with pagination.
     */
    public Page<Book> searchGlobal(String searchTerm, Pageable pageable) {
        return bookRepository.searchGlobal(searchTerm, pageable);
    }

    /**
     * Get the total number of books.
     */
    public long getAllBooksCount() {
        return bookRepository.countAllBooks();
    }
}