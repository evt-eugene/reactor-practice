package com.example.demo.student.service;

import com.example.demo.student.entity.librarian.Librarian;
import com.example.demo.student.web.LibrarianDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LibrarianService {

  Flux<Librarian> findAll();

  Mono<Librarian> createLibrarian(LibrarianDto dto);

  Mono<Void> deleteLibrarian(UUID id);

}
