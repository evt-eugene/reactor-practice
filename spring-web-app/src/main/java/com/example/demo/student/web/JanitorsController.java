package com.example.demo.student.web;

import com.example.demo.student.entity.janitor.Janitor;
import com.example.demo.student.service.JanitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

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
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Mono<ResponseEntity<Janitor>> createJanitor(@RequestBody JanitorDto dto) {
    return service.createJanitor(dto)
        .map(janitor -> ResponseEntity.created(URI.create("/janitors/" + janitor.getId())).body(janitor))
        .defaultIfEmpty(ResponseEntity.internalServerError().build());
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<Janitor>> updateJanitor(@PathVariable("id") UUID id, @RequestBody JanitorDto dto) {
    return service.updateJanitor(id, dto)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
