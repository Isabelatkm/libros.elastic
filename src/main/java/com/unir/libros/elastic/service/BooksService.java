package com.unir.libros.elastic.service;

import com.unir.libros.elastic.model.db.Book;
import com.unir.libros.elastic.request.CreateBookRequest;
import com.unir.libros.elastic.response.BooksQueryResponse;

public interface BooksService {

	BooksQueryResponse getBooks(String title, String author, String published, String isbn10, String isbn13, String summary, String reviews, String publisher, String cantidad, String image, Boolean aggregate);
	
	Book getBook(String bookId);
	
	Boolean removeBook(String bookId);
	
	Book createBook(CreateBookRequest request);

}
