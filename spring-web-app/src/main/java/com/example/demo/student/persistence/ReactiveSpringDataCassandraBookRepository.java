package com.example.demo.student.persistence;

import com.example.demo.student.entity.book.Book;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ReactiveSpringDataCassandraBookRepository extends ReactiveCassandraRepository<Book, UUID> {

  @Query(value = "SELECT * FROM books WHERE title = :title")
  Flux<Book> findByTitle(@Param("title") String title);

}
