package com.example.demo.student.service;

import com.example.demo.student.entity.Book;
import com.example.demo.student.web.BookDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BooksService {

  Flux<Book> findAll();

  Flux<Book> findByTitle(String title);

  Mono<Book> createBook(BookDto book);

}
