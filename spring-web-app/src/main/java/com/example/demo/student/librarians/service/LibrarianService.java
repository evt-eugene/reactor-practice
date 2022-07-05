package com.example.demo.student.librarians.service;

import com.example.demo.student.librarians.entity.Librarian;
import com.example.demo.student.librarians.web.LibrarianDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LibrarianService {

  Flux<Librarian> findAll();

  Mono<Librarian> createLibrarian(LibrarianDto dto);

  Mono<Void> deleteLibrarian(UUID id);

}
