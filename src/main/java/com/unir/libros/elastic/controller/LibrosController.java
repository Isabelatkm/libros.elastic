package com.unir.libros.elastic.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unir.libros.elastic.model.db.Book;
import com.unir.libros.elastic.request.CreateBookRequest;
import com.unir.libros.elastic.response.BooksQueryResponse;
import com.unir.libros.elastic.service.BooksService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LibrosController {

	private final BooksService service;

	@GetMapping("/books")
	public ResponseEntity<BooksQueryResponse> getBooks(
			@RequestHeader Map<String, String> headers,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String published,
			@RequestParam(required = false) String isbn10,
			@RequestParam(required = false) String isbn13,
			@RequestParam(required = false) String summary,
			@RequestParam(required = false) String reviews,
			@RequestParam(required = false) String publisher,
			@RequestParam(required = false) String cantidad,
			@RequestParam(required = false) String image,
			@RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

	
		BooksQueryResponse books = service.getBooks(title,  author,  published,  isbn10,  isbn13,  summary,  reviews,  publisher,  cantidad, image, aggregate);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/books/{bookId}")
	public ResponseEntity<Book> getBook(@PathVariable String bookId) {

		Book book = service.getBook(bookId);

		if (book != null) {
			return ResponseEntity.ok(book);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<Void> deleteBook(@PathVariable String bookId) {

		Boolean removed = service.removeBook(bookId);

		if (Boolean.TRUE.equals(removed)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/books")
	public ResponseEntity<Book> getBook(@RequestBody CreateBookRequest request) {

		Book createdBook = service.createBook(request);

		if (createdBook != null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}

}
