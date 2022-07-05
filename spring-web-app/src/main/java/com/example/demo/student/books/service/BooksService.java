package com.example.demo.student.books.service;

import com.example.demo.student.books.entity.Book;
import com.example.demo.student.books.web.BookPublishingYearDto;
import com.example.demo.student.books.web.BookTitleDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BooksService {

  Flux<Book> findAll();

  Flux<Book> findByTitle(String title);

  Mono<Book> createBook(BookTitleDto dto);

  Flux<Book> updateBookPublishingYearByTitle(String title, BookPublishingYearDto dto);

}
