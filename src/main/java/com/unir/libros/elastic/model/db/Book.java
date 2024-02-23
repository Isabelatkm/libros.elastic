package com.unir.libros.elastic.model.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "books", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Text, name = "title")
	private String title;
	
	@Field(type = FieldType.Text, name = "author")
	private String author;
	
	@Field(type = FieldType.Text, name = "published")
	private String published;
	
	@Field(type = FieldType.Text, name = "isbn10")
	private String isbn10;

	@Field(type = FieldType.Text, name = "isbn13")
	private String isbn13;
	
	@Field(type = FieldType.Search_As_You_Type, name = "summary")
	private String summary;
	
	@Field(type = FieldType.Search_As_You_Type, name = "reviews")
	private String reviews;
	
	@Field(type = FieldType.Keyword, name = "publisher")
	private String publisher;
	
	@Field(type = FieldType.Text, name = "cantidad")
	private String cantidad;

	@Field(type = FieldType.Text, name = "image")
	private String image;
}
