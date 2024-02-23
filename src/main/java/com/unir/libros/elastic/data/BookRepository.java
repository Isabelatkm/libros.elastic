package com.unir.libros.elastic.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.unir.libros.elastic.model.db.Book;


public interface BookRepository extends ElasticsearchRepository<Book, String> {

	List<Book> findByTitle(String title);
	
	Optional<Book> findById(String id);
	
	Book save(Book product);
	
	void delete(Book product);
	
	List<Book> findAll();
}
