package com.unir.libros.elastic.data;

import java.net.InetAddress;
import java.util.*;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import com.unir.libros.elastic.model.db.Book;
import com.unir.libros.elastic.response.AggregationDetails;
import com.unir.libros.elastic.response.BooksQueryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final BookRepository bookRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] summarySearchFields = {"summary", "summary._2gram", "summary._3gram"};
    private final String[] reviewsSearchFields = {"reviews", "reviews._2gram", "reviews._3gram"};

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Boolean delete(Book book) {
        bookRepository.delete(book);
        return Boolean.TRUE;
    }

	public Optional<Book> findById(String id) {
		return bookRepository.findById(id);
	}

    @SneakyThrows
    public BooksQueryResponse findBooks(String title, String author, String published, String isbn10, String isbn13, String summary, String reviews, String publisher, String cantidad, String image, Boolean aggregate) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(title)) {
            querySpec.must(QueryBuilders.termQuery("title", title));
        }
        if (!StringUtils.isEmpty(author)) {
            querySpec.must(QueryBuilders.termQuery("author", author));
        }
        if (!StringUtils.isEmpty(isbn10)) {
            querySpec.must(QueryBuilders.termQuery("isbn10", isbn10));
        }

        if (!StringUtils.isEmpty(published)) {
            querySpec.must(QueryBuilders.matchQuery("published", published));
        }
        
        if (!StringUtils.isEmpty(isbn13)) {
            querySpec.must(QueryBuilders.matchQuery("isbn13", isbn13));
        }
        
       
        if (!StringUtils.isEmpty(reviews)) {
            querySpec.must(QueryBuilders.matchQuery("reviews", reviews));
        }
        
        if (!StringUtils.isEmpty(publisher)) {
            querySpec.must(QueryBuilders.matchQuery("publisher", publisher));
        }
        
        if (!StringUtils.isEmpty(cantidad)) {
            querySpec.must(QueryBuilders.matchQuery("image", cantidad));
        }
        
        if (!StringUtils.isEmpty(image)) {
            querySpec.must(QueryBuilders.matchQuery("cantidad", image));
        }

        if (!StringUtils.isEmpty(summary)) {
            querySpec.must(QueryBuilders.multiMatchQuery(summary, summarySearchFields).type(Type.BOOL_PREFIX));
        }
        
        if (!StringUtils.isEmpty(reviews)) {
            querySpec.must(QueryBuilders.multiMatchQuery(reviews, reviewsSearchFields).type(Type.BOOL_PREFIX));
        }

        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("publisher Aggregation").field("publisher").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        //nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Book> result = elasticClient.search(query, Book.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("publisher Aggregation");

            //Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
            String queryParams = getQueryParams( title,  author,  published,  isbn10,  isbn13,  summary,  reviews,  publisher,  cantidad, image);
            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/books?publisher=" + bucket.getKey() + queryParams)));
        }
        return new BooksQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @return
     */
    private String getQueryParams(String title, String author, String published, String isbn10, String isbn13, String summary, String reviews, String publisher, String cantidad, String image) {
        String queryParams = (StringUtils.isEmpty(title) ? "" : "&title=" + title)
                + (StringUtils.isEmpty(author) ? "" : "&author=" + author)
                + (StringUtils.isEmpty(published) ? "" : "&published=" + published)
                + (StringUtils.isEmpty(isbn10) ? "" : "&isbn10=" + isbn10)
                + (StringUtils.isEmpty(isbn13) ? "" : "&isbn13=" + isbn13)
                + (StringUtils.isEmpty(summary) ? "" : "&summary=" + summary)
                + (StringUtils.isEmpty(reviews) ? "" : "&reviews=" + reviews)
                + (StringUtils.isEmpty(publisher) ? "" : "&publisher=" + publisher)
                + (StringUtils.isEmpty(image) ? "" : "&image=" + image)
                + (StringUtils.isEmpty(cantidad) ? "" : "&cantidad=" + cantidad);
        // Eliminamos el ultimo & si existe
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }
}
