package com.example.demo.student.librarians.persistance;

import com.example.demo.student.librarians.entity.Librarian;
import com.example.demo.student.librarians.web.LibrarianDto;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoRepositoryBean
public interface LibrarianRepository extends Repository<Librarian, UUID> {

  Flux<Librarian> findAll();

  Mono<Librarian> findLibrarianById(UUID id);

  Mono<Librarian> insert(UUID id, LibrarianDto dto);

  Mono<Void> deleteById(UUID id);

}
