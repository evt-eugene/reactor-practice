package com.example.demo.student.librarians.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.student.librarians.entity.Librarian;
import com.example.demo.student.librarians.persistance.LibrarianRepository;
import com.example.demo.student.librarians.service.LibrarianService;
import com.example.demo.student.librarians.web.LibrarianDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ReactiveCqlTemplateLibrarianService implements LibrarianService {

  private final LibrarianRepository repository;

  @Autowired
  public ReactiveCqlTemplateLibrarianService(LibrarianRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<Librarian> findAll() {
    return repository.findAll();
  }

  @Override
  public Mono<Librarian> findLibrarianById(UUID id) {
    return repository.findLibrarianById(id);
  }

  @Override
  public Mono<Librarian> createLibrarian(LibrarianDto dto) {
    return Mono.defer(() -> Mono.just(Uuids.timeBased()))
        .flatMap(id -> repository.insert(id, dto));
  }

  @Override
  public Mono<Void> deleteLibrarian(UUID id) {
    return repository.deleteById(id);
  }
}
