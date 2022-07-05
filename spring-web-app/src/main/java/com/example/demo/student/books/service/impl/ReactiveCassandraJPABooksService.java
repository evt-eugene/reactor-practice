package com.example.demo.student.books.service.impl;

import com.example.demo.student.books.entity.Book;
import com.example.demo.student.books.persistence.ReactiveSpringDataCassandraBookRepository;
import com.example.demo.student.books.service.BooksService;
import com.example.demo.student.books.web.BookPublishingYearDto;
import com.example.demo.student.books.web.BookTitleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Year;
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
  public Mono<Book> createBook(BookTitleDto dto) {
    return Mono.just(dto)
        .map(d -> new Book(UUID.randomUUID(), d.getTitle(), Year.now().getValue()))
        .flatMap(repository::save);
  }

  @Override
  public Flux<Book> updateBookPublishingYearByTitle(String title, BookPublishingYearDto dto) {
    var publishingYear = dto.getPublishingYear();

    return repository.findByTitle(title)
        .map(book -> {
          book.setPublishingYear(publishingYear);
          return book;
        })
        .transform(repository::saveAll);
  }
}
