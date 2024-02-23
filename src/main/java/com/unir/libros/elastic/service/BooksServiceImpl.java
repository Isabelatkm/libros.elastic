package com.unir.libros.elastic.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.unir.libros.elastic.data.DataAccessRepository;
import com.unir.libros.elastic.model.db.Book;
import com.unir.libros.elastic.request.CreateBookRequest;
import com.unir.libros.elastic.response.BooksQueryResponse;



@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

	private final DataAccessRepository repository;

	@Override
	public BooksQueryResponse getBooks(String title, String author, String published, String isbn10, String isbn13, String summary, String reviews, String publisher, String cantidad, String image, Boolean aggregate) {
		//Ahora por defecto solo devolvera books visibles
		return repository.findBooks( title,  author,  published,  isbn10,  isbn13,  summary,  reviews,  publisher,  cantidad, image, aggregate);
	}

	@Override
	public Book getBook(String bookId) {
		return repository.findById(bookId).orElse(null);
	}

	@Override
	public Boolean removeBook(String bookId) {

		Book book= repository.findById(bookId).orElse(null);
		if (book != null) {
			repository.delete(book);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Book createBook(CreateBookRequest request) {

		if (request != null && StringUtils.hasLength(request.getAuthor().trim())
				&& StringUtils.hasLength(request.getCantidad().trim())
				&& StringUtils.hasLength(request.getIsbn10().trim())
				&& StringUtils.hasLength(request.getIsbn13().trim())
				&& StringUtils.hasLength(request.getPublished().trim())
				&& StringUtils.hasLength(request.getPublisher().trim())
				&& StringUtils.hasLength(request.getReviews().trim())
				&& StringUtils.hasLength(request.getSummary().trim())
				&& StringUtils.hasLength(request.getTitle().trim())
				&& StringUtils.hasLength(request.getImage().trim())
				) {

			Book book = Book.builder().title(request.getTitle()).author(request.getAuthor())
					.cantidad(request.getCantidad()).isbn10(request.getIsbn10())
							.isbn13(request.getIsbn13()).published(request.getPublished())
									.publisher(request.getPublisher()).reviews(request.getReviews()).image(request.getImage())
											.summary(request.getSummary()).title(request.getTitle()).build();

			return repository.save(book);
		} else {
			return null;
		}
	}

}
