package com.example.demo.student.janitors.web;

import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.service.JanitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/janitors")
public class JanitorsController {

  private final JanitorService service;

  @Autowired
  public JanitorsController(JanitorService service) {
    this.service = service;
  }

  @GetMapping
  public Flux<Janitor> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Janitor>> findJanitorById(@PathVariable("id") UUID id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(notFound().build());
  }

  @PostMapping
  public Mono<ResponseEntity<Janitor>> createJanitor(@RequestBody JanitorDto dto) {
    return service.createJanitor(dto)
        .map(janitor ->
                 created(URI.create("/janitors/" + janitor.getId()))
                     .body(janitor)
        )
        .defaultIfEmpty(internalServerError().build());
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<Janitor>> updateJanitor(@PathVariable("id") UUID id, @RequestBody JanitorDto dto) {
    return service.updateJanitor(id, dto)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(notFound().build());
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteJanitor(@PathVariable("id") UUID id) {
    return service.deleteJanitor(id)
        .map(wasApplied ->
                 wasApplied ? noContent().<Void>build() : status(HttpStatus.CONFLICT).<Void>build()
        )
        .defaultIfEmpty(notFound().build());
  }

}
