package com.example.demo.student.service.impl;

import com.example.demo.student.entity.Book;
import com.example.demo.student.persistance.ReactiveSpringDataCassandraBookRepository;
import com.example.demo.student.service.BooksService;
import com.example.demo.student.web.BookDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ReactiveCassandraJPABooksService implements BooksService {

  private final ReactiveSpringDataCassandraBookRepository repository;

  @Autowired
  public ReactiveCassandraJPABooksService(ReactiveSpringDataCassandraBookRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<Book> findAll() {
    return repository.findAll();
  }

  @Override
  public Flux<Book> findByTitle(String title) {
    return repository.findByTitle(title);
  }

  @Override
  public Mono<Book> createBook(BookDto dto) {
    return Mono.just(dto)
        .map(d -> new Book(UUID.randomUUID(), d.getTitle()))
        .flatMap(repository::save);
  }
}
