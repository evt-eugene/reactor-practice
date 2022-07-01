package com.example.demo.student.service;

import com.example.demo.student.entity.Librarian;
import com.example.demo.student.web.LibrarianDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LibrarianService {

  Flux<Librarian> findAll();

  Mono<Librarian> createLibrarian(LibrarianDto dto);

}
